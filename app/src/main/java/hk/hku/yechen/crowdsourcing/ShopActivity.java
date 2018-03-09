package hk.hku.yechen.crowdsourcing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.adapters.HeaderBaseAdapter;
import hk.hku.yechen.crowdsourcing.adapters.NormalTailorAdapter;
import hk.hku.yechen.crowdsourcing.adapters.PullRecyclerViewListener;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.model.ShopsModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ReloadPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import hk.hku.yechen.crowdsourcing.util.SimpleItemDecorator;

/**
 * Created by yechen on 2017/12/18.
 */

public class ShopActivity extends Activity {
    private ProgressBar progressBar;
    private HeaderBaseAdapter headerBaseAdapter;
    private CommodityHandler commodityHandler;
    private List<CommodityModel> datas;
    private List<CommodityModel> newDatas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BaseAdapter adapter;
    private ImageView backGround;
    private Button launchButton;
    private TextView titleView;
    private TextView priceView;
    private int imageSRC;
    private String shopTitle;
    private String shopADD;
    private String cusADD;
    private double shopLat;
    private double cusLat;
    private double shopLng;
    private double cusLng;
    private double price;
    private String customerPhone;
    private int itemNum = 0;
    private ShopsModel shopsModel;
    private ExecutorService executorService;
    private HashMap<CommodityModel,Integer> commodities;
    public static final String SHOPADD = "SHOPADD";
    public static final String CUSTOMERADD = "CUSTOMERADD";
    public static final String SHOPLAT = "SHOPLAT";
    public static final String SHOPLNG = "SHOPLng";
    public static final String CUSLAT = "CUSLAT";
    public static final String CUSLNG = "CUSLNG";
    public static final String SHOP = "SHOP";
    public static final String CUSID="CUSID";
    private int offset = 0;
    private int num = COUNT;
    private static final int COUNT = 10;

