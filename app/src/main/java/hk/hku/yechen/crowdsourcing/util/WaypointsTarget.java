package hk.hku.yechen.crowdsourcing.util;

import android.graphics.Bitmap;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by yechen on 2017/12/26.
 */

public class WaypointsTarget extends SimpleTarget<Bitmap> {
    private MarkerOptions markerOptions;
    private LatLng latLng;
    public WaypointsTarget(MarkerOptions markerOptions, LatLng latLng){
        this.markerOptions = markerOptions;
        this.latLng = latLng;
    }

    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        markerOptions.position(latLng)
                .title("wayPoints").
                icon(BitmapDescriptorFactory.fromBitmap(resource));
        LevelLog.log(LevelLog.ERROR,"waypoints",resource.toString());
    }
}
