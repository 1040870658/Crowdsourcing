package hk.hku.yechen.crowdsourcing.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.ShopActivity;
import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.adapters.HeaderBaseAdapter;
import hk.hku.yechen.crowdsourcing.adapters.NormalTailorAdapter;
import hk.hku.yechen.crowdsourcing.adapters.PullRecyclerViewListener;
import hk.hku.yechen.crowdsourcing.model.ShopsModel;
import hk.hku.yechen.crowdsourcing.presenter.LocationPresenter;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ReloadPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.AdHandler;
import hk.hku.yechen.crowdsourcing.util.AdPageChangedListener;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2017/11/21.
 */

public class FragmentService extends Fragment {
    private static final int LOCATION_GET = 0x1000000;
    private static final int LOCATION_RETRY = 0x1000001;
    private static final  int ADV_NUM = 5;
    private List<ShopsModel> shopsModels;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private View contentView;
    private Handler shopHandler;
    private BaseAdapter<ShopsModel> adapter;
    private HeaderBaseAdapter headerBaseAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<View> adViews;
    String originLatLng;
    String destinationLatLng;
    String originAddress;
    String destinationAddress;
    private String currentLatLng;
    private LatLng latLng;
    private int offset = 1;
    private int num = COUNT;
    private static final int COUNT = 10;
    private List<ShopsModel> newDatas;
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
            shopsModels = new ArrayList<>();
            imageView = (ImageView) contentView.findViewById(R.id.iv_service_back);
            Glide.with(getActivity()).load(R.drawable.back_shop).into(imageView);
            progressBar = (ProgressBar) contentView.findViewById(R.id.pb_shop);
            shopHandler = new ShopHandler();
            readUserInfo();
            initAdvList(contentView);
            initShopList(contentView);
        }
        return contentView;
    }
    public void initShopList(final View view){
        latLng = LocationPresenter.getCurrentLatLng(getActivity());
        if(latLng == null){
            shopHandler.sendEmptyMessageDelayed(LOCATION_RETRY,1000);
        }
        else{
            shopHandler.sendEmptyMessage(LOCATION_GET);
        }
   //     loadPictures();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_shop);
        swipeRefreshLayout.setColorSchemeColors(
                Color.GREEN,
                Color.BLUE,
                Color.CYAN);
        recyclerView = (RecyclerView) view.findViewById(R.id.rcv_shops);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadPictures();
            }
        });

        final View.OnClickListener enterShop = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent);
            }
        };
        adapter = new BaseAdapter<ShopsModel>(shopsModels) {

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.shop_item;
            }

            @Override
            public void convert(ShopsModel data, GeneralViewHolder viewHolder, int position) {
                Glide.with(FragmentService.this).load(data.getImage()).into((ImageView)viewHolder.getView(R.id.iv_shop));
                viewHolder.setTextView(R.id.tv_description,data.toString());
                viewHolder.setListener(new ShopListener(data));
            }

        };
        headerBaseAdapter = new NormalTailorAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(headerBaseAdapter);
        recyclerView.addOnScrollListener(new PullRecyclerViewListener(getActivity(),shopHandler,true));
    }
    private class ShopHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NetworkPresenter.SHOP_RELOAD:
                    ((MainActivity)getActivity()).getExecutorService().submit(
                                    new ReloadPresenter<>(shopsModels,
                                            newDatas,
                                            shopHandler,
                                            NetworkPresenter.SHOP_SUCCESS));
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case PullRecyclerViewListener.SHOW_TAILER:
                    headerBaseAdapter.showHeader(true);
                    headerBaseAdapter.notifyDataSetChanged();
                    loadPictures();
                    break;
                case NetworkPresenter.SHOP_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    headerBaseAdapter.showHeader(false);
                    headerBaseAdapter.notifyDataSetChanged();
                    offset = num;
                    num = num + COUNT;
                    break;
                case LOCATION_GET:
                    currentLatLng = latLng.latitude+","+latLng.longitude;
                    loadPictures();
                    break;
                case LOCATION_RETRY:
                    sendEmptyMessageDelayed(LOCATION_RETRY,1000);
                    break;
                default:
                    swipeRefreshLayout.setRefreshing(false);
            }
        }
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

        NetworkPresenter networkPresenter  = new NetworkPresenter(
                NetworkPresenter.SHOP_SUCCESS,
                NetworkPresenter.UrlBuilder.buildShopList(offset,num,currentLatLng),
                null,
                shopHandler,
                new ResponseExtractor.ShopExtractor(shopsModels,shopHandler)
        );
        ((MainActivity)getActivity()).getExecutorService().submit(networkPresenter);
    }

    private void reloadPictures(){
        newDatas = new ArrayList<>();
        NetworkPresenter networkPresenter  = new NetworkPresenter(
                NetworkPresenter.SHOP_RELOAD,
                NetworkPresenter.UrlBuilder.buildShopList(0,COUNT,currentLatLng),
                null,
                shopHandler,
                new ResponseExtractor.ShopExtractor(newDatas,shopHandler)
        );
        ((MainActivity)getActivity()).getExecutorService().submit(networkPresenter);
    }
    private void loadAdv(View view,int pic){
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_ad);
        Glide.with(getActivity()).load(pic).into(imageView);
        adViews.add(view);
    }
    private class ShopListener implements View.OnClickListener {

        private String name;
        private int backgroundID;
        private ShopsModel shopsModel;
        public ShopListener(String name,int backgroundID){
            this.name = name;
            this.backgroundID = backgroundID;
        }
        public ShopListener(ShopsModel shopsModel){
            this.shopsModel = shopsModel;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ShopActivity.class);
            intent.putExtra(ShopActivity.SHOP,shopsModel);
            intent.putExtra(ShopActivity.CUSTOMERADD,destinationAddress);
            String[] strings = shopsModel.getAddress().split(",");
            double latD = Double.valueOf(strings[0]);
            double lngD = Double.valueOf(strings[1]);
            intent.putExtra(ShopActivity.SHOPLAT,latD);
            intent.putExtra(ShopActivity.SHOPLNG,lngD);
            strings = destinationLatLng.split(",");
            latD = Double.valueOf(strings[0]);
            lngD = Double.valueOf(strings[1]);
            intent.putExtra(ShopActivity.CUSLAT,latD);
            intent.putExtra(ShopActivity.CUSLNG,lngD);
            intent.putExtra(ShopActivity.CUSTOMERADD,originAddress);
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
