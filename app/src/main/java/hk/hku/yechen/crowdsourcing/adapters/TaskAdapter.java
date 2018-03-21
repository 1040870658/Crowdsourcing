package hk.hku.yechen.crowdsourcing.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/1/4.
 */

public class TaskAdapter extends GroupAdapter {
    private static final int ONGOING = 1;
    private static final int FINISHED = 2;

    public TaskAdapter(List<List> groups, Activity context) {
        super(groups, context);
    }

    @Override
    public int getItemViewType(int position) {
        if (groupPosition.size() > 1 && position >= groupPosition.get(1)) {
            return FINISHED;
        } else {
            return ONGOING;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getLayoutId(int viewType) {
        if (viewType == ONGOING)
            return R.layout.sub_task;
        return R.layout.sub_task_finished;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkPermission(Activity context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED ) {
            context.requestPermissions(new String[]{Manifest.permission.CALL_PHONE},0);
            return false;
        }
        return true;
    }
    @Override
    public void convert(Object data, BaseAdapter.GeneralViewHolder viewHolder, int position) {
        final OrderModel orderModel = (OrderModel)data;
        viewHolder.setChildListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        +orderModel.getCustomerPhone()));
                if(!checkPermission(context)){
                    LevelLog.log(LevelLog.ERROR,"phone","phone");
                    return;
                }
                context.startActivity(intent);
            }
        },R.id.iv_phone);

        viewHolder.setChildListener(new GuideListener(orderModel.getStartLat(),orderModel.getStartLng()),R.id.ib_target_location);
        viewHolder.setChildListener(new GuideListener(orderModel.getEndLat(),orderModel.getEndLng()),R.id.ib_des_location);

        if(getItemViewType(position) == ONGOING){

            viewHolder.setTextView(R.id.tv_name,orderModel.getCustomerName());
            viewHolder.setTextView(R.id.tv_reward,String.valueOf("Task In Process"));
            ((TextView)viewHolder.getView(R.id.tv_reward)).setTextColor(context.getResources().getColor(R.color.green));
            viewHolder.setTextView(R.id.tv_process_shop,orderModel.getShopName());

            switch (orderModel.getState()){

                case OrderModel.PICKED:
                    viewHolder.setImageButton(context,R.id.ib_shop_location,R.drawable.icon_stage_processing);
                    viewHolder.setImageView(R.id.iv_shop_in_process,R.color.grey);
                    viewHolder.setImageButton(context,R.id.ib_target_location,R.drawable.icon_stage_waiting);
                    viewHolder.setImageView(R.id.iv_target_in_process,R.color.grey);
                    viewHolder.setImageButton(context,R.id.ib_des_location,R.drawable.icon_stage_waiting);
                    break;
                case OrderModel.COLLECTED:
                    viewHolder.setImageButton(context,R.id.ib_shop_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_shop_in_process,R.color.orange);
                    viewHolder.setImageButton(context,R.id.ib_target_location,R.drawable.icon_stage_processing);
                    viewHolder.setImageView(R.id.iv_target_in_process,R.color.grey);
                    viewHolder.setImageButton(context,R.id.ib_des_location,R.drawable.icon_stage_waiting);
                    break;
                case OrderModel.ARRIVED:
                    viewHolder.setImageButton(context,R.id.ib_shop_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_shop_in_process,R.color.orange);
                    viewHolder.setImageButton(context,R.id.ib_target_location,R.drawable.icon_stage_finish);
                    viewHolder.setImageView(R.id.iv_target_in_process,R.color.orange);
                    viewHolder.setImageButton(context,R.id.ib_des_location,R.drawable.icon_stage_processing);
                    viewHolder.setTextView(R.id.tv_reward,String.valueOf("Waiting For Confirmation"));
                    ((TextView)viewHolder.getView(R.id.tv_reward)).setTextColor(Color.RED);
                    break;
            }
        }
        else{
            viewHolder.setTextView(R.id.tv_task_refund,String.valueOf("$ "+orderModel.getTips()));
            viewHolder.setTextView(R.id.tv_task_credit,String.valueOf(orderModel.getCredit()));
            viewHolder.setTextView(R.id.tv_f_item_1,orderModel.getShopName());
        }
    }
    private class GuideListener implements View.OnClickListener{

        private String latLng;
        public GuideListener(double lat,double lng){
            latLng = lat+","+lng;
        }
        @Override
        public void onClick(View v) {
            LevelLog.log(LevelLog.ERROR,"click","guide");
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+latLng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }
    }
}
