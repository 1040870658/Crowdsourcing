package hk.hku.yechen.crowdsourcing.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yechen on 2017/12/7.
 */

public abstract class HeaderBaseAdapter extends RecyclerView.Adapter{
    BaseAdapter baseAdapter;

    public HeaderBaseAdapter(BaseAdapter baseAdapter){
        this.baseAdapter = baseAdapter;
    }

    @Override
    public int getItemCount() {
        return baseAdapter.getItemCount()+1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == getHeaderView()){
            return HeaderHolder.getInstance(initHeaderView(parent,getHeaderView()));
        }
        return baseAdapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position != getHeaderView()){
            baseAdapter.onBindViewHolder((BaseAdapter.GeneralViewHolder) holder,position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return getHeaderView();
        return super.getItemViewType(position);
    }
    public abstract int getHeaderView();

    public static class HeaderHolder extends RecyclerView.ViewHolder{

        private HeaderHolder(View itemView) {
            super(itemView);

        }
        public static HeaderHolder getInstance(View headerView){
            return new HeaderHolder(headerView);
        }
    }
    public abstract View initHeaderView(ViewGroup parent,int headerviewId);
}
