package hk.hku.yechen.crowdsourcing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hk.hku.yechen.crowdsourcing.fragments.FragmentMain;
import hk.hku.yechen.crowdsourcing.fragments.FragmentOrders;
import hk.hku.yechen.crowdsourcing.fragments.FragmentService;
import hk.hku.yechen.crowdsourcing.fragments.FragmentTask;
import hk.hku.yechen.crowdsourcing.model.UserModel;
import hk.hku.yechen.crowdsourcing.presenter.LocationPresenter;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.UserHandler;

/**
 * Created by yechen on 2017/11/20.
 */

public class MainActivity extends FragmentActivity {

    private LocationPresenter locationPresenter;
    private SharedPreferences sharedPreferences;
    public ExecutorService executorService = Executors.newCachedThreadPool();
    public ExecutorService fixService = Executors.newFixedThreadPool(2);
    private FragmentTabHost fragmentTabHost;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawerSetting;
    public static UserModel userModel;
    private TextView userText;
    private TextView accountText;
    private TextView creditText;
    private TextView loginState;
    protected String originLatLng;
    protected String destinationLatLng;
    protected String originAddress;
    protected String destinationAddress;
    protected LatLng currentLatLng;
    protected String currentAddress;

    public static final String shared_table = "UserInfo";
    public static final String shared_originLatLng = "originLatLng";
    public static final String shared_desLatLng = "desLatLng";
    public static final String shared_originAddress = "originAddress";
    public static final String shared_desAddress = "desAddress";

    public ExecutorService getFixService(){
        return fixService;
    }
    private int tabs[]={
            R.layout.tablayout_m1,
            R.layout.tablayout_m2,
            R.layout.tablayout_m3,
            R.layout.tablayout_m4
    };
    public ExecutorService getExecutorService(){
        return executorService;
    }


    public void setOriginLatLng(String originLatLng) {
        this.originLatLng = originLatLng;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getOriginLatLng() {
        return originLatLng;
    }

    public String getDestinationLatLng() {
        return destinationLatLng;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationLatLng(String destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public LocationPresenter getLocationPresenter(){
        return locationPresenter;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        customizeActionBar();
        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this,this.getSupportFragmentManager(),R.id.main_content);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("Pick").setIndicator(getLayoutInflater().inflate(tabs[0],null))
                ,FragmentMain.class,null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("shop").setIndicator(getLayoutInflater().inflate(tabs[1],null))
                ,FragmentService.class,null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("order").setIndicator(getLayoutInflater().inflate(tabs[2],null))
                ,FragmentOrders.class,null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("task").setIndicator(getLayoutInflater().inflate(tabs[3],null))
                ,FragmentTask.class,null);
        //fragmentTabHost.addTab(fragmentTabHost.newTabSpec("pick").setIndicator(getLayoutInflater().inflate(tabs[0],null),));
        drawerLayout = (DrawerLayout) findViewById(R.id.main_container);
        drawerSetting = (RelativeLayout)findViewById(R.id.main_drawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        userText = (TextView) drawerLayout.findViewById(R.id.tv_drawerName);
        accountText = (TextView) drawerLayout.findViewById(R.id.tv_account);
        creditText = (TextView) drawerLayout.findViewById(R.id.tv_credit);
        loginState = (TextView) drawerLayout.findViewById(R.id.tv_login_state);

        loginState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                userModel = null;
                locationPresenter.ReleaseLocationService();
                finish();
            }
        });
        if(userModel != null){
            setUserInfo();
        }
        readUserInfo();
        locationPresenter = new LocationPresenter(this,new MyLocationListener(this));
       // currentLatLng = locationPresenter.getCurrentLatLng();
    }

    public void setUserInfo(){
        userText.setText(userModel.getUserName());
        accountText.setText(String.valueOf(userModel.getProperty()));
        creditText.setText(String.valueOf(userModel.getCredit()));
    }
    @Override
    protected void onResume() {
        super.onResume();
        currentLatLng = locationPresenter.getCurrentLatLng();
        int tabs = getIntent().getIntExtra("tabs",-1);
        if(tabs != -1){
            fragmentTabHost.setCurrentTab(tabs);
            getIntent().putExtra("tabs",-1);
        }
        Handler handler = new UserHandler(this);
        NetworkPresenter networkPresenter = new NetworkPresenter(
                NetworkPresenter.LOGIN_SUCCESS,
                NetworkPresenter.UrlBuilder.buildLogin(userModel.getPhone(),userModel.getPassword()),
                null,
                handler,
                new ResponseExtractor.UserExtractor(handler));
        new Thread(networkPresenter).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationPresenter.ReleaseLocationService();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    void customizeActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.actionbar_style, null);
        getActionBar().setCustomView(actionbarLayout);
    }
    public void Switch(View view){

        if(drawerLayout.isDrawerOpen(drawerSetting)){
            drawerLayout.closeDrawer(drawerSetting);
        }
        else{
            drawerLayout.openDrawer(drawerSetting);
        }
    }
    public FragmentTabHost getFragmentTabHost(){
       return fragmentTabHost;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeUserInfo();
    }
    private void writeUserInfo(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.shared_originLatLng,originLatLng);
        editor.putString(MainActivity.shared_desLatLng,destinationLatLng);
        editor.putString(MainActivity.shared_originAddress,originAddress);
        editor.putString(MainActivity.shared_desAddress,destinationAddress);
        editor.apply();
    }

    private static class MyLocationListener implements LocationListener{

        private WeakReference<MainActivity> activityWeakReference;
        public MyLocationListener(MainActivity activity){
            this.activityWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            activityWeakReference.get().setCurrentLatLng( new LatLng(latitude,longitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
    }


    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    private void readUserInfo(){
        sharedPreferences = getSharedPreferences(MainActivity.shared_table, Context.MODE_PRIVATE);
        setOriginLatLng(sharedPreferences.getString(MainActivity.shared_originLatLng,null));
        setDestinationLatLng(sharedPreferences.getString(MainActivity.shared_desLatLng,null));
        setOriginAddress(sharedPreferences.getString(MainActivity.shared_originAddress,null));
        setDestinationAddress(sharedPreferences.getString(MainActivity.shared_desAddress,null));
    };
}
