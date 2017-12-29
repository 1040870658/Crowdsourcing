package hk.hku.yechen.crowdsourcing.util;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;

/**
 * Created by yechen on 2017/12/15.
 */

public class AdHandler extends Handler {
    public static final int ROLLING = 0x00000101;
    public static final int STOPROLLING = 0x00000100;
    public static final int ROLLING_SPEED = 2000;

    private ViewPager viewPager;
    private int size;
    public AdHandler(ViewPager viewPager,int size) {
        this.viewPager = viewPager;
        this.size = size;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ROLLING:
                int item = viewPager.getCurrentItem();
                if(size != 0) {
                    item = (item + 1) % size;
                }
                else{
                    item = 0;
                }
                viewPager.setCurrentItem(item, true);
                break;
            case STOPROLLING:
                if (hasMessages(ROLLING))
                    removeMessages(ROLLING);
            default:
                break;
        }
    }
}
