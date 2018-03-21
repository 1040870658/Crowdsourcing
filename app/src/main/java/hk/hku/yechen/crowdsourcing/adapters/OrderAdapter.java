package hk.hku.yechen.crowdsourcing.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.util.List;

import hk.hku.yechen.crowdsourcing.OrderActivity;
import hk.hku.yechen.crowdsourcing.OrderDetailActivity;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.fragments.FragmentOrders;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/1/5.
 */

public class OrderAdapter extends GroupAdapter{
    private final int ONGOING = 1;
    private final int FINISHED = 2;
    private FragmentOrders.ConfirmListener feedback;
    public OrderAdapter(List<List> groups, Activity context, FragmentOrders.ConfirmListener feedback) {
        super(groups, context);
        this.feedback = feedback;
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
            return R.layout.sub_order_x;
        return R.layout.sub_orders_finished;
    }

    @Override
    public void convert(final Object data, BaseAdapter.GeneralViewHolder viewHolder, int position) {
       final OrderModel orderModel = (OrderModel) data;
        viewHolder.setChildListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.ORDER_AUG,orderModel);
               // intent.putExtra(OrderActivity.ORDER,orderModel);
                context.startActivity(intent);
            }
        },R.id.btn_detail);

        if(getItemViewType(position) == ONGOING){

            viewHolder.setTextView(R.id.tv_name,orderModel.getProviderName());
            viewHolder.setTextView(R.id.tv_process_shop,orderModel.getShopName());
            viewHolder.getView(R.id.ib_des_location).setClickable(false);

            switch (orderModel.getState()){
                case OrderModel.LAUNCHED:
                    viewHolder.getView(R.id.iv_phone).setClickable(false);
                    viewHolder.setImageView(R.id.iv_phone,R.drawable.iv_phone_disable);
                    viewHolder.setImageButton(context,R.id.ib_shop_location,R.drawable.icon_stage_processing);
                    viewHolder.setImageView(R.id.iv_shop_in_process,R.color.grey);
                    viewHolder.setImageButton(context,R.id.ib_target_location,R.drawable.icon_stage_waiting);
                    viewHolder.setImageView(R.id.iv_target_in_process,R.color.grey);
                    viewHolder.setImageButton(context,R.id.ib_des_location,R.drawable.icon_stage_waiting);
                    break;
                case OrderModel.PICKED:
                    viewHolder.getView(R.id.iv_phone).setClickable(true);
                    viewHolder.setImageView(R.id.iv_phone,R.drawable.phone);
                    viewHolder.setImageButton(context,R.id.ib_shop_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_shop_in_process,R.color.orange);
                    viewHolder.setImageButton(context,R.id.ib_target_location,R.drawable.icon_stage_processing);
                    viewHolder.setImageView(R.id.iv_target_in_process,R.color.grey);
                    viewHolder.setImageButton(context,R.id.ib_des_location,R.drawable.icon_stage_waiting);
                    break;
                case OrderModel.COLLECTED:
                    viewHolder.getView(R.id.iv_phone).setClickable(true);
                    viewHolder.setImageView(R.id.iv_phone,R.drawable.phone);
                    viewHolder.setImageButton(context,R.id.ib_shop_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_shop_in_process,R.color.orange);
                    viewHolder.setImageButton(context,R.id.ib_target_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_target_in_process,R.color.grey);
                    viewHolder.setImageButton(context,R.id.ib_des_location,R.drawable.icon_stage_waiting);
                    break;
                case OrderModel.ARRIVED:
                    viewHolder.getView(R.id.iv_phone).setClickable(true);
                    viewHolder.setImageView(R.id.iv_phone,R.drawable.phone);
                    viewHolder.setImageButton(context,R.id.ib_shop_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_shop_in_process,R.color.orange);
                    viewHolder.setImageButton(context,R.id.ib_target_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_target_in_process,R.color.orange);
                    viewHolder.setImageButton(context,R.id.ib_des_location,R.drawable.btn_confirm);
                    feedback.setOrderId(orderModel.getId());
                    feedback.setStatus(orderModel.getState()+1);
                    feedback.setUserPhone(orderModel.getProviderPhone());
                    viewHolder.setChildListener(feedback,R.id.ib_des_location);
                    viewHolder.getView(R.id.iv_phone).setClickable(true);
                    break;
            }
        }
        else{
            viewHolder.setTextView(R.id.tv_f_item_1,orderModel.getShopName());
            viewHolder.setTextView(R.id.tv_f_status,orderModel.getStateInfo());
        }
    }

}
