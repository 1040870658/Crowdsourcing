package hk.hku.yechen.crowdsourcing.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.hku.yechen.crowdsourcing.R;

/**
 * Created by yechen on 2018/3/5.
 */

public class NormalTailorAdapter extends HeaderBaseAdapter {
    public NormalTailorAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    public int getHeaderView() {
        return R.layout.recyclerview_header;
    }

    @Override
    public View initHeaderView(ViewGroup parent, int headerviewId) {
        return LayoutInflater.from(parent.getContext()).inflate(headerviewId,null);
    }

    @Override
    public void hideHeaderView(RecyclerView.ViewHolder viewHolder) {
        HeaderHolder headerHolder = (HeaderHolder)viewHolder;
        View header = headerHolder.getHeaderView();
        header.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showHeaderView(RecyclerView.ViewHolder viewHolder) {
        HeaderHolder headerHolder = (HeaderHolder)viewHolder;
        View header = headerHolder.getHeaderView();
        header.setVisibility(View.VISIBLE);
    }
}
