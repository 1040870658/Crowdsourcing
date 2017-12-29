package hk.hku.yechen.crowdsourcing.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yechen on 2017/12/18.
 */

public class CommodityModel implements Parcelable {

    private String name;
    private double price;
    private String imgURL;

    public CommodityModel(String name, double price, int imgID) {
        this.name = name;
        this.price = price;
        this.imgID = imgID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImgURL() {
        return imgURL;
    }

    public int getImgID() {
        return imgID;
    }

    //Temporary for testing
    private int imgID;

    protected CommodityModel(Parcel in) {
        this.name = in.readString();
        this.price = in.readDouble();
        this.imgURL = in.readString();
        this.imgID = in.readInt();
    }

    public static final Creator<CommodityModel> CREATOR = new Creator<CommodityModel>() {
        @Override
        public CommodityModel createFromParcel(Parcel in) {
            return new CommodityModel(in);
        }

        @Override
        public CommodityModel[] newArray(int size) {
            return new CommodityModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(imgURL);
        dest.writeInt(imgID);
    }

}
