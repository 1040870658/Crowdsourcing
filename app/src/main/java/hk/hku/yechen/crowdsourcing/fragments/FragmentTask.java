package hk.hku.yechen.crowdsourcing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.adapters.GroupAdapter;
import hk.hku.yechen.crowdsourcing.adapters.TaskAdapter;
import hk.hku.yechen.crowdsourcing.model.ItemModel;
import hk.hku.yechen.crowdsourcing.util.HoverListDecorator;

/**
 * Created by yechen on 2017/11/21.
 */

public class FragmentTask extends Fragment {
    private View contentView;
    private RecyclerView recyclerView;
    private BaseAdapter adapter;
    private List<String> titles;
    private List<List> tasks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_task,container,false);
            init();
            adapter = new TaskAdapter(tasks,getActivity());
            recyclerView = (RecyclerView) contentView.findViewById(R.id.rcv_task);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addItemDecoration(new HoverListDecorator(getResources(),tasks,titles));
        }
        return contentView;
    }
    private void init(){
        titles = new ArrayList<>();
        titles.add(getString(R.string.task_title_1));
        titles.add(getString(R.string.task_title_2));
        tasks = getData();
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
