package hk.hku.yechen.crowdsourcing;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import hk.hku.yechen.crowdsourcing.fragments.FragmentMain;
import hk.hku.yechen.crowdsourcing.fragments.FragmentOrders;
import hk.hku.yechen.crowdsourcing.fragments.FragmentService;
import hk.hku.yechen.crowdsourcing.fragments.FragmentTask;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;

/**
 * Created by yechen on 2017/11/20.
 */

public class MainActivity extends FragmentActivity {

    public ExecutorService executorService = Executors.newCachedThreadPool();
    private FragmentTabHost fragmentTabHost;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawerSetting;
    private String origin;
    private String destination;
    private NetworkPresenter networkPresenter;
    public static final String shared_table = "UserInfo";
    public static final String shared_originLatLng = "originLatLng";
    public static final String shared_desLatLng = "desLatLng";
    public static final String shared_originAddress = "originAddress";
    public static final String shared_desAddress = "desAddress";

    private int tabs[]={
            R.layout.tablayout_m1,
            R.layout.tablayout_m2,
            R.layout.tablayout_m3,
            R.layout.tablayout_m4
    };
    public ExecutorService getExecutorService(){
        return executorService;
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
}
