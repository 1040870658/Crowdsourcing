package hk.hku.yechen.crowdsourcing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;

/**
 * Created by yechen on 2018/1/9.
 */

public class OrderDetailActivity extends Activity {
    public static final String ORDER_AUG = "OrderDetail";
    private TextView addressText;
    private String address= "";
    private Button backButton;
    private OrderModel orderModel;
    private List<CommodityModel> description;
    private TextView shopAddressView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm_general);
        customizeActionBar();
        backButton = (Button) findViewById(R.id.btn_throw);
        shopAddressView = (TextView) findViewById(R.id.tv_confirm_shop);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getDataFromShop();
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
    private class PickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OrderDetailActivity.this,MainActivity.class);
            intent.putExtra("tabs",3);
            startActivity(intent);
            finish();
        }
    }
    private void getDataFromShop(){
        Intent intent = getIntent();
        if(intent == null) {
            return;
        }
        orderModel = getIntent().getParcelableExtra(OrderActivity.ORDER);
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
        }
    }
}