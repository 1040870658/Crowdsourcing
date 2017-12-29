package hk.hku.yechen.crowdsourcing.presenter;

import java.util.List;

import hk.hku.yechen.crowdsourcing.model.DestinationModel;
import hk.hku.yechen.crowdsourcing.myviews.RecommendView;

/**
 * Created by yechen on 2017/12/5.
 */

public interface RecommendIPresenter extends Runnable{
    public void recommend();
    public void initialMap(List<DestinationModel> datas);
}
