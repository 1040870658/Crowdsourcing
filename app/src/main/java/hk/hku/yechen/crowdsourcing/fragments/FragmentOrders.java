package hk.hku.yechen.crowdsourcing.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.adapters.OrderAdapter;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.DecoratorModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.HoverListDecorator;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import hk.hku.yechen.crowdsourcing.util.LookupHandler;

/**
 * Created by yechen on 2017/11/21.
 */

public class FragmentOrders extends Fragment {
    private View contentView;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<List> orders;
    private List<String> titles;
    private ImageView imageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LookupHandler lookupHandler;
    public static final int LOOKUP_FINISHED = 0x01000001;
    public static final int LOOKUP_FAILED = 0x01000002;
    public static int CUSTOMER_TYPE = 0;
    public static int PROVIDER_TYPE = 1;
    private DecoratorModel decoratorModel;
    private HoverListDecorator hoverListDecorator;
    private ExecutorService executorService;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_task,container,false);
            executorService = Executors.newSingleThreadExecutor();
            swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.srl_orders);
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.CYAN,Color.GREEN);
            init();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                }
            });
            imageView = (ImageView) contentView.findViewById(R.id.iv_task_background);
            imageView.setImageResource(R.drawable.back_order);
            orders = new ArrayList<>();
            adapter = new OrderAdapter(orders,getActivity());
            recyclerView = (RecyclerView) contentView.findViewById(R.id.rcv_task);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
          //  recyclerView.addItemDecoration(new SimpleItemDecorator(getActivity(),SimpleItemDecorator.VERTICAL_LIST));
            decoratorModel = new DecoratorModel(getResources(),orders,titles);
            hoverListDecorator = new HoverListDecorator(decoratorModel);
            recyclerView.addItemDecoration(hoverListDecorator);
            lookupHandler = new LookupHandler(adapter,getActivity(),swipeRefreshLayout,decoratorModel);
        }
        return contentView;
    }
    private void init(){
        titles = new ArrayList<>();
        titles.add(getString(R.string.order_title_1));
        titles.add(getString(R.string.order_title_2));
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData(){
        NetworkPresenter networkPresenter = new NetworkPresenter(
                LOOKUP_FINISHED,
                NetworkPresenter.UrlBuilder.buildOrdersLookup(MainActivity.userModel.getPhone(),CUSTOMER_TYPE),
                null,
                lookupHandler,
                new ResponseExtractor.OrderLookup(lookupHandler,orders));

        executorService.submit(networkPresenter);
    }
}
