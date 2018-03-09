package hk.hku.yechen.crowdsourcing.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.DestinationModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
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
        HashMap<CommodityModel,Integer> commodities;
        earnMoney[0] = 10;
        earnMoney[1] = 20;
        earnMoney[2] = 30;

        commodities = new HashMap<>();
        commodities.put(new CommodityModel(0,0,"bottle",10,1,88),1);
        DestinationModel destinationModel = new DestinationModel(
                1000,earnMoney,"Starbuck",
                "https://www.google.com.hk/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwiS6orhs6fYAhXGjLwKHb0uDKYQjRwIBw&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2F7-Eleven&psig=AOvVaw2Ge0mVkKjLBNQp6KVqMBGD&ust=1514368319678477",
                R.drawable.starbucks,"800m",
                new OrderModel(1,"0",commodities,
                        new LatLng(22.2831435,114.1358022),new LatLng(22.2831928,114.1381175),
                        "香港大学西营盘大学图书馆大楼旧翼地下","香港大学图书馆"));
        datas.add(destinationModel);

        commodities = new HashMap<>();
        commodities.put(new CommodityModel(0,0,"bottle",10,1,23),2);
        datas.add(new DestinationModel(1000,earnMoney,"7-11",
                "https://www.google.com.hk/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwiS6orhs6fYAhXGjLwKHb0uDKYQjRwIBw&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2F7-Eleven&psig=AOvVaw2Ge0mVkKjLBNQp6KVqMBGD&ust=1514368319678477",
                R.drawable.seven_11,"900m",
                new OrderModel(2,"0",commodities,
                        new LatLng(22.269442,114.129571),new LatLng(22.2831926,114.1381177),
                        "西高山薄扶林道102號","香港大学庄月明大楼")));

        commodities = new HashMap<>();
        commodities.put(new CommodityModel(0,0,"bottle",10,1,33),1);
        datas.add(new DestinationModel(1000,earnMoney,"Welcome",null,R.drawable.wellcome,"1000m",
                new OrderModel(7,"0",commodities,
                        new LatLng(22.282712,114.129371),new LatLng(22.2831920,114.1381181),
                        "香港石塘咀卑路乍街8号","香港大学图书馆")));

        commodities = new HashMap<>();
        commodities.put(new CommodityModel(0,0,"bottle",10,1,100),1);
        datas.add(new DestinationModel(1000,earnMoney,"Mc'Donald",null,R.drawable.mcdonald,"1100m",
                new OrderModel(3,"0",commodities,
                        new LatLng(22.2821878,114.1284681),new LatLng(22.2831918,114.1381187),
                        "香港西环皇后大道西265号地下","香港大学智华馆")));

        commodities = new HashMap<>();
        commodities.put(new CommodityModel(0,0,"bottle",10,1,55),1);
        datas.add(new DestinationModel(1000,earnMoney,"Jhceshop",null,R.drawable.jhceshop,"1500m",
                new OrderModel(4,"0",commodities,
                        new LatLng(22.283432,114.129269),new LatLng(22.2831923,114.1381197),
                        "香港大角咀","香港大学智华馆")));

    }
}