    private class CommodityHandler extends Handler{
        Context context;
        public CommodityHandler(Context context){
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NetworkPresenter.SHOP_RELOAD:
                    executorService.submit(
                            new ReloadPresenter<>(datas,
                                    newDatas,
                                    commodityHandler,
                                    NetworkPresenter.COMMODITY_SHOP_SUCCESS));
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case PullRecyclerViewListener.SHOW_TAILER:
                    headerBaseAdapter.showHeader(true);
                    getData(shopsModel);
                    headerBaseAdapter.notifyDataSetChanged();
                    break;
                case NetworkPresenter.COMMODITY_SHOP_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    headerBaseAdapter.notifyDataSetChanged();
                    offset = num;
                    num = num + COUNT;
                    headerBaseAdapter.showHeader(false);
                    break;
                case NetworkPresenter.H_FAIL:
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context,"Network Error",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.shop_activity_layout);
        executorService = Executors.newSingleThreadExecutor();
        commodityHandler = new CommodityHandler(this);
        commodities = new HashMap<>();
        launchButton = (Button) findViewById(R.id.btn_buy);
        titleView = (TextView) findViewById(R.id.tv_shop_title);
        priceView = (TextView) findViewById(R.id.tv_order_price);
        progressBar = (ProgressBar) findViewById(R.id.pb_commodity);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_commodity);
        swipeRefreshLayout.setColorSchemeColors(
                Color.GREEN,
                Color.BLUE,
                Color.CYAN);
        price = 0.0;
        getDataFromMain();

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commodities.size() == 0){
                    Toast.makeText(ShopActivity.this, "Oops,You didn't select anything", Toast.LENGTH_SHORT).show();
                    return;
                }
                submit();
                //TODO: trans orderModel to OrderActivity
                OrderModel orderModel = new OrderModel(0,customerPhone,commodities,
                        new LatLng(shopLat,shopLng),new LatLng(cusLat,cusLng),
                        shopsModel.getPysicalAdd(),cusADD);
                orderModel.setPrice(price);
                Intent intent = new Intent(ShopActivity.this,OrderActivity.class);
                intent.putExtra(OrderActivity.ORDER,orderModel);
                startActivity(intent);
            }
        });
        datas = new ArrayList<>();
        customizeActionBar();
     //   getData(null);

        if(imageSRC != -1) {
            backGround = (ImageView) findViewById(R.id.iv_shop_back);
            Glide.with(this).load(imageSRC).into(backGround);
        }
        if(shopTitle != null){
            titleView.setText(shopTitle);
        }
        adapter = new BaseAdapter<CommodityModel>(datas){

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.in_shop_layout;
            }

            @Override
            public void convert(final CommodityModel data, final GeneralViewHolder viewHolder, int position) {
                viewHolder.setTextView(R.id.tv_item_name,data.getName());
                viewHolder.setTextView(R.id.tv_price,"$"+data.getPrice());
                Glide.with(ShopActivity.this).load(data.getImgURL()).into((ImageView) viewHolder.getView(R.id.iv_item_pic));
            //    viewHolder.setImageView(ShopActivity.this,R.id.iv_item_pic,data.getImgID());
                final TextView itemNumTextView = viewHolder.getView(R.id.tv_item_num);
                final int limit = data.getAvaNum();
                viewHolder.setTextView(R.id.tv_stock,"Stock: "+String.valueOf(limit));
                viewHolder.setChildListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemNum = Integer.valueOf(itemNumTextView.getText().toString());
                        if(itemNum < limit) {
                            itemNum++;
                            itemNumTextView.setText(String.valueOf(itemNum));
                            commodities.put(data,itemNum);
                            price += data.getPrice();
                            priceView.setText(String.valueOf("$"+price));
                        }
                    }
                },R.id.ib_add);
                viewHolder.setChildListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int limit = 0;
                        itemNum = Integer.valueOf(itemNumTextView.getText().toString());
                        if(itemNum > limit) {
                            itemNum --;
                            itemNumTextView.setText(String.valueOf(itemNum));
                            if(itemNum == 0)
                                commodities.remove(data);
                            else
                                commodities.put(data,itemNum);
                            price -= data.getPrice();
                            priceView.setText(String.valueOf("$"+price));
                        }
                    }
                },R.id.ib_minus);
            }
        };
        headerBaseAdapter = new NormalTailorAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadPictures(shopsModel);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rcv_commodity_list);
        recyclerView.setAdapter(headerBaseAdapter);
        recyclerView.addOnScrollListener(new PullRecyclerViewListener(this,commodityHandler,true));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        getData(shopsModel);
    }
    private void reloadPictures(ShopsModel shop){
        newDatas = new ArrayList<>();
        NetworkPresenter networkPresenter  = new NetworkPresenter(
                NetworkPresenter.SHOP_RELOAD,
                NetworkPresenter.UrlBuilder.buildCommodityInShop(shop.getId(),0,COUNT),
                null,
                commodityHandler,
                new ResponseExtractor.CommodityExtractor(newDatas,commodityHandler)
        );
        executorService.submit(networkPresenter);
    }
    private void getData(ShopsModel shop){

        NetworkPresenter networkPresenter = new NetworkPresenter(
                NetworkPresenter.COMMODITY_SHOP_SUCCESS,
                NetworkPresenter.UrlBuilder.buildCommodityInShop(shop.getId(),offset,num),
                null,
                commodityHandler,
                new ResponseExtractor.CommodityExtractor(datas,commodityHandler)
        );
        executorService.submit(networkPresenter);
    }

    void customizeActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.actionbar_style, null);
        actionbarLayout.findViewById(R.id.btn_drawer_switcher).setVisibility(View.GONE);
        getActionBar().setCustomView(actionbarLayout);
    }
    private void getDataFromMain(){
        Intent intent = getIntent();
        if(intent == null)
            return;
        shopsModel = intent.getParcelableExtra(SHOP);
        shopTitle = shopsModel.toString();
        shopADD = intent.getStringExtra(SHOPADD);
        cusADD = intent.getStringExtra(CUSTOMERADD);
        shopLat = intent.getDoubleExtra(SHOPLAT,0.0);
        shopLng = intent.getDoubleExtra(SHOPLNG,0.0);
        cusLat = intent.getDoubleExtra(CUSLAT,0.0);
        cusLng = intent.getDoubleExtra(CUSLNG,0.0);
        customerPhone = intent.getStringExtra(CUSID);
    }
    public void GoBack(View view){
        finish();
    }

    //TODO:request order from server
    private void submit(){};
}
