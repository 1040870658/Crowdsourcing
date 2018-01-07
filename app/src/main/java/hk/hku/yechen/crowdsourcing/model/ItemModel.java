package hk.hku.yechen.crowdsourcing.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yechen on 2017/11/23.
 */

/**
 * ItemModel is for an order which consists of commodity
 */
public class ItemModel implements Parcelable{
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private LatLng start;
    private LatLng end;
    private int id;
    private CommodityModel commodityModel;

    protected ItemModel(Parcel in) {
        this.id = in.readInt();
        this.commodityModel = new CommodityModel(in);
        this.startLat = in.readDouble();
        this.startLng = in.readDouble();
        this.endLat = in.readDouble();
        this.endLng = in.readDouble();
        start = new LatLng(startLat,startLng);
        end = new LatLng(endLat,endLng);
    }

    public LatLng getStart(){
        return start;
    }
    public LatLng getEnd(){
        return end;
    }
    public ItemModel(int id, CommodityModel commodityModel,LatLng start,LatLng end) {
        this.id = id;
        this.commodityModel = commodityModel;
        this.start = start;
        this.end = end;
        this.startLng = start.longitude;
        this.startLat = start.latitude;
        this.endLat = end.latitude;
        this.endLng = end.longitude;
    }

    public static final Creator<ItemModel> CREATOR = new Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel in) {
            return new ItemModel(in);
        }

        @Override
        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        commodityModel.writeToParcel(dest,flags);
        dest.writeDouble(startLat);
        dest.writeDouble(startLng);
        dest.writeDouble(endLat);
        dest.writeDouble(endLng);
    }
}
