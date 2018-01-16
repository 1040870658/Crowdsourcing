package hk.hku.yechen.crowdsourcing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.util.SimpleItemDecorator;

/**
 * Created by yechen on 2017/12/18.
 */

public class ShopActivity extends Activity {
    private List<CommodityModel> datas;
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
    private int itemNum = 0;
    private HashMap<CommodityModel,Integer> commodities;
    public static final String BACKIMAGE = "IMAGEID";
    public static final String SHOPADD = "SHOPADD";
    public static final String CUSTOMERADD = "CUSTOMERADD";
    public static final String SHOPTITLE = "SHOPTITLE";
    public static final String SHOPLAT = "SHOPLAT";
    public static final String SHOPLNG = "SHOPLng";
    public static final String CUSLAT = "CUSLAT";
    public static final String CUSLNG = "CUSLNG";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.shop_activity_layout);
        commodities = new HashMap<>();
        launchButton = (Button) findViewById(R.id.btn_buy);
        titleView = (TextView) findViewById(R.id.tv_shop_title);
        priceView = (TextView) findViewById(R.id.tv_order_price);
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
                OrderModel orderModel = new OrderModel(0,commodities,
                        new LatLng(shopLat,shopLng),new LatLng(cusLat,cusLng),
                        "香港大学西营盘大学图书馆大楼旧翼地下","香港大学图书馆");
                orderModel.setPrice(price);
                Intent intent = new Intent(ShopActivity.this,OrderActivity.class);
                intent.putExtra(OrderActivity.ORDER,orderModel);
       //         intent.putExtra(SHOPADD,shopADD);
       //         intent.putExtra(CUSTOMERADD,cusADD);
                startActivity(intent);
            }
        });
        customizeActionBar();
        getData(null);

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
                viewHolder.setImageView(ShopActivity.this,R.id.iv_item_pic,data.getImgID());
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
                            priceView.setText("$"+price);
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
                            commodities.put(data,itemNum);
                            price -= data.getPrice();
                            priceView.setText("$"+price);
                        }
                    }
                },R.id.ib_minus);
            }
        };
        recyclerView = (RecyclerView) findViewById(R.id.rcv_commodity_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleItemDecorator(this,SimpleItemDecorator.VERTICAL_LIST));
    }
    private void getData(String shopID){
        datas = new ArrayList<>();
        datas.add(new CommodityModel("American Blueberry Scone",50,R.drawable.american_blueberry_scone,12));
        datas.add(new CommodityModel("Chicken Mushroom Pie",40,R.drawable.chicken_mushroom_pie,20));
        datas.add(new CommodityModel("Cinnamon Danish",30,R.drawable.cinnamon_danish,100));
        datas.add(new CommodityModel("French Butter Croissant",35,R.drawable.croque_monsieuer,50));
        datas.add(new CommodityModel("Sausage Roll",38,R.drawable.sausage_roll,32));
        datas.add(new CommodityModel("Croque Monsieuer",42,R.drawable.french_butter_croissant,11));
        datas.add(new CommodityModel("Mushroom Cheese Pocket",42,R.drawable.mushroom_cheese_pocket,16));
        datas.add(new CommodityModel("Chicken Mushroom Pie",40,R.drawable.chicken_mushroom_pie,67));
        datas.add(new CommodityModel("Cinnamon Danish",30,R.drawable.cinnamon_danish,33));
        datas.add(new CommodityModel("French Butter Croissant",35,R.drawable.croque_monsieuer,76));
        datas.add(new CommodityModel("Sausage Roll",38,R.drawable.sausage_roll,3));
        datas.add(new CommodityModel("American Blueberry Scone",50,R.drawable.american_blueberry_scone,65));
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
        imageSRC = intent.getIntExtra(BACKIMAGE,-1);
        shopTitle = intent.getStringExtra(SHOPTITLE);
        shopADD = intent.getStringExtra(SHOPADD);
        cusADD = intent.getStringExtra(CUSTOMERADD);
        shopLat = intent.getDoubleExtra(SHOPLAT,0.0);
        shopLng = intent.getDoubleExtra(SHOPLNG,0.0);
        cusLat = intent.getDoubleExtra(CUSLAT,0.0);
        cusLng = intent.getDoubleExtra(CUSLNG,0.0);
    }
    public void GoBack(View view){
        finish();
    }

    //TODO:request order from server
    private void submit(){};
}
