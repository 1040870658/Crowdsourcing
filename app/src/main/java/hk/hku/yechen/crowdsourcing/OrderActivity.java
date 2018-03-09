package hk.hku.yechen.crowdsourcing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.model.UserModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2017/12/21.
 */

public class OrderActivity extends Activity {
    public static final String ORDER = "ORDER_DETAIL";
    private TextView phoneText;
    private TextView cusNameText;
    private TextView addressText;
    private TextView priceText;
    private String address= "";
    private Button payButton;
    private TextView tipsText;
    private TextView totalText;
    private TextView shopAddressView;
    private OrderModel orderModel;
    private RecyclerView recyclerView;
    private List<String> invalidItemNames;
    List<CommodityModel> description;
    OrderHandler orderHandler;
    float alpha;
    private View backgroundView;
    private ProgressBar progressBar;
    public static final int START=0x00000001;
    public static final int LAUNCH_SUCCESS = 0x00001000;
    public static final int NUMBER_EXCEEDED = 0x000010001;
    public static final int LAUNCH_UNEXPECTED = 0x00001002;
    public static final int INSUFFICIENT_BALANCE = 0x000010003;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm_layout);
        customizeActionBar();
        orderHandler = new OrderHandler();
        invalidItemNames = new ArrayList<>();
        payButton = (Button) findViewById(R.id.btn_pay);
        payButton.setOnClickListener(new PayListener());
        addressText = (TextView) findViewById(R.id.tv_confirm_address);
        priceText = (TextView) findViewById(R.id.tv_order_confirm_price);
        tipsText = (TextView) findViewById(R.id.tv_tips_price);
        totalText = (TextView) findViewById(R.id.tv_total_price);
        phoneText = (TextView) findViewById(R.id.tv_order_confirm_tel);
        cusNameText = (TextView) findViewById(R.id.tv_order_confirm_name);
        progressBar = (ProgressBar) findViewById(R.id.pb_order_confirm);
        backgroundView = findViewById(R.id.ll_order_confirm);
        alpha = backgroundView.getAlpha();

        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this,MapsActivity.class);
                startActivityForResult(intent,16);
            }
        });
        shopAddressView = (TextView) findViewById(R.id.tv_confirm_shop);
        recyclerView = (RecyclerView) findViewById(R.id.rcv_order_confirm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getDataFromShop();
        recyclerView.setAdapter(new BaseAdapter<CommodityModel>(description) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.order_description;
            }

            @Override
            public void convert(CommodityModel data, GeneralViewHolder viewHolder, int position) {
                viewHolder.setTextView(R.id.tv_order_description,data.getName());
                viewHolder.setTextView(R.id.tv_order_description_number,"x"+orderModel.getCommodityMap().get(data));
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            address = data.getStringExtra("address");
            addressText.setText(address);
        }
    }

    void customizeActionBar() {

        if (getActionBar() == null)
            return;

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.actionbar_style, null);
        actionbarLayout.findViewById(R.id.btn_drawer_switcher).setVisibility(View.GONE);
        getActionBar().setCustomView(actionbarLayout);
    }
    public void GoBack(View view){
        finish();
    }
    private class PayListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            backgroundView.setAlpha(0.5f);
            orderHandler.sendEmptyMessageDelayed(START,1500);
        }
    }
    private void getDataFromShop(){

        Intent intent = getIntent();

        if(intent == null) {
            return;
        }

        orderModel = getIntent().getParcelableExtra(ORDER);
        if(orderModel != null){
            shopAddressView.setText(orderModel.getShopAdd());
            addressText.setText(orderModel.getTargetAdd());
            priceText.setText(String.valueOf("$"+orderModel.getPrice()));
            UserModel userModel = MainActivity.userModel;
            if(userModel != null){
                phoneText.setText(userModel.getPhone());
                cusNameText.setText(userModel.getUserName());
            }
            description = new ArrayList<>();
            CommodityModel tmp;
            HashMap<CommodityModel,Integer> commodityMap = orderModel.getCommodityMap();
            for(Map.Entry<CommodityModel,Integer> entry:commodityMap.entrySet()){
                tmp = entry.getKey();
                description.add(tmp);
            }
            tipsText.setText(String.valueOf("$"+orderModel.getTips()));
            totalText.setText(String.valueOf("$"+(orderModel.getTips() + orderModel.getPrice())));
        }
    }

    private class OrderHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.INVISIBLE);
            backgroundView.setAlpha(alpha);
            switch (msg.what){

                case START:
                    invalidItemNames = new ArrayList<>();
                    NetworkPresenter networkPresenter = new NetworkPresenter(
                            LAUNCH_SUCCESS,
                            NetworkPresenter.UrlBuilder.buildOrderLaunch(orderModel.getCustomerPhone(),
                                    orderModel.getCommodityMap(),
                                    orderModel.getTargetAdd(),
                                    orderModel.getEndLat()+","+orderModel.getEndLng()),
                            null,orderHandler,new ResponseExtractor.LaunchExtractor(invalidItemNames,orderHandler)
                    );
                    new Thread(networkPresenter).start();
                    break;
                case LAUNCH_SUCCESS:
                    Toast.makeText(OrderActivity.this,"Order Launch Successfully!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderActivity.this,MainActivity.class);
                    intent.putExtra("tabs",2);
                    startActivity(intent);
                    finish();
                    break;

                case LAUNCH_UNEXPECTED:
                    Toast.makeText(OrderActivity.this,
                            "Ooops,something wrong with the remote server,try again later",Toast.LENGTH_SHORT).show();
                    break;

                case NUMBER_EXCEEDED:

                    StringBuilder names = new StringBuilder();
                    boolean started = false;
                    for(String name : invalidItemNames){
                        if(started)
                            names.append("\n");
                        names.append(name);
                        started = true;
                    }
                    String warning = "Some items have been sold out just now and order launch failed.\n" +
                            " You will be forward to the shop\n\n" + "Item names:"+names;
                    new AlertDialog.Builder(OrderActivity.this)
                            .setTitle("Order Launch Failed")
                            .setMessage(warning)
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            finish();
                                        }
                                    }).show();
                    break;
                case INSUFFICIENT_BALANCE:
                    Toast.makeText(OrderActivity.this,"Insufficient balance in your account",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(OrderActivity.this,"Unexpected Errors occur",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
