package hk.hku.yechen.crowdsourcing.presenter;

import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import hk.hku.yechen.crowdsourcing.model.DestinationModel;
import hk.hku.yechen.crowdsourcing.util.Extractor;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by yechen on 2017/11/30.
 */

public class ResponseExtractor implements Extractor {
    public static final int E_SUCCESS = 0x00000011;
    public static final int D_SUCCESS = 0X00000014;
    public static final int E_Error = 0x00000012;
    private static Extractor extractor;
    private ResponseExtractor(){};
    public static Extractor BuildRouteExtractor(PolylineOptions polylineOptions, Handler handler){
        extractor = new RouteExtractor(polylineOptions,handler);
        return extractor;
    }
    public static Extractor BuildReverseGeoExtractor(Handler handler){
        extractor = new ReverseGeoExtractor(handler);
        return extractor;
    }

    @Override
    public void extract(Response response,int messageCode) {
        extractor.extract(response,messageCode);
    }

    public static class ReverseGeoExtractor implements Extractor{
        private JSONObject data;
        private Handler handler;
        public ReverseGeoExtractor(Handler handler){
            this.handler = handler;
        }

        @Override
        public void extract(Response response,int messageCode) {
            try {
                if(response == null) {
                    data = null;
                }
                else {
                    ResponseBody body =response.body();
                    if(body != null)
                        data = new JSONObject(body.string());
                    else
                        data = null;
                }
                JSONArray results = data.getJSONArray("results");
                JSONObject adJson = null;
                String address = null;
                if(results != null)
                     adJson =  results.getJSONObject(0);
                if(adJson != null)
                    address = (String) adJson.get("formatted_address");
                Message message = new Message();
                message.obj = address;
                message.what = messageCode;
                handler.sendMessage(message);

            } catch (JSONException e) {
                LevelLog.log(LevelLog.ERROR,"ResponseExtractor","JsonArray Explained failed");
                handler.sendEmptyMessage(E_Error);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class RouteExtractor implements Extractor {

        private JSONObject data;
        private PolylineOptions polylineOptions;
        private Handler handler;
        public RouteExtractor(PolylineOptions polylineOptions,Handler handler){
            this.polylineOptions = polylineOptions;
            this.handler = handler;
            //LevelLog.log(LevelLog.ERROR,"response",response.body().toString());

        }
        @Override
        public void extract(Response response,int messageCode) {
            try {
                if(response == null) {
                    data = null;
                }
                else {
                    ResponseBody body =response.body();
                    if(body != null)
                        data = new JSONObject(body.string());
                    else
                        data = null;
                }
                ArrayList<JSONArray> steps;
//            LevelLog.log(LevelLog.ERROR,"routes",data.getJSONArray("routes").toString());
                JSONArray legs = data.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
//            LevelLog.log(LevelLog.ERROR,"legs",legs.toString());
                JSONObject step;
                JSONObject startObject;
                JSONObject endObject;
                JSONArray stepsArray;
                LatLng latLng;

                steps = new ArrayList<>();
                JSONObject durationJson;
                JSONObject distanceJson;
                int timeConsuming = 0;
                int distance = 0;
                for(int i = 0;i <legs.length();i ++){
                    steps.add(legs.getJSONObject(i).getJSONArray("steps"));
                    durationJson = legs.getJSONObject(i).getJSONObject("duration");
                    distanceJson = legs.getJSONObject(i).getJSONObject("distance");
                    if (durationJson != null)
                        timeConsuming += durationJson.getInt("value");
                    if(distanceJson != null)
                        distance += distanceJson.getInt("value");
                }
                for(int j = 0;j < steps.size();j ++) {
                    stepsArray = steps.get(j);
//                LevelLog.log(LevelLog.ERROR,"steps",stepsArray.toString());
                    for (int i = 0; i < stepsArray.length(); i++) {
                        step = stepsArray.getJSONObject(i);
                        startObject = step.getJSONObject("start_location");
                        endObject = step.getJSONObject("end_location");
                        latLng = new LatLng(startObject.getDouble("lat"), startObject.getDouble("lng"));

                        polylineOptions.add(latLng);
                        latLng = new LatLng(endObject.getDouble("lat"), endObject.getDouble("lng"));
                        polylineOptions.add(latLng);
                    }
                }
                Message message = new Message();
                message.arg1 = distance;
                message.arg2 = timeConsuming;
                message.what = messageCode;
                handler.sendMessage(message);
                //JSONObject leg = new JSONObject(routes.get("legs").toString());
                //LevelLog.log(LevelLog.ERROR,"routes",routes.toString());
                //LevelLog.log(LevelLog.ERROR,"routes",polylineOptions.toString());
            } catch (JSONException e) {
                LevelLog.log(LevelLog.ERROR,"ResponseExtractor","JsonArray Explained failed");
                handler.sendEmptyMessage(E_Error);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSuccess(){
    }
    public void onError(){
        LevelLog.log(LevelLog.ERROR,"ResponseExtractor","JsonArray Explained failed");
    }
}
