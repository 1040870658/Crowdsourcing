package hk.hku.yechen.crowdsourcing.presenter;

import android.os.Handler;

import java.util.List;

import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/3/5.
 */

public class ReloadPresenter<T> implements Runnable {
    private List<T> oldVal;
    private List<T> newVal;
    private Handler handler;
    private int message;
    public ReloadPresenter(List<T> oldVal, List<T> newVal, Handler handler, int messageCode){
        this.oldVal = oldVal;
        this.newVal = newVal;
        this.handler = handler;
        this.message = messageCode;
    }
    private void reload(){
        int sep = 0;
        synchronized (oldVal){
            if(oldVal == null || newVal == null || newVal.size() == 0){
                return;
            }
            if(oldVal.size() == 0){
                oldVal.addAll(newVal);
                return;
            }
            for(int i = newVal.size() - 1;i >= 0;i --){
                if(!oldVal.contains(newVal.get(i))) {
                    sep = i;
                    break;
                }
            }
            oldVal.addAll(0,newVal.subList(0,sep));
            handler.sendEmptyMessage(message);
        }
    }
    @Override
    public void run() {
        reload();
    }
}
