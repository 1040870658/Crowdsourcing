package hk.hku.yechen.crowdsourcing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.adapters.OrderAdapter;
import hk.hku.yechen.crowdsourcing.util.HoverListDecorator;
import hk.hku.yechen.crowdsourcing.util.SimpleItemDecorator;

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_task,container,false);
            init();
            imageView = (ImageView) contentView.findViewById(R.id.iv_task_background);
            imageView.setImageResource(R.drawable.back_order);
            adapter = new OrderAdapter(orders,getActivity());
            recyclerView = (RecyclerView) contentView.findViewById(R.id.rcv_task);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addItemDecoration(new HoverListDecorator(getResources(),orders,titles));
            recyclerView.addItemDecoration(new SimpleItemDecorator(getActivity(),SimpleItemDecorator.VERTICAL_LIST));
        }
        return contentView;
    }
    private void init(){
        titles = new ArrayList<>();
        titles.add(getString(R.string.order_title_1));
        titles.add(getString(R.string.order_title_2));
        orders = getData();
    }
    public  List<List> getData(){
        List tmp = new ArrayList();
        List tmp2 = new ArrayList();
        tmp2.add(2);
        tmp.add(tmp2);
        tmp2 = new ArrayList();
        tmp2.add(2);
        tmp.add(tmp2);
        return tmp;
    }
}
