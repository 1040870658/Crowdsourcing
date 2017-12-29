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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import hk.hku.yechen.crowdsourcing.util.SimpleItemDecorator;

/**
 * Created by yechen on 2017/12/18.
 */

public class ShopActivity extends Activity {
    private List<CommodityModel> datas;
    private RecyclerView recyclerView;
    private BaseAdapter adapter;
    private ImageView backGround;
    private Button button;
    public static final String BACKIMAGE = "IMAGEID";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.shop_activity_layout);
        button = (Button) findViewById(R.id.btn_buy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this,OrderActivity.class);
                startActivity(intent);
            }
        });
        customizeActionBar();
        String imageSRC = getIntent().getStringExtra(BACKIMAGE);
        getData();

        if(imageSRC != null) {
            backGround = (ImageView) findViewById(R.id.iv_shop_back);
            Glide.with(this).load(imageSRC).into(backGround);
        }
        adapter = new BaseAdapter<CommodityModel>(datas){

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.in_shop_layout;
            }

            @Override
            public void convert(CommodityModel data, GeneralViewHolder viewHolder, int position) {
                viewHolder.setTextView(R.id.tv_item_name,data.getName());
                viewHolder.setTextView(R.id.tv_price,"$"+data.getPrice());
                viewHolder.setImageView(ShopActivity.this,R.id.iv_item_pic,data.getImgID());
            }
        };
        recyclerView = (RecyclerView) findViewById(R.id.rcv_commodity_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleItemDecorator(this,SimpleItemDecorator.VERTICAL_LIST));
    }
    private void getData(){
        datas = new ArrayList<>();
        datas.add(new CommodityModel("American Blueberry Scone",50,R.drawable.american_blueberry_scone));
        datas.add(new CommodityModel("Chicken Mushroom Pie",40,R.drawable.chicken_mushroom_pie));
        datas.add(new CommodityModel("Cinnamon Danish",30,R.drawable.cinnamon_danish));
        datas.add(new CommodityModel("French Butter Croissant",35,R.drawable.croque_monsieuer));
        datas.add(new CommodityModel("Sausage Roll",38,R.drawable.sausage_roll));
        datas.add(new CommodityModel("Croque Monsieuer",42,R.drawable.french_butter_croissant));
        datas.add(new CommodityModel("Mushroom Cheese Pocket",42,R.drawable.mushroom_cheese_pocket));
        datas.add(new CommodityModel("Chicken Mushroom Pie",40,R.drawable.chicken_mushroom_pie));
        datas.add(new CommodityModel("Cinnamon Danish",30,R.drawable.cinnamon_danish));
        datas.add(new CommodityModel("French Butter Croissant",35,R.drawable.croque_monsieuer));
        datas.add(new CommodityModel("Sausage Roll",38,R.drawable.sausage_roll));
        datas.add(new CommodityModel("American Blueberry Scone",50,R.drawable.american_blueberry_scone));
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
}
