package hk.hku.yechen.crowdsourcing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.haha.perflib.Main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;

/**
 * Created by yechen on 2018/1/9.
 */

public class OrderDetailActivity extends Activity {
    public static final String ORDER_AUG = "OrderDetail";
    protected TextView addressText;
    protected String address= "";
    protected Button backButton;
    protected OrderModel orderModel;
    protected List<CommodityModel> description;
    protected TextView shopAddressView;
    protected TextView tipText;
    protected TextView totalText;
    protected TextView priceText;
    protected TextView phoneText;
    protected TextView nameText;
    protected RecyclerView recyclerView;
    protected String name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm_general);
        customizeActionBar();
        addressText = (TextView) findViewById(R.id.tv_confirm_address);
        backButton = (Button) findViewById(R.id.btn_throw);
        shopAddressView = (TextView) findViewById(R.id.tv_confirm_shop);
        tipText = (TextView) findViewById(R.id.tv_tips_price);
        totalText = (TextView) findViewById(R.id.tv_total_price);
        priceText = (TextView) findViewById(R.id.tv_order_confirm_price);
        recyclerView = (RecyclerView) findViewById(R.id.rcv_order_confirm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nameText = (TextView) findViewById(R.id.tv_order_confirm_name);
        phoneText = (TextView) findViewById(R.id.tv_order_confirm_tel);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        address = data.getStringExtra("address");
        addressText.setText(address);
    }

    void customizeActionBar() {
        if(getActionBar() == null)
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

    protected class PickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OrderDetailActivity.this,MainActivity.class);
            intent.putExtra("tabs",3);
            startActivity(intent);
            finish();
        }
    }

    protected void getDataFromShop(){
        Intent intent = getIntent();
        if(intent == null) {
            return;
        }
        orderModel = intent.getParcelableExtra(ORDER_AUG);

        if(orderModel != null){
            shopAddressView.setText(orderModel.getShopAdd());
            addressText.setText(orderModel.getTargetAdd());
            description = new ArrayList<>();
            CommodityModel tmp;
            HashMap<CommodityModel,Integer> commodityMap = orderModel.getCommodityMap();
            for(Map.Entry<CommodityModel,Integer> entry:commodityMap.entrySet()){
                tmp = entry.getKey();
                description.add(tmp);
            }
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String value = decimalFormat.format(orderModel.getPrice());
            priceText.setText(String.valueOf("$ "+value));
            value = decimalFormat.format(orderModel.getTips());
            tipText.setText(String.valueOf("$ "+value));
            String total = decimalFormat.format(orderModel.getPrice() + orderModel.getTips());
            totalText.setText(String.valueOf("$ "+ total));
            phoneText.setText(orderModel.getCustomerPhone());
            nameText.setText(orderModel.getCustomerName());
        }
    }
}