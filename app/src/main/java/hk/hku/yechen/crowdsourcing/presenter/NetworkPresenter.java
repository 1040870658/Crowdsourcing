package hk.hku.yechen.crowdsourcing.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import hk.hku.yechen.crowdsourcing.network.NetworkEngine;
import hk.hku.yechen.crowdsourcing.util.Extractor;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by yechen on 2017/11/20.
 */

public class NetworkPresenter implements Runnable{
    public static final int H_SUCCESS = 0x00000001;
    public static final int D_SUCCESS = 0x00000003;
    public static final int GEO_SUCCESS = 0x0000002;
    public static final int H_FAIL = 0x00000000;
    public static final int ASYN = 1;
    public static final int SYN = 0;
    public static final int CODE = 0;
    private Extractor responseExtractor;
    //private static String BEGIN = "22.283257, 114.136774";
    //  private static String DESTINATION = "22.2831928,114.1381175";
    private static String BEGIN = "22.313297,114.170546";
    private static String DESTINATION = "22.283208,114.138175";
    public static final String RGEO_SEARCH = "https://maps.googleapis.com/maps/api/geocode/json?";
    public static final String ROUTE_SEARCH = "https://maps.googleapis.com/maps/api/directions/json?";
    //        "origin=22.283257,114.136774&destination=22.2831928,114.1381175&sensor=true&mode=walking&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";
    public static final String NEARBY_SEARCH =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";

    public static final String TEXT_SEARCH =
            "https://maps.googleapis.com/maps/api/place/textsearch/xml" +
                    "?query=starbucks+in+Hong+Kong&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";

    private int code = CODE;
    private int messageCode;
    private Handler handler;
    private String url;
    private FormBody formBody;
    private NetworkEngine.Listener listener;
    private NetworkEngine network = NetworkEngine.getInstance();
    public NetworkPresenter(String url, FormBody formBody, NetworkEngine.Listener listener,Handler handler){
        this.url = url;
        this.formBody = formBody;
        this.listener = listener;
        this.handler = handler;
    }
    public NetworkPresenter(String url, FormBody formBody, NetworkEngine.Listener listener,int code,Handler handler){
        this.url = url;
        this.formBody = formBody;
        this.listener = listener;
        this.code = code;
        this.handler = handler;
    }
    public NetworkPresenter(int messageCode,String url, NetworkEngine.Listener listener, Handler handler, Extractor extractor) {
        this.url = url;
        this.responseExtractor = extractor;
        this.listener = listener;
        this.messageCode = messageCode;
        this.handler = handler;
    }
    public static class UrlBuilder{
        public static String buildRoute(String origin,String destination){
            String url = ROUTE_SEARCH + "origin=" +origin+"&destination=" + destination;
            return url + "&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";
        }
        public static String buildRoute(String origin,String destination,String mode){
            String url = ROUTE_SEARCH + "origin=" +origin+"&destination=" + destination +"&mode="+mode;
         //   messageCode = H_SUCCESS;
            return url + "&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";
        }
        public static String buildRoute(String origin,String destination,String mode,String wayPoints){
            String url = ROUTE_SEARCH + "origin=" +origin +
                    "&destination=" +destination +
                    "&mode=" +mode +
                    "&waypoints="+wayPoints;
         //   messageCode = D_SUCCESS;
            return url + "&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";
        }
        public static String buildRGEO(String latlng){
            String url = RGEO_SEARCH+"latlng="+latlng;
        //    messageCode = reqCode;
            return url + "&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";
        }
    }
    public static FormBody DefineBody(HashMap<String,String> hashMap){
        FormBody.Builder fb = new FormBody.Builder();
        String key;
        String value;
        for(Map.Entry<String,String> entry:hashMap.entrySet()){
            key = entry.getKey();
            value = entry.getValue();
            fb.add(key,value);
        }
        return fb.build();
    }

    public void onSuccess(Response response) {
        responseExtractor.extract(response,messageCode);
    }


    public void onFailed() {
        handler.sendEmptyMessage(H_FAIL);
    }

    @Override
    public void run() {
        network.asynRequest(url, new NetworkEngine.Listener() {
            @Override
            public void onFailure(Call call, IOException e) {
                LevelLog.log(LevelLog.ERROR,"asyn error",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onSuccess(response);
            }
        });

    }
}
