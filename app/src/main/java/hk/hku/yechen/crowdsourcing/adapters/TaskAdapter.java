package hk.hku.yechen.crowdsourcing.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import java.util.List;

import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/1/4.
 */

public class TaskAdapter extends GroupAdapter {
    private final int ONGOING = 1;
    private final int FINISHED = 2;

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
        viewHolder.setChildListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + "12345678"));
                if(!checkPermission(context)){
                    LevelLog.log(LevelLog.ERROR,"phone","phone");
                    return;
                }
                context.startActivity(intent);
            }
        },R.id.iv_phone);
        viewHolder.setChildListener(new GuideListener(22.480019,113.898851),R.id.ib_shop_location);
        viewHolder.setChildListener(new GuideListener(22.480019,113.898851),R.id.ib_target_location);
        viewHolder.setChildListener(new GuideListener(22.480019,113.898851),R.id.ib_des_location);
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
