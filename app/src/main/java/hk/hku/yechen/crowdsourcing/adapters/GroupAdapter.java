package hk.hku.yechen.crowdsourcing.adapters;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yechen on 2018/1/4.
 */

public abstract class GroupAdapter extends BaseAdapter{
    protected List<List> groups;
    protected Activity context;
    protected List<Integer> groupPosition;


    public GroupAdapter(List<List> groups, Activity context){
        super();
        this.context = context;
        this.groups = groups;
        setData();
    }

    private void setData(){
        datas = new ArrayList();
        groupPosition = new ArrayList<>();
        groupPosition.add(0);
        for(List group:groups){
            datas.addAll(group);
            groupPosition.add(datas.size());
        }
    }

    public void notifyGroupsChanged(){
        setData();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

}