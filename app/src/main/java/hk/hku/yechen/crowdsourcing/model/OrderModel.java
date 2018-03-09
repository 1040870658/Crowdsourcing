package hk.hku.yechen.crowdsourcing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yechen on 2017/11/23.
 */

/**
 * OrderModel is for an order which consists of commodity
 */
public class OrderModel implements Parcelable{

    private final int LIMIT = 50;
    private String customerPhone;
    private String providerPhone;
    private String shopAdd;
    private String targetAdd;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private LatLng start;
    private LatLng end;
    private int id;
    private double price;
    private double tips;

    public static int LAUNCHED = 1;

    public static int PICKED = 2;

    public static int COLLECTED = 3;

    public static int ARRIVED = 4;

    public static int FINISHED = 5;

    private int state = LAUNCHED;

    private HashMap<CommodityModel,Integer> commodities;

    public void setPrice(double price){
        this.price = price;
        tips = price / 10;
        if(tips > LIMIT)
            tips = LIMIT;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public double getTips(){
        return tips;
    }
    public double getPrice(){
        return price;
    }
    public String getCommodities(){
        StringBuilder stringBuilder = new StringBuilder();
        for(CommodityModel commodityModel:commodities.keySet()){
            stringBuilder.append(commodityModel.getName());
            stringBuilder.append(" ");
        }
        return new String(stringBuilder);
    }
    public HashMap<CommodityModel,Integer> getCommodityMap(){
        return  commodities;
    }
    protected OrderModel(Parcel in) {
        this.id = in.readInt();
        readHashMap(in);
        this.customerPhone = in.readString();
        this.providerPhone = in.readString();
        this.shopAdd = in.readString();
        this.targetAdd = in.readString();
        this.startLat = in.readDouble();
        this.startLng = in.readDouble();
        this.endLat = in.readDouble();
        this.endLng = in.readDouble();
        this.start = new LatLng(startLat,startLng);
        this.end = new LatLng(endLat,endLng);
        this.setPrice(in.readDouble());
    }

    public LatLng getStart(){
        return start;
    }
    public LatLng getEnd(){
        return end;
    }


    public OrderModel(int id, String customerPhone,HashMap<CommodityModel,Integer> commodities, LatLng start, LatLng end,
                      String shopAdd,String targetAdd){
        this.id = id;
        this.commodities = commodities;
        this.customerPhone = customerPhone;
        this.providerPhone = "0";
        this.start = start;
        this.end = end;
        this.startLng = start.longitude;
        this.startLat = start.latitude;
        this.endLat = end.latitude;
        this.endLng = end.longitude;
        this.targetAdd = targetAdd;
        this.shopAdd = shopAdd;
    }
    public OrderModel(int id, String customerPhone,String providerPhone,HashMap<CommodityModel,Integer> commodities, LatLng start, LatLng end,
                      String shopAdd,String targetAdd,double price){
        this.id = id;
        this.commodities = commodities;
        this.customerPhone = customerPhone;
        this.providerPhone = providerPhone;
        this.start = start;
        this.end = end;
        this.startLng = start.longitude;
        this.startLat = start.latitude;
        this.endLat = end.latitude;
        this.endLng = end.longitude;
        this.targetAdd = targetAdd;
        this.shopAdd = shopAdd;
        this.price = price;
    }
    public OrderModel(int id, String customerPhone,HashMap<CommodityModel,Integer> commodities, LatLng start, LatLng end,
                      String shopAdd,String targetAdd,double price){
        this.id = id;
        this.customerPhone = customerPhone;
        this.providerPhone = "0";
        this.commodities = commodities;
        this.start = start;
        this.end = end;
        this.startLng = start.longitude;
        this.startLat = start.latitude;
        this.endLat = end.latitude;
        this.endLng = end.longitude;
        this.targetAdd = targetAdd;
        this.shopAdd = shopAdd;
        setPrice(price);
    }


    public void setShopAdd(String shopAdd) {
        this.shopAdd = shopAdd;
    }

    public void setTargetAdd(String targetAdd) {
        this.targetAdd = targetAdd;
    }

    public String getShopAdd() {
        return shopAdd;
    }

    public String getTargetAdd() {
        return targetAdd;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    private void readHashMap(Parcel in){
        int size = in.readInt();
        commodities = new HashMap<>(2*size);
        for(int i = 0;i < size;i ++){
            commodities.put(new CommodityModel(in),in.readInt());
        }
    }

    private void writeHashMap(Parcel dest,int flags){
        int size = commodities.size();
        dest.writeInt(size);
        for(CommodityModel commodityModel:commodities.keySet()){
            commodityModel.writeToParcel(dest,flags);
            dest.writeInt(commodities.get(commodityModel));
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        writeHashMap(dest,flags);
        dest.writeString(customerPhone);
        dest.writeString(providerPhone);
        dest.writeString(shopAdd);
        dest.writeString(targetAdd);
        dest.writeDouble(startLat);
        dest.writeDouble(startLng);
        dest.writeDouble(endLat);
        dest.writeDouble(endLng);
        dest.writeDouble(price);
    }

    public void setProviderPhone(String providerPhone){
        this.providerPhone = providerPhone;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

}
