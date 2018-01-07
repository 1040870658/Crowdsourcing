package hk.hku.yechen.crowdsourcing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yechen on 2017/12/21.
 */

public class OrderActivity extends Activity {
    private TextView addressText;
    private String address= "";
    private Button payButton;
    private String shopADD;
    private String cusADD;
    private TextView shopAddressView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm_layout);
        customizeActionBar();
        payButton = (Button) findViewById(R.id.btn_pay);
        payButton.setOnClickListener(new PayListener());
        getDataFromShop();
        addressText = (TextView) findViewById(R.id.tv_confirm_address);
        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this,MapsActivity.class);
                startActivityForResult(intent,16);
            }
        });
        shopAddressView = (TextView) findViewById(R.id.tv_confirm_shop);
        if(shopADD != null){
            shopAddressView.setText(shopADD);
        }
        if(cusADD != null){
            addressText.setText(cusADD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        address = data.getStringExtra("address");
        addressText.setText(address);
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
    public void GoBack(View view){
        finish();
    }
    private class PayListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OrderActivity.this,MainActivity.class);
            intent.putExtra("tabs",2);
            startActivity(intent);
            finish();
        }
    }
    private void getDataFromShop(){
        Intent intent = getIntent();
        if(intent == null)
            return;
        shopADD = intent.getStringExtra(ShopActivity.SHOPADD);
        cusADD = intent.getStringExtra(ShopActivity.CUSTOMERADD);
    }
}
