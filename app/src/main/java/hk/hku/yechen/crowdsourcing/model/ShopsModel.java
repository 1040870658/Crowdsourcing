package hk.hku.yechen.crowdsourcing.model;

import android.os.Parcel;
import android.os.Parcelable;

import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/3/3.
 */

public class ShopsModel implements Parcelable{
    private String image;
    private long id;
    private String address;
    private String pysicalAdd;
    private String name;
    private String distance;
    private String duration;

    protected ShopsModel(Parcel in) {
        image = in.readString();
        id = in.readLong();
        address = in.readString();
        pysicalAdd = in.readString();
        name = in.readString();
        distance = in.readString();
        duration = in.readString();
    }

    public static final Creator<ShopsModel> CREATOR = new Creator<ShopsModel>() {
        @Override
        public ShopsModel createFromParcel(Parcel in) {
            return new ShopsModel(in);
        }

        @Override
        public ShopsModel[] newArray(int size) {
            return new ShopsModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeLong(id);
        dest.writeString(address);
        dest.writeString(pysicalAdd);
        dest.writeString(name);
        dest.writeString(distance);
        dest.writeString(duration);
    }
    public ShopsModel(String image, long id, String address, String pysicalAdd, String name, String distance, String duration) {
        this.image = "http://"+ NetworkPresenter.ip_address+"/"+image;
        this.id = id;
        this.address = address;
        this.pysicalAdd = pysicalAdd;
        this.name = name;
        this.distance = distance;
        this.duration = duration;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPysicalAdd(String pysicalAdd) {
        this.pysicalAdd = pysicalAdd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getPysicalAdd() {
        return pysicalAdd;
    }

    public String getName() {
        return name;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String toString(){
        return name+"   \t"+distance+"  \t"+duration+"  \t";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((ShopsModel)obj).getId();
    }
}
