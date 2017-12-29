package hk.hku.yechen.crowdsourcing.model;

import android.os.Parcel;
import android.os.Parcelable;

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
    private String address;
    private String images;
    private String distance;
    private ItemModel itemModel;

    public DestinationModel(long timeCost, double[] pricesEarn, String name, String address, String images, String distance, ItemModel itemModel) {
        this.timeCost = timeCost;
        this.pricesEarn = pricesEarn;
        this.name = name;
        this.address = address;
        this.images = images;
        this.distance = distance;
        this.itemModel = itemModel;
    }

    protected DestinationModel(Parcel in) {
        this.timeCost = in.readLong();
        in.readDoubleArray(this.pricesEarn);
        this.itemModel = in.readParcelable(ItemModel.class.getClassLoader());
        this.name = in.readString();
        this.address = in.readString();
        this.images = in.readString();
        this.distance = in.readString();
    }
    public ItemModel getItem(){
        return itemModel;
    }

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
        dest.writeParcelable(itemModel,Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(images);
        dest.writeString(distance);
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
