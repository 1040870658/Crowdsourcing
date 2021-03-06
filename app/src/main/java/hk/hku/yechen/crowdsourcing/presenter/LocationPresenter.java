package hk.hku.yechen.crowdsourcing.presenter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2017/12/11.
 */

public class LocationPresenter {

    public  LocationManager locationManager;
    private LocationListener locationListener;
    private WeakReference<Activity> activityWeakReference;
    public  boolean PermissionGranted = true;
    public LocationPresenter(Activity activity,LocationListener locationListener){
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.locationListener = locationListener;
    }
    public static String LatLng2String(LatLng latLng){
        if(latLng == null)
            return null;
        return latLng.latitude+","+latLng.longitude;
    }

    public static LatLng String2LatLng(String latLngString){
        if (latLngString == null)
            return null;
        String[] res = latLngString.split(",");
        return new LatLng(Double.valueOf(res[0]),Double.valueOf(res[1]));
    }
    @TargetApi(Build.VERSION_CODES.M)
    private  boolean  checkPermission(Activity context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);
            return false;
        }
        LevelLog.log(LevelLog.ERROR,"Location Presenter","Permission Granted");
        return true;
    }
    public  LatLng getCurrentLatLng() {
        LatLng latLng = null;
        double latitude = 0.0;
        double longitude = 0.0;
        if(activityWeakReference.get() == null)
            return new LatLng(latitude,longitude);
        locationManager = (LocationManager) activityWeakReference.get().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LevelLog.log(LevelLog.ERROR,"Location Presenter","Gps Enabled");
            if(!checkPermission(activityWeakReference.get())){
                return null;
            }
            LevelLog.log(LevelLog.ERROR,"Location Presenter","Now requesting Location from GPS_PROVIDER");
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                LevelLog.log(LevelLog.ERROR,"Location Presenter","Now requesting Location from Network_PROVIDER");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(location != null){
                LevelLog.log(LevelLog.ERROR,"Location Presenter","GPS Located");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                latLng = new LatLng(latitude,longitude);
            }
            else{
                LevelLog.log(LevelLog.ERROR,"Location Presenter","Request Failed");
            }

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    TimeUnit.MINUTES.toMillis(100),
                    100,
                    locationListener);

        }

        return latLng;
    }
    private static class MyLocationListener implements LocationListener {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            LevelLog.log(LevelLog.ERROR,"Located successfully------->","location------>" + "latitude：" + location.getLatitude() + "\nlongtitude:" + location.getAltitude());
        }
    }
    public  LatLng getCurrentLatLng(Activity context, GoogleMap mMap) {
        LatLng latLng = null;
        double latitude = 0.0;
        double longitude = 0.0;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LevelLog.log(LevelLog.ERROR,"Location Presenter","Gps Enabled");
            if(!checkPermission(context)){
                return null;
            };
            Location location = mMap.getMyLocation();
            if(location != null){
                LevelLog.log(LevelLog.ERROR,"Location Presenter","GPS Located");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                latLng = new LatLng(latitude,longitude);
            }
        }
        return latLng;
    }
    public void ReleaseLocationService(){
        if(locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
