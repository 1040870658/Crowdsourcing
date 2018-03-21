package hk.hku.yechen.crowdsourcing.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.adapters.TaskAdapter;
import hk.hku.yechen.crowdsourcing.model.DecoratorModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.HoverListDecorator;
import hk.hku.yechen.crowdsourcing.util.LookupHandler;
import hk.hku.yechen.crowdsourcing.util.SimpleItemDecorator;

import static hk.hku.yechen.crowdsourcing.fragments.FragmentOrders.LOOKUP_FINISHED;
import static hk.hku.yechen.crowdsourcing.fragments.FragmentOrders.PROVIDER_TYPE;

/**
 * Created by yechen on 2017/11/21.
 */

public class FragmentTask extends Fragment {
    private View contentView;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<String> titles;
    private List<List> tasks;
    private DecoratorModel decoratorModel;
    private LookupHandler lookupHandler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_task,container,false);
            init();
            tasks = new ArrayList<>();
            adapter = new TaskAdapter(tasks,getActivity());
            executorService = Executors.newSingleThreadExecutor();
            swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.srl_orders);
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.CYAN,Color.GREEN);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                }
            });
            recyclerView = (RecyclerView) contentView.findViewById(R.id.rcv_task);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            decoratorModel = new DecoratorModel(getResources(),tasks,titles);
            recyclerView.addItemDecoration(new HoverListDecorator(decoratorModel));
            lookupHandler = new LookupHandler(adapter,getActivity(),swipeRefreshLayout,decoratorModel);
        }
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void init(){
        titles = new ArrayList<>();
        titles.add(getString(R.string.task_title_1));
        titles.add(getString(R.string.task_title_2));
    }
    public void getData(){
        NetworkPresenter networkPresenter = new NetworkPresenter(
                LOOKUP_FINISHED,
                NetworkPresenter.UrlBuilder.buildOrdersLookup(MainActivity.userModel.getPhone(),PROVIDER_TYPE),
                null,
                lookupHandler,
                new ResponseExtractor.OrderLookup(lookupHandler,tasks));

        executorService.submit(networkPresenter);
    }
}
