package hk.hku.yechen.crowdsourcing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.adapters.OrderAdapter;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.util.HoverListDecorator;
import hk.hku.yechen.crowdsourcing.util.SimpleItemDecorator;

import static android.support.v7.recyclerview.R.attr.layoutManager;

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
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
          //  recyclerView.addItemDecoration(new SimpleItemDecorator(getActivity(),SimpleItemDecorator.VERTICAL_LIST));
            recyclerView.addItemDecoration(new HoverListDecorator(getResources(),orders,titles));
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
        HashMap<CommodityModel,Integer> commodities;
        commodities = new HashMap<>();
        commodities.put(new CommodityModel(0,0,"Sausage Roll",10,1,33),1);
        tmp2.add(new OrderModel(7,commodities,
                new LatLng(22.282712,114.129371),new LatLng(22.2831920,114.1381181),
                "香港石塘咀卑路乍街8号","香港大学图书馆",38.0));
        tmp.add(tmp2);

        commodities = new HashMap<>();
        commodities.put(new CommodityModel(0,0,"Sausage Roll",10,1,55),1);
        tmp2 = new ArrayList();
        tmp2.add(new OrderModel(4,commodities,
                new LatLng(22.283432,114.129269),
                new LatLng(22.2831923,114.1381197),
                "香港大角咀","香港大学智华馆",38.0));
        tmp2.add(new OrderModel(4,commodities,
                new LatLng(22.283432,114.129269),
                new LatLng(22.2831923,114.1381197),
                "香港大角咀","香港大学智华馆",38.0));
        tmp2.add(new OrderModel(4,commodities,
                new LatLng(22.283432,114.129269),
                new LatLng(22.2831923,114.1381197),
                "香港大角咀","香港大学智华馆",38.0));
        tmp.add(tmp2);
        return tmp;
    }
}
