package hk.hku.yechen.crowdsourcing.presenter;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.DestinationModel;
import hk.hku.yechen.crowdsourcing.model.ItemModel;
import hk.hku.yechen.crowdsourcing.myviews.RecommendView;

/**
 * Created by yechen on 2017/12/5.
 */

public class RecommendPresenter implements RecommendIPresenter,Runnable {
    private WeakReference<RecommendView> viewWeakReference;
    private PolylineOptions polylineOptions;
    private Handler handler;
    public RecommendPresenter(RecommendView recommendView, PolylineOptions polylineOptions){
        this.viewWeakReference = new WeakReference<RecommendView>(recommendView);
        this.polylineOptions = polylineOptions;
        handler = new Handler(Looper.getMainLooper());
    }
    @Override
    public void recommend() {
        polylineOptions.add(new LatLng(22.283257,114.136774));
        polylineOptions.add(new LatLng(22.283208,114.138175));
        if(viewWeakReference.get() != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    viewWeakReference.get().onRecommendFinished();
                }
            });
        }
    }
    public void initialMap(List<DestinationModel> datas){
        test(datas);
        if(viewWeakReference.get() !=null)
            viewWeakReference.get().onMapInitialFinished();
    }
    public void run(){
        recommend();
    }
    public void test(List<DestinationModel> datas){
        double[] earnMoney = new double[3];
        earnMoney[0] = 10;
        earnMoney[1] = 20;
        earnMoney[2] = 30;
        DestinationModel destinationModel = new DestinationModel(
                1000,earnMoney,"7-11","7-11",
                "https://www.google.com.hk/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwiS6orhs6fYAhXGjLwKHb0uDKYQjRwIBw&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2F7-Eleven&psig=AOvVaw2Ge0mVkKjLBNQp6KVqMBGD&ust=1514368319678477",
                "800m",
                new ItemModel(1,new CommodityModel("bottle",10,1),new LatLng(22.283257,114.136784),new LatLng(22.2831928,114.1381175)));
        datas.add(destinationModel);
        datas.add(new DestinationModel(1000,earnMoney,"7-11","7-11",
                "https://www.google.com.hk/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwiS6orhs6fYAhXGjLwKHb0uDKYQjRwIBw&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2F7-Eleven&psig=AOvVaw2Ge0mVkKjLBNQp6KVqMBGD&ust=1514368319678477",
                "900m",
                new ItemModel(2,new CommodityModel("bottle",10,1),new LatLng(22.283569,114.136755),new LatLng(22.2831926,114.1381177))));
        datas.add(new DestinationModel(1000,earnMoney,"7-11","7-11",null,"1000m",
                new ItemModel(7,new CommodityModel("bottle",10,1),new LatLng(22.283775,114.136746),new LatLng(22.2831920,114.1381181))));
        datas.add(new DestinationModel(1000,earnMoney,"7-11","7-11",null,"1100m",
                new ItemModel(3,new CommodityModel("bottle",10,1),new LatLng(22.283983,114.136777),new LatLng(22.2831918,114.1381187))));
        datas.add(new DestinationModel(1000,earnMoney,"7-11","7-11",null,"1500m",
                new ItemModel(4,new CommodityModel("bottle",10,1),new LatLng(22.285290,114.136728),new LatLng(22.2831923,114.1381197))));

    }
}
