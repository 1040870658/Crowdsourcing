package hk.hku.yechen.crowdsourcing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2017/11/23.
 */

public class DestinationModel implements Parcelable {
    private static final int WALK = 0;
    private static final int TAXI = 1;
    private static final int BUS = 2;

    private long timeCost;
    private double[] pricesEarn;
    private String name;
    private String images;
    private String distance;
    private OrderModel orderModel;
    private PolylineOptions polylineOptions;
    private String timeConsuming;

    //for temporary testing
    private int imageID;
    public DestinationModel(long timeCost, double[] pricesEarn, String name, String images, String distance, OrderModel orderModel) {
        this.timeCost = timeCost;
        this.pricesEarn = pricesEarn;
        this.name = name;
        this.images = images;
        this.distance = distance;
        this.orderModel = orderModel;
        this.polylineOptions = new PolylineOptions();
    }
    public DestinationModel(long timeCost, double[] pricesEarn, String name, String images,int imageID, String distance, OrderModel orderModel) {
        this.timeCost = timeCost;
        this.pricesEarn = pricesEarn;
        this.name = name;
        this.images = images;
        this.imageID = imageID;
        this.distance = distance;
        this.orderModel = orderModel;
        this.polylineOptions = new PolylineOptions();
        setTimeConsuming(timeCost);
    }

    public DestinationModel(long timeCost, double pricesEarn, String name, String images,String distance, OrderModel orderModel) {
        this.timeCost = timeCost;
        this.pricesEarn = new double[3];
        DecimalFormat df = new DecimalFormat("0.00");
        this.pricesEarn[WALK] = Double.valueOf(df.format(pricesEarn));
        this.pricesEarn[TAXI] = Double.valueOf(df.format(pricesEarn));
        this.pricesEarn[BUS] = Double.valueOf(df.format(pricesEarn));

        this.name = name;
        this.images = "http://"+ NetworkPresenter.ip_address+"/"+images;
        this.distance = distance;
        this.orderModel = orderModel;
        this.polylineOptions = new PolylineOptions();
        setTimeConsuming(timeCost);
    }

    public void setDistance(String distance){
        this.distance = distance;
    }

    public String getTimeConsuming(){
        return timeConsuming;
    }
    public void setTimeCost(long timeCost){
        this.timeCost = timeCost;
        setTimeConsuming(timeCost);
    }
    public void setTimeConsuming(long timeCost){
        long tmp = timeCost;
        int count = 0;
        while(tmp >= 60){
            tmp /= 60;
            count ++;
        }
        if(count == 0){
            timeConsuming = timeCost +" s";
        }
        if(count == 1){
            timeConsuming = timeCost / 60 + " min";
        }
        if(count == 2){
            timeConsuming = timeCost / 60 / 60 + " h " + timeCost / 60 + " min";
        }
    }
    public PolylineOptions getPolylineOptions(){
        return polylineOptions;
    }
    protected DestinationModel(Parcel in) {
        this.timeCost = in.readLong();
        in.readDoubleArray(this.pricesEarn);
        this.name = in.readString();
        this.images = in.readString();
        this.distance = in.readString();
        this.orderModel = new OrderModel(in);
        this.imageID = in.readInt();
        this.polylineOptions = PolylineOptions.CREATOR.createFromParcel(in);
    }
    public OrderModel getItem(){
        return orderModel;
    }

    public int getImageID() { return imageID; }
    public String getImages(){
        return images;
    }
    public String getName(){
        return  name;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timeCost);
        dest.writeDoubleArray(pricesEarn);
        dest.writeString(name);
        dest.writeString(images);
        dest.writeString(distance);
        orderModel.writeToParcel(dest,flags);
        dest.writeInt(imageID);
        polylineOptions.writeToParcel(dest,flags);
    }

    public long getTimeCost() {
        return timeCost;
    }

    public double[] getPricesEarn() {
        return pricesEarn;
    }


    public String getDistance() {
        return distance;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DestinationModel> CREATOR = new Creator<DestinationModel>() {
        @Override
        public DestinationModel createFromParcel(Parcel in) {
            return new DestinationModel(in);
        }

        @Override
        public DestinationModel[] newArray(int size) {
            return new DestinationModel[size];
        }
    };
}
