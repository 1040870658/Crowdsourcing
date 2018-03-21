package hk.hku.yechen.crowdsourcing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;

/**
 * Created by yechen on 2018/3/15.
 */

public class OrderPickActivity extends OrderDetailActivity {
    private Button btnPick;
    private ProgressBar progressBar;
    private View backgroundView;
    public static final int START=0x00000001;
    public static final int SUBMIT_SUCCESS = 0x00000002;
    private Handler pickHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickHandler = new PickHandler(this ,orderModel);
        progressBar = (ProgressBar) findViewById(R.id.pb_order_pick);
        backgroundView = findViewById(R.id.ll_order_pick);
        btnPick = (Button) findViewById(R.id.btn_pick);
        btnPick.setVisibility(View.VISIBLE);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                backgroundView.setAlpha(0.5f);
                pickHandler.sendEmptyMessageDelayed(START,1500);
            }
        });
    }
    public void resumeWaiting(){
        progressBar.setVisibility(View.INVISIBLE);
        backgroundView.setAlpha(1f);
    }
    private static class PickHandler extends Handler{
        private WeakReference<OrderModel> orderModel;
        private WeakReference<OrderPickActivity> orderPickActivityWeakReference;
        public PickHandler(OrderPickActivity orderPickActivity ,OrderModel orderModel){
            this.orderModel = new WeakReference<>(orderModel);
            this.orderPickActivityWeakReference = new WeakReference<>(orderPickActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START:
                    NetworkPresenter networkPresenter = new NetworkPresenter(
                            SUBMIT_SUCCESS,
                            NetworkPresenter.UrlBuilder.buildUpdateStatus(
                                    orderModel.get().getId(),
                                    orderModel.get().getState() + 1,
                                    MainActivity.userModel.getPhone()),
                            null,
                            this,
                            new ResponseExtractor.UpdateOrderStatusExtractor(this)
                    );
                    new Thread(networkPresenter).start();
                    break;
                case SUBMIT_SUCCESS:
                    orderPickActivityWeakReference.get().resumeWaiting();
                    Intent intent = new Intent(orderPickActivityWeakReference.get(),MainActivity.class);
                    intent.putExtra("tabs",4);
                    orderPickActivityWeakReference.get().startActivity(intent);
                    orderPickActivityWeakReference.get().finish();
                    break;
                case NetworkPresenter.H_FAIL:
                    Toast.makeText(orderPickActivityWeakReference.get(),"Unexpected Error Occurs",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
