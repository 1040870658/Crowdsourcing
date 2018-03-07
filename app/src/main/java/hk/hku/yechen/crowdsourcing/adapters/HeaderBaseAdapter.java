package hk.hku.yechen.crowdsourcing.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yechen on 2017/12/7.
 */

public abstract class HeaderBaseAdapter extends RecyclerView.Adapter{
    private BaseAdapter baseAdapter;
    private boolean showHeader = true;

    public void showHeader(boolean show){
        this.showHeader = show;
    }
    public HeaderBaseAdapter(BaseAdapter baseAdapter){
        this.baseAdapter = baseAdapter;
    }

    @Override
    public int getItemCount() {
        if(baseAdapter.getItemCount() != 0)
            return baseAdapter.getItemCount()+1;
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == getHeaderView()){
            return HeaderHolder.getInstance(initHeaderView(parent,getHeaderView()));
        }
        else {
            return baseAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) != getHeaderView()){
            baseAdapter.onBindViewHolder((BaseAdapter.GeneralViewHolder) holder,position );
        }
        else{
            if(showHeader){
                showHeaderView(holder);
            }
            else{
                hideHeaderView(holder);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() - 1)
            return getHeaderView();
        return position;
    }
    public abstract int getHeaderView();

    public static class HeaderHolder extends RecyclerView.ViewHolder{

        private View headerView;

        private HeaderHolder(View itemView) {
            super(itemView);
            this.headerView = itemView;
        }
        public static HeaderHolder getInstance(View headerView){
            return new HeaderHolder(headerView);
        }

        public View getHeaderView(){
            return headerView;
        }
    }
    public abstract View initHeaderView(ViewGroup parent,int headerviewId);
    public abstract void hideHeaderView(RecyclerView.ViewHolder viewHolder);
    public abstract void showHeaderView(RecyclerView.ViewHolder viewHolder);

}
