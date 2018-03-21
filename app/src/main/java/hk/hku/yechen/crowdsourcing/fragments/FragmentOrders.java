package hk.hku.yechen.crowdsourcing.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.adapters.OrderAdapter;
import hk.hku.yechen.crowdsourcing.model.CommodityModel;
import hk.hku.yechen.crowdsourcing.model.DecoratorModel;
import hk.hku.yechen.crowdsourcing.model.OrderModel;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.HoverListDecorator;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import hk.hku.yechen.crowdsourcing.util.LookupHandler;

/**
 * Created by yechen on 2017/11/21.
 */

public class FragmentOrders extends Fragment {
    private PopupWindow popupWindow;
    private View popupView;
    private Button confirm;
    private TextView feedback_comment;
    private ImageView feedback_background;
    private ProgressBar feedback_progress;
    private View contentView;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<List> orders;
    private List<String> titles;
    private ImageView imageView;
    private NetworkPresenter updatePresenter;
    private NetworkPresenter networkPresenter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LookupHandler lookupHandler;
    public static final int LOOKUP_FINISHED = 0x01000001;
    public static final int LOOKUP_FAILED = 0x01000002;
    public static final int UPDATE_SUCCESS =  0x10000003;
    public static int CUSTOMER_TYPE = 0;
    public static int PROVIDER_TYPE = 1;
    private DecoratorModel decoratorModel;
    private HoverListDecorator hoverListDecorator;
    private ExecutorService executorService;
    private Handler handler;
    private ConfirmListener confirmListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_task,container,false);
            executorService = Executors.newSingleThreadExecutor();
            swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.srl_orders);
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.CYAN,Color.GREEN);
            initPopupWindow();
            init();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                }
            });
            imageView = (ImageView) contentView.findViewById(R.id.iv_task_background);
            imageView.setImageResource(R.drawable.back_order);
            orders = new ArrayList<>();
            confirmListener = new ConfirmListener(MainActivity.userModel.getPhone());
            adapter = new OrderAdapter(orders,getActivity(),confirmListener);
            recyclerView = (RecyclerView) contentView.findViewById(R.id.rcv_task);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
          //  recyclerView.addItemDecoration(new SimpleItemDecorator(getActivity(),SimpleItemDecorator.VERTICAL_LIST));
            decoratorModel = new DecoratorModel(getResources(),orders,titles);
            hoverListDecorator = new HoverListDecorator(decoratorModel);
            recyclerView.addItemDecoration(hoverListDecorator);
            lookupHandler = new LookupHandler(adapter,getActivity(),swipeRefreshLayout,decoratorModel);
        }
        return contentView;
    }
    private void init(){
        titles = new ArrayList<>();
        titles.add(getString(R.string.order_title_1));
        titles.add(getString(R.string.order_title_2));
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                popupWindow.dismiss();
                switch (msg.what){
                    case NetworkPresenter.H_FAIL:
                        Toast.makeText(getActivity(),"Illegal operation",Toast.LENGTH_SHORT).show();
                        break;
                    case UPDATE_SUCCESS:
                        getData();
                        break;
                }
            }
        };
    }

    public class ConfirmListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            popupWindow.showAtLocation(contentView, Gravity.BOTTOM|Gravity.CENTER,0,0);

            feedback_background.setVisibility(View.GONE);
            feedback_progress.setVisibility(View.GONE);
            contentView.setAlpha(0.5f);
        }
        private long orderId;
        private int status;
        private String userPhone;
        private float feedback;

        public void setFeedback(float feedback) {
            this.feedback = feedback;
        }

        public ConfirmListener(String userPhone) {
            this.userPhone = userPhone;
            feedback = 5.0f;
        }

        public float getFeedback() {
            return feedback;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public long getOrderId() {
            return orderId;
        }

        public int getStatus() {
            return status;
        }

        public String getUserPhone() {
            return userPhone;
        }
    }

    public void initPopupWindow(){
        if(popupWindow == null){
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            popupWindow = new PopupWindow(getActivity());
            popupWindow.setWidth(displayMetrics.widthPixels);
            popupWindow.setHeight(displayMetrics.heightPixels/7*2);
            popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_feedback,null);
            feedback_comment = (TextView) popupView.findViewById(R.id.tv_feedback_comment);
            feedback_background = (ImageView) popupView.findViewById(R.id.iv_feedback);
            feedback_progress = (ProgressBar) popupView.findViewById(R.id.pb_feedback);
            feedback_background.setVisibility(View.GONE);
            feedback_progress.setVisibility(View.GONE);

            ratingBar = (RatingBar) popupView.findViewById(R.id.rb_feedback);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    confirmListener.setFeedback(rating);

                    if(rating >= 0.0){
                        feedback_comment.setText("Terrible");
                    }
                    if(rating > 2.1){
                        feedback_comment.setText("Bad");
                    }
                    if(rating > 2.9){
                        feedback_comment.setText("Good");
                    }
                    if(rating > 4.1){
                        feedback_comment.setText("Perfect");
                    }
                }
            });
            LayerDrawable layerDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
            layerDrawable.getDrawable(2).setColorFilter( getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
            confirm = (Button) popupView.findViewById(R.id.btn_feedback_confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePresenter = new NetworkPresenter(
                            UPDATE_SUCCESS,
                            NetworkPresenter.UrlBuilder.buildConfirmOrder(confirmListener.getOrderId(),
                                    confirmListener.getStatus(),
                                    confirmListener.getUserPhone(),confirmListener.getFeedback()),
                            null,
                            handler,
                            new ResponseExtractor.UpdateOrderStatusExtractor(handler)
                    );
                    feedback_background.setVisibility(View.VISIBLE);
                    feedback_progress.setVisibility(View.VISIBLE);
                    executorService.submit(updatePresenter);
                }
            });
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    contentView.setAlpha(1f);
                }
            });
            popupWindow.setContentView(popupView);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setAnimationStyle(R.style.popup_style);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData(){
         networkPresenter = new NetworkPresenter(
                LOOKUP_FINISHED,
                NetworkPresenter.UrlBuilder.buildOrdersLookup(MainActivity.userModel.getPhone(),CUSTOMER_TYPE),
                null,
                lookupHandler,
                new ResponseExtractor.OrderLookup(lookupHandler,orders));

        executorService.submit(networkPresenter);
    }
}
