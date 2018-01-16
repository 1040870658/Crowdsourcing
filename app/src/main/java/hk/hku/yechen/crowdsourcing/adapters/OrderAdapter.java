package hk.hku.yechen.crowdsourcing.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.util.List;

import hk.hku.yechen.crowdsourcing.OrderActivity;
import hk.hku.yechen.crowdsourcing.OrderDetailActivity;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/1/5.
 */

public class OrderAdapter extends GroupAdapter{
    private final int ONGOING = 1;
    private final int FINISHED = 2;
    public OrderAdapter(List<List> groups, Activity context) {
        super(groups, context);
    }

    @Override
    public int getItemViewType(int position) {
        if(groupPosition.size() > 1 && position >= groupPosition.get(1)){
            return FINISHED;
        }
        else{
            return ONGOING;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getLayoutId(int viewType) {
        if(viewType == ONGOING)
            return R.layout.sub_orders;
        return R.layout.sub_orders_finished;
    }

    @Override
    public void convert(final Object data, BaseAdapter.GeneralViewHolder viewHolder, int position) {
      // final OrderModel orderModel = (OrderModel) data;
        viewHolder.setChildListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OrderDetailActivity.class);
                //intent.putExtra(OrderDetailActivity.ORDER_AUG,data);
               // intent.putExtra(OrderActivity.ORDER,orderModel);
                context.startActivity(intent);
            }
        },R.id.btn_detail);
    }

}
