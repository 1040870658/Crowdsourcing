package hk.hku.yechen.crowdsourcing.model;

import android.os.Parcel;
import android.os.Parcelable;

import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2017/12/18.
 */

/**
 * CommodityModel is for presenting the data of item sale by shops*
 */
public class CommodityModel implements Parcelable {

    private long commodityId;
    private long shopId;
    private String name;
    private double price;
    private String imgURL;
    private int avaNum;

    public CommodityModel(long commodityId,long shopId, String name, double price, int imgID,int avaNum) {
        this.commodityId = commodityId;
        this.shopId = shopId;
        this.name = name;
        this.price = price;
        this.imgID = imgID;
        this.avaNum = avaNum;
    }
    public CommodityModel(long commodityId,long shopId,String name, double price, String imgURL,int avaNum) {
        this.commodityId = commodityId;
        this.shopId = shopId;
        this.name = name;
        this.price = price;
        this.imgURL = "http://"+ NetworkPresenter.ip_address+"/"+imgURL;
        this.avaNum = avaNum;
    }
    public int getAvaNum(){ return  avaNum; }
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImgURL() {
        return imgURL;
    }


    public long getCommodityId() {
        return commodityId;
    }

    public long getShopId() {
        return shopId;
    }

    public int getImgID() {
        return imgID;
    }

    //Temporary for testing
    private int imgID;

    protected CommodityModel(Parcel in) {
        this.commodityId = in.readLong();
        this.shopId = in.readLong();
        this.name = in.readString();
        this.price = in.readDouble();
        this.imgURL = in.readString();
        this.imgID = in.readInt();
        this.avaNum = in.readInt();
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
        dest.writeLong(commodityId);
        dest.writeLong(shopId);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(imgURL);
        dest.writeInt(imgID);
        dest.writeInt(avaNum);
    }

    @Override
    public boolean equals(Object obj) {
        return this.commodityId == ((CommodityModel)obj).getCommodityId();
    }
}
