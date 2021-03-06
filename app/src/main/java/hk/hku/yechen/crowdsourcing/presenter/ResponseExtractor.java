package hk.hku.yechen.crowdsourcing.presenter;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import hk.hku.yechen.crowdsourcing.OrderActivity;
import hk.hku.yechen.crowdsourcing.ShopActivity;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.DestinationModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.model.ShopsModel;
import hk.hku.yechen.crowdsourcing.model.UserModel;
import hk.hku.yechen.crowdsourcing.network.Network;
import hk.hku.yechen.crowdsourcing.util.Extractor;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by yechen on 2017/11/30.
 */

public class ResponseExtractor implements Extractor {
    public static final int SUCCESS = 1;
    public static final int EXCEED = 0;
    public static final int SERVER_ERROR = -1;
    public static final int INSUFFICIENT_BALANCE = -2;
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

   public static class UserExtractor implements Extractor{
        private JSONObject data;
        private Handler handler;

        public UserExtractor(Handler handler){
            this.handler = handler;
        }
        @Override
        public void extract(Response response, int messageCode) {
            try {
                if(response == null){
                    data = null;
                    return;
                }
                else {
                    ResponseBody body = response.body();
                    if(body != null) {
                        data = new JSONObject(body.string());
                    }
                    else {
                        data = null;
                        return;
                    }
                }

                if(data.getString("success").equals("false")){
                    handler.sendEmptyMessage(NetworkPresenter.REG_FAIL);
                    handler.sendEmptyMessage(NetworkPresenter.LOGIN_FAIL);
                    return;
                }

                data = data.getJSONObject("data");
                UserModel userModel = new UserModel(
                        data.getString("phone"),
                        data.getString("userName"),
                        data.getString("passWord"),
                        data.getString("email"),
                        Float.valueOf(data.getString("credit")),
                        data.getDouble("property"));
                Message message = new Message();
                message.obj = userModel;
                message.what = messageCode;
                handler.sendMessage(message);
            }catch (IOException e){
                LevelLog.log(LevelLog.ERROR,"IOEXception",e.toString());
                handler.sendEmptyMessage(NetworkPresenter.H_FAIL);
            }catch (JSONException e){
                LevelLog.log(LevelLog.ERROR,"JsonEXception",e.toString());
                handler.sendEmptyMessage(NetworkPresenter.H_FAIL);

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

    public static class CommodityExtractor implements Extractor{

        private Handler handler;
        private List<CommodityModel> commodityModels;

        public CommodityExtractor(List<CommodityModel> commodityModels,Handler handler){
            this.handler = handler;
            this.commodityModels = commodityModels;
        }
        @Override
        public void extract(Response response, int messageCode) {
            JSONArray jsonArray;
            JSONObject data;
            if(response == null){
                return;
            }
            else {
                ResponseBody body = response.body();
                if (body != null) {
                    try {
                        jsonArray = new JSONArray(body.string());
                        CommodityModel commodityModel;
                        synchronized (commodityModels) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                data = jsonArray.getJSONObject(i);
                                if (data == null)
                                    break;

                                commodityModel = new CommodityModel(
                                        data.getLong("commodityID"),
                                        data.getLong("shopID"),
                                        data.getString("commodityName"),
                                        data.getDouble("commodityPrice"),
                                        data.getString("image"),
                                        data.getInt("commodityStock")
                                );

                                commodityModels.add(commodityModel);
                            }
                        }
                        handler.sendEmptyMessage(messageCode);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        }
    }

    public static class OrderLookup implements Extractor{
        private Handler handler;
        private List<List> orders;

        public OrderLookup(Handler handler,List<List> orders){
            this.handler = handler;
            this.orders = orders;
        }
        @Override
        public void extract(Response response, int messageCode){
            JSONArray jsonArray;
            List<OrderModel> finished;
            List<OrderModel> inProcess;

            if(response != null){
                ResponseBody body = response.body();

                if(body != null){

                    try {
                        synchronized (orders) {
                            jsonArray = new JSONArray(body.string());
                            finished = new ArrayList<>();
                            inProcess = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                OrderModel orderModel = extractOrder(jsonArray.getJSONObject(i));
                                if (orderModel.getState() == OrderModel.FINISHED) {
                                    finished.add(orderModel);
                                } else {
                                    inProcess.add(orderModel);
                                }
                            }
                            orders.clear();
                            orders.add(inProcess);
                            orders.add(finished);
                            handler.sendEmptyMessage(messageCode);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static OrderModel extractOrder(JSONObject outerJson) throws Exception{
        JSONArray itemArray;
        JSONArray numberArray;
        LatLng start;
        LatLng end;
        JSONObject jsonObject;
        JSONObject itemObject;
        List<Integer> numbers;
        HashMap<CommodityModel,Integer> commodityModelIntegerHashMap;
        CommodityModel commodityModel;

        jsonObject = outerJson.getJSONObject("order");
        itemArray = outerJson.getJSONArray("commodityList");
        commodityModelIntegerHashMap = new HashMap<>();
        numbers = new ArrayList<>();
        numberArray = outerJson.getJSONArray("numbers");

        if (numberArray != null) {
            for (int j = 0; j != numberArray.length(); j++) {
                numbers.add(numberArray.getInt(j));
            }
        }

        if (itemArray != null) {
            for (int j = 0; j != itemArray.length(); j++) {
                itemObject = itemArray.getJSONObject(j);
                commodityModel = new CommodityModel(
                        itemObject.getLong("commodityID"),
                        itemObject.getLong("shopID"),
                        itemObject.getString("commodityName"),
                        itemObject.getDouble("commodityPrice"),
                        itemObject.getString("image"),
                        itemObject.getInt("commodityStock"));
                commodityModelIntegerHashMap.put(commodityModel, numbers.get(j));
            }
        }

        start = transferString2LatLng(jsonObject.getString("shopLatLng"));
        end = transferString2LatLng(jsonObject.getString("desLatLng"));
        OrderModel orderModel = new OrderModel(
                jsonObject.getInt("orderId"),
                jsonObject.getString("customerPhone"),
                jsonObject.getString("providerPhone"),
                commodityModelIntegerHashMap,
                start,
                end,
                jsonObject.getString("shopAddress"),
                jsonObject.getString("destination"),
                jsonObject.getDouble("price"),
                outerJson.getString("providerName"),
                outerJson.getString("customerName"),
                outerJson.getString("shopName"),
                (float)jsonObject.getDouble("credit")
        );
        int status = jsonObject.getInt("status");
        orderModel.setState(status);
        return orderModel;

    }
    public static class RecommendExtractor implements Extractor{

        private Handler handler;
        private List<DestinationModel> destinationModels;

        public RecommendExtractor(Handler handler,List<DestinationModel> destinationModels){
            this.handler = handler;
            this.destinationModels = destinationModels;
        }

        @Override
        public void extract(Response response, int messageCode) {

            if(response != null){
                ResponseBody responseBody = response.body();
                if(responseBody != null){
                    try {
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        OrderModel orderModel;

                        jsonArray = new JSONArray(responseBody.string());
                        destinationModels.clear();
                        for(int i = 0;i < jsonArray.length();i ++){
                            jsonObject = jsonArray.getJSONObject(i);
                            orderModel = extractOrder(jsonObject.getJSONObject("order"));
                            long timeCost = jsonObject.getLong("timeCost");
                            String distance = jsonObject.getString("distance");
                            String shopName = jsonObject.getString("shopName");
                            String image = jsonObject.getString("image");
                            destinationModels.add(new DestinationModel(
                                    timeCost,
                                    orderModel.getTips(),
                                    shopName,
                                    image,
                                    distance,
                                    orderModel));
                        }
                        handler.sendEmptyMessage(messageCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class UpdateOrderStatusExtractor implements Extractor{

        private Handler handler;

        public UpdateOrderStatusExtractor(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void extract(Response response, int messageCode) {

            if(response != null){
                ResponseBody responseBody = response.body();
                if(responseBody != null){
                    try {
                        if(responseBody.string().equals("true"))
                            handler.sendEmptyMessageDelayed(messageCode,2000);
                        else
                            handler.sendEmptyMessageDelayed(NetworkPresenter.H_FAIL,2000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static LatLng transferString2LatLng(String latlng){
        String[] strings;
        strings = latlng.split(",");
        return new LatLng(Double.valueOf(strings[0]),Double.valueOf(strings[1]));
    }
    public static class LaunchExtractor implements Extractor{

        private Handler handler;
        private List<String> commodityNames;

        public LaunchExtractor(List<String> commodityNames,Handler handler){
            this.handler = handler;
            this.commodityNames = commodityNames;
        }

        @Override
        public void extract(Response response, int messageCode) {
            JSONObject jsonObject;
            JSONArray jsonArray;

            if(response != null){
                ResponseBody body = response.body();
                if(body != null){

                    try{
                        jsonObject = new JSONObject(body.string());
                        int state = jsonObject.getInt("state");
                        switch (state) {
                            case SUCCESS:
                                handler.sendEmptyMessage(OrderActivity.LAUNCH_SUCCESS);
                                break;
                            case EXCEED :
                                jsonArray = jsonObject.getJSONArray("commodityNames");
                                for(int i = 0;i < jsonArray.length();i ++){
                                    commodityNames.add(jsonArray.getString(i));
                                }
                                handler.sendEmptyMessage(OrderActivity.NUMBER_EXCEEDED);
                                break;
                            case SERVER_ERROR:
                                handler.sendEmptyMessage(OrderActivity.LAUNCH_UNEXPECTED);
                                break;
                            case INSUFFICIENT_BALANCE:
                                handler.sendEmptyMessage(OrderActivity.INSUFFICIENT_BALANCE);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class ShopExtractor implements Extractor{

        private Handler handler;
        private List<ShopsModel> shopsData;

        public ShopExtractor(List<ShopsModel> shopsData,Handler handler){
            this.handler = handler;
            this.shopsData = shopsData;
        }

        @Override
        public void extract(Response response, int messageCode) {
            JSONArray jsonArray;
            JSONObject data;
            if(response == null){
                return;
            }
            else {
                ResponseBody body = response.body();
                if(body != null) {


                    try {
                        jsonArray = new JSONArray(body.string());
                        ShopsModel shopsModel;

                        synchronized (shopsData) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                data = jsonArray.getJSONObject(i);
                                if (data == null)
                                    break;
                                JSONObject shop = data.getJSONObject("shop");
                                if (shop == null)
                                    break;
                                shopsModel = new ShopsModel(
                                        shop.getString("image"),
                                        shop.getLong("shopId"),
                                        shop.getString("address"),
                                        shop.getString("pysicalAddress"),
                                        shop.getString("shopName"),
                                        data.getString("distanceText"),
                                        data.getString("durationText")
                                );
                                shopsData.add(shopsModel);
                            }
                        }
                        handler.sendEmptyMessage(messageCode);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    return;
                }
            }
        }
    }
    public void onSuccess(){}
    public void onError(){
        LevelLog.log(LevelLog.ERROR,"ResponseExtractor","JsonArray Explained failed");
    }
}
