package hk.hku.yechen.crowdsourcing.util;

import android.os.Handler;
import android.support.v4.view.ViewPager;

import static hk.hku.yechen.crowdsourcing.util.AdHandler.ROLLING;
import static hk.hku.yechen.crowdsourcing.util.AdHandler.ROLLING_SPEED;
import static hk.hku.yechen.crowdsourcing.util.AdHandler.STOPROLLING;

/**
 * Created by yechen on 2017/12/15.
 */

public class AdPageChangedListener implements ViewPager.OnPageChangeListener {
    private Handler handler;
    public AdPageChangedListener(Handler handler){
        this.handler = handler;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                handler.sendEmptyMessageDelayed(ROLLING, ROLLING_SPEED);
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                handler.sendEmptyMessage(STOPROLLING);
            default:
                break;
        }
    }
}
