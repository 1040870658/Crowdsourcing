package hk.hku.yechen.crowdsourcing.presenter;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ExecutorService;

/**
 * Created by yechen on 2018/1/13.
 */

public class ShopPresenter implements Presenter {
    private ExecutorService executorService;
    private IView iView;
    private NetworkPresenter networkPresenter;

    public ShopPresenter(ExecutorService executorService,IView iView){
        this.iView = iView;
        this.executorService = executorService;
    }
    private class ShopHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NetworkPresenter.SHOP_SUCCESS:

            }
        }
    }
    @Override
    public void fetchData() {
        executorService.submit(networkPresenter);
    }

}
