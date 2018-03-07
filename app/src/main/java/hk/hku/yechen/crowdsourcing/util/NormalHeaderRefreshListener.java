package hk.hku.yechen.crowdsourcing.util;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;

/**
 * Created by yechen on 2018/3/5.
 */

public class NormalHeaderRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    private WeakReference<Handler> weakReference;

    public NormalHeaderRefreshListener(Handler handler){
        weakReference = new WeakReference<Handler>(handler);
    }

    @Override
    public void onRefresh() {

    }
}
