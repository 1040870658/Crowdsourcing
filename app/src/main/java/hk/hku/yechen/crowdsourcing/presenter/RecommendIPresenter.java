package hk.hku.yechen.crowdsourcing.presenter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import hk.hku.yechen.crowdsourcing.model.DestinationModel;
import hk.hku.yechen.crowdsourcing.myviews.RecommendView;

/**
 * Created by yechen on 2017/12/5.
 */

public interface RecommendIPresenter extends Runnable{
    public void recommend(String src, String des);
    public void initialMap(List<DestinationModel> datas);
}
