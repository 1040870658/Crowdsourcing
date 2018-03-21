package hk.hku.yechen.crowdsourcing.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.hku.yechen.crowdsourcing.model.CommodityModel;
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
    public static final int COMMODITY_SHOP_FAIL = 0x00000011;
    public static final int COMMODITY_SHOP_SUCCESS = 0x00000010;
    public static final int SHOP_SUCCESS = 0x00000008;
    public static final int SHOP_RELOAD = 0x00000012;
    public static final int SHOP_FAIL = 0x00000009;
    public static final int H_SUCCESS = 0x00000001;
    public static final int D_SUCCESS = 0x00000003;
    public static final int GEO_SUCCESS = 0x0000002;
    public static final int LOGIN_SUCCESS = 0x00000004;
    public static final int LOGIN_FAIL = 0x00000005;
    public static final int REG_FAIL = 0x00000006;
    public static final int REG_SUCCESS = 0x00000007;
    public static final int H_FAIL = 0x00000000;
    public static final int ASYN = 1;
    public static final int SYN = 0;
    public static final int CODE = 0;
    private Extractor responseExtractor;
    //private static String BEGIN = "22.283257, 114.136774";
    //  private static String DESTINATION = "22.2831928,114.1381175";
    private static String BEGIN = "22.313297,114.170546";
    private static String DESTINATION = "22.283208,114.138175";
    public static String ip_address = "147.8.5.121:8080";
    public static final String LOGIN = "http://"+ip_address+"/user/login?";
    public static final String REGISTER = "http://"+ip_address+"/user/register?";
    public static final String UPDATE_STATUS = "http://"+ip_address+"/order/updateOrder?";
    public static final String CONFIRM_ORDER = "http://"+ip_address+"/order/confirmOrder?";
    public static final String RGEO_SEARCH = "https://maps.googleapis.com/maps/api/geocode/json?";
    public static final String ROUTE_SEARCH = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String SHOP_LIST = "http://"+ip_address+"/shop/list?";
    public static final String COMMODITY_IN_SHOP = "http://"+ip_address+"/commodity/singleshop?";
    public static final String ORDERS_LOOKUP = "http://"+ip_address+"/order/lookup?";
    public static final String RECOMMEND_ORDER = "http://"+ip_address+"/order/recommendOrder?";
    //        "origin=22.283257,114.136774&destination=22.2831928,114.1381175&sensor=true&mode=walking&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";
    public static final String NEARBY_SEARCH =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";

    public static final String TEXT_SEARCH =
            "https://maps.googleapis.com/maps/api/place/textsearch/xml" +
                    "?query=starbucks+in+Hong+Kong&key=AIzaSyCN8pCyzbH7sNTHpaeEA7Rg48nt49WoUOU";

    public static final String ORDER_LAUNCH = "http://"+ip_address+"/order/launch?";

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
        public static String buildLogin(String phone,String password){
            return LOGIN + "userId="+phone+"&password="+password;
        }

        public static String buildUpdateStatus(long orderId,int status,String userPhone){
            return UPDATE_STATUS+"orderId="+orderId+"&status="+status+"&providerId="+userPhone;
        }

        public static String buildConfirmOrder(long orderId,int status,String phone,float credit){
            return CONFIRM_ORDER+"orderId="+orderId+"&status="+status+"&providerId="+phone+"&credit="+credit;
        }
        public static String buildOrderLaunch(String customerPhone,
                                              HashMap<CommodityModel,Integer> commMap,
                                              String destination,
                                              String desLatLng){
            String url = ORDER_LAUNCH +"customerPhone="+customerPhone;

            StringBuilder commodityIds = new StringBuilder();
            boolean started = false;
            for(HashMap.Entry<CommodityModel,Integer> entry:commMap.entrySet()){
                if(started)
                    commodityIds.append(",");
                commodityIds.append(entry.getKey().getCommodityId());
                commodityIds.append("x");
                commodityIds.append(entry.getValue());
                started = true;
            }

            url = url + "&commodityIds=" + commodityIds + "&destination=" + destination
                    +"&desLatLng="+desLatLng;
            return url;
        }
        public static String buildRegister(String phone,String userName,String password,String idCard,String email){
            String url= REGISTER +"phone="+phone+
                    "&username="+userName+
                    "&password="+password+
                    "&idCard="+idCard+
                    "&email="+email;
            return url;
        }

        public static String buildShopList(int offset,int count,String location){
            String url = SHOP_LIST + "offset="+offset
                    +"&num="+count
                    +"&location="+location;
            return url;
        }

        public static String buildCommodityInShop(long shopId,int offset,int count){
            return COMMODITY_IN_SHOP+"shopId="+shopId+"&offset="+offset+"&count="+count;
        }

        public static String buildRecommend(String src,String des){
            return RECOMMEND_ORDER+"src="+src+"&des="+des;
        }
        public static String buildOrdersLookup(String phone,int type){
            if (type != 0 && type != 1)
                type = 0;
            String[] types = new String[2];
            types[0] = "customer";
            types[1] = "provider";
            String url = ORDERS_LOOKUP + "userPhone="+phone+"&userType="+types[type];
            return url;
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

        LevelLog.log(LevelLog.ERROR,"url",url);
        network.asynRequest(url, new NetworkEngine.Listener() {
            @Override
            public void onFailure(Call call, IOException e) {
                LevelLog.log(LevelLog.ERROR,"asyn error",e.toString());
                handler.sendEmptyMessage(NetworkPresenter.H_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onSuccess(response);
            }
        });

    }
}
