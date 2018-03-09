package hk.hku.yechen.crowdsourcing.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import hk.hku.yechen.crowdsourcing.adapters.GroupAdapter;
import hk.hku.yechen.crowdsourcing.fragments.FragmentOrders;
import hk.hku.yechen.crowdsourcing.model.DecoratorModel;

/**
 * Created by yechen on 2018/3/9.
 */

public class LookupHandler extends Handler {
    private WeakReference<Context> context;
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutWeakReference;
    private WeakReference<DecoratorModel> decoratorModelWeakReference;
    private GroupAdapter adapter;
    public LookupHandler(GroupAdapter adapter, Context context, SwipeRefreshLayout swipeRefreshLayout,DecoratorModel decoratorModel){
        this.adapter = adapter;
        this.context = new WeakReference<>(context);
        this.swipeRefreshLayoutWeakReference = new WeakReference<>(swipeRefreshLayout);
        this.decoratorModelWeakReference = new WeakReference<DecoratorModel>(decoratorModel);
    }

    @Override
    public void handleMessage(Message msg) {
        swipeRefreshLayoutWeakReference.get().setRefreshing(false);
        switch (msg.what){
            case FragmentOrders.LOOKUP_FINISHED:
                adapter.notifyGroupsChanged();
                decoratorModelWeakReference.get().notifyDataSetChanged();
                break;
            case FragmentOrders.LOOKUP_FAILED:
                Toast.makeText(context.get(),"Network Error",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
