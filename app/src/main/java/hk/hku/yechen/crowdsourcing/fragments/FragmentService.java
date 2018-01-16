package hk.hku.yechen.crowdsourcing.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.ShopActivity;
import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.util.AdHandler;
import hk.hku.yechen.crowdsourcing.util.AdPageChangedListener;

/**
 * Created by yechen on 2017/11/21.
 */

public class FragmentService extends Fragment {
    private static final  int ADV_NUM = 5;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private View contentView;
    private BaseAdapter<Object[]> adapter;
    private List shops = new ArrayList();
    private List descriptions = new ArrayList();
    private List<Object[]> shopDatas = new ArrayList();
    private List<View> adViews;
    String originLatLng;
    String destinationLatLng;
    String originAddress;
    String destinationAddress;
    private final int[] resources = {R.drawable.mcdonald,
            R.drawable.seven_11,
            R.drawable.starbucks,
            R.drawable.wellcome,
            R.drawable.jhceshop,
            R.drawable.zhouheiya,
            R.drawable.kfc,
            R.drawable.seven_11,
            R.drawable.hagendas,
            R.drawable.wellcome,
            R.drawable.mx,
            R.drawable.park
    };
    private final String[] description = {
            "McDonald\t   1.1km\t   About 20mins",
            "7-Eleven\t   1.3km\t   About 20mins",
            "Starbucks\t  0.5km\t   About 15mins",
            "Wellcome\t   2.3km\t   About 25mins",
            "Jhceshop\t   5.3km\t   More than 1h",
            "ZhouHaYA\t   4.4km\t   About 35mins",
            " KFC\t       3.2km\t   About 30mins",
            "7-Eleven\t   0.3km\t   About 10mins",
            "Haagen-Dazs\t  6.5km\t More than 1h",
            "Wellcome\t   3.6km\t   About 40mins",
            "MX\t   7.3km\t   More than 1h",
            "PARKnSHOP\t   6.6km\t  More than 1h",
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.shop_layout,container,false);
            imageView = (ImageView) contentView.findViewById(R.id.iv_service_back);
            Glide.with(getActivity()).load(R.drawable.back_shop).into(imageView);
            readUserInfo();
            initAdvList(contentView);
            initShopList(contentView);
        }
        return contentView;
    }
    public void initShopList(View view){
        loadPictures();
        recyclerView = (RecyclerView) view.findViewById(R.id.rcv_shops);
        shopDatas = new ArrayList<>();
        for(int i = 0;i < shops.size();i ++){
            Object[] objects = new Object[2];
            objects[0] = shops.get(i);
            objects[1] = descriptions.get(i);
            shopDatas.add(objects);
        }
        final View.OnClickListener enterShop = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent);
            }
        };
        adapter = new BaseAdapter<Object[]>(shopDatas) {

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.shop_item;
            }

            @Override
            public void convert(Object[] data, GeneralViewHolder viewHolder, int position) {
                viewHolder.setImageView(R.id.iv_shop, (Drawable)data[0]);
                viewHolder.setTextView(R.id.tv_description, (String) data[1]);
                viewHolder.setListener(new ShopListener((String)data[1],resources[position]));
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }
    public void initAdvList(View view){
        viewPager = (ViewPager) view.findViewById(R.id.vp_adv);
        adViews = new ArrayList<>(ADV_NUM);
        loadAdv(LayoutInflater.from(view.getContext()).inflate(R.layout.ad_item_layout,viewPager,false),R.drawable.ad_1);
        loadAdv(LayoutInflater.from(view.getContext()).inflate(R.layout.ad_item_layout,viewPager,false),R.drawable.ad_2);
        loadAdv(LayoutInflater.from(view.getContext()).inflate(R.layout.ad_item_layout,viewPager,false),R.drawable.ad_3);
        loadAdv(LayoutInflater.from(view.getContext()).inflate(R.layout.ad_item_layout,viewPager,false),R.drawable.ad_4);
        loadAdv(LayoutInflater.from(view.getContext()).inflate(R.layout.ad_item_layout,viewPager,false),R.drawable.ad_5);

        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return adViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if(getCount() != 0) {
                    container.addView(adViews.get(position));
                    return adViews.get(position);
                }
                else{
                    return null;
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(adViews.get(position));
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        Handler adHandler = new AdHandler(viewPager,ADV_NUM);
        adHandler.sendEmptyMessage(AdHandler.ROLLING);
        viewPager.addOnPageChangeListener(new AdPageChangedListener(adHandler));
    }
    private void loadPictures(){
        for(int i = 0;i < resources.length;i ++){
            Drawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),resources[i]));
            shops.add(drawable);
            descriptions.add(description[i]);
        }
    }
    private void loadAdv(View view,int pic){
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_ad);
        Glide.with(getActivity()).load(pic).into(imageView);
        adViews.add(view);
    }
    private class ShopListener implements View.OnClickListener {

        private String name;
        private int backgroundID;
        public ShopListener(String name,int backgroundID){
            this.name = name;
            this.backgroundID = backgroundID;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ShopActivity.class);
            intent.putExtra(ShopActivity.SHOPTITLE,name);
            intent.putExtra(ShopActivity.BACKIMAGE,backgroundID);
            intent.putExtra(ShopActivity.SHOPADD,originAddress);
            intent.putExtra(ShopActivity.CUSTOMERADD,destinationAddress);
            String[] strings = originLatLng.split(",");
            double latD = Double.valueOf(strings[0]);
            double lngD = Double.valueOf(strings[1]);
            intent.putExtra(ShopActivity.SHOPLAT,latD);
            intent.putExtra(ShopActivity.SHOPLNG,lngD);
            strings = destinationLatLng.split(",");
            latD = Double.valueOf(strings[0]);
            lngD = Double.valueOf(strings[1]);
            intent.putExtra(ShopActivity.CUSLAT,latD);
            intent.putExtra(ShopActivity.CUSLNG,lngD);
            startActivity(intent);
        }
    }
    private void readUserInfo(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.shared_table, Context.MODE_PRIVATE);
        if(sharedPreferences == null)
            return;
        originLatLng = sharedPreferences.getString(MainActivity.shared_originLatLng,null);
        destinationLatLng = sharedPreferences.getString(MainActivity.shared_desLatLng,null);
        originAddress = sharedPreferences.getString(MainActivity.shared_originAddress,null);
        destinationAddress = sharedPreferences.getString(MainActivity.shared_desAddress,null);
    }
}
