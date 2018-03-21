package hk.hku.yechen.crowdsourcing.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

import hk.hku.yechen.crowdsourcing.util.LevelLog;

/**
 * Created by yechen on 2018/3/4.
 */

public class PullRecyclerViewListener extends RecyclerView.OnScrollListener {
    //private ImageLoader imageLoader;
    public static final int SHOW_HEADER = 0x11000001;
    public static final int SHOW_TAILER = 0x11000002;
    private Context context;
    private Handler handler;
    public  int page = 1;

    private final boolean pauseOnScroll;

    public PullRecyclerViewListener( Context context, Handler handler, boolean pauseOnScroll) {
        super();
        this.pauseOnScroll = pauseOnScroll;
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState){
            case RecyclerView.SCROLL_STATE_IDLE:
                //imageLoader.resume();
                Glide.with(context).resumeRequests();
                if(isVisBottom(recyclerView)){
                    Message msg = new Message();
                    msg.arg1 = ++page;
                    msg.what = SHOW_TAILER;
                    handler.sendMessage(msg);
                }
                else if(isTappingTop(recyclerView)){
                    Message msg = new Message();
                    msg.what = SHOW_HEADER;
                    handler.sendMessage(msg);
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if(pauseOnScroll) {
                    Glide.with(context).pauseRequests();
                    //imageLoader.pause();
                }
                break;
        }
    }
    public static boolean isVisBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.getScrollState()){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isTappingTop(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        int visibleItemCount = layoutManager.getChildCount();

        int state = recyclerView.getScrollState();

        if(visibleItemCount > 0 && firstVisibleItemPosition == 0 && state == recyclerView.getScrollState()){
            return true;
        }
        else{
            return false;
        }
    }
}