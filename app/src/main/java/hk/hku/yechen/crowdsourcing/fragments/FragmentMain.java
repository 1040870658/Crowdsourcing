package hk.hku.yechen.crowdsourcing.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.MainActivity;
import hk.hku.yechen.crowdsourcing.adapters.BaseAdapter;
import hk.hku.yechen.crowdsourcing.model.DestinationModel;
import hk.hku.yechen.crowdsourcing.myviews.RecommendView;
import hk.hku.yechen.crowdsourcing.presenter.LocationPresenter;
import hk.hku.yechen.crowdsourcing.presenter.NetworkPresenter;
import hk.hku.yechen.crowdsourcing.R;
import hk.hku.yechen.crowdsourcing.presenter.RecommendIPresenter;
import hk.hku.yechen.crowdsourcing.presenter.RecommendPresenter;
import hk.hku.yechen.crowdsourcing.presenter.ResponseExtractor;
import hk.hku.yechen.crowdsourcing.util.Extractor;
import hk.hku.yechen.crowdsourcing.util.LevelLog;
import hk.hku.yechen.crowdsourcing.util.SimpleItemDecorator;
import hk.hku.yechen.crowdsourcing.util.WaypointsTarget;
import okhttp3.Response;


/**
 * Created by yechen on 2017/11/21.
 */

public class FragmentMain extends Fragment implements OnMapReadyCallback,RecommendView{
    private MainActivity activity;
    private RecyclerView recyclerView;
    private PopupWindow popupWindow;
    private DisplayMetrics displayMetrics;
    private View contentView;
    private GoogleMap mMap;
    private View popView;
    private SupportMapFragment mapFragment;
    private List<DestinationModel> datas;
    private PolylineOptions polylineOptions;
    private PolylineOptions directPoly;
    private RecommendIPresenter recommendIPresenter;
    private BaseAdapter<DestinationModel> baseAdapter;
    private RelativeLayout searchHidden;
    private View popupView;
    private SupportPlaceAutocompleteFragment searchFragment;
    private PlaceSelectionListener placeSelectionListener;
    private PlaceSelectionListener oPlaceSelectionListener;
    private PlaceSelectionListener dPlaceSelectionListener;
    private SharedPreferences sharedPreferences;
    private ImageView sOriginImageView;
    private TextView  sOriginTextView;
    private ImageView sDesImageView;
    private TextView sDesTextView;
    private ImageView cancelSearch;
    private ImageView lOriginImageView;
    private ImageView lDesImageView;
    private EditText searchEditText;
    String originLatLng;
    String destinationLatLng;
    String originAddress;
    String destinationAddress;
    ArrayList<MarkerOptions> wayPointsMarkers;
    int orange;
    int green;
    int polyWidth = 5;
    static final int ORIGIN_CODE = 0x10000000;
    static final int DES_CODE = 0x10000001;
    static final int ORIGIN_REVERSE_CODE = 0x10000002;
    static final int DES_REVERSE_CODE = 0x10000003;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wayPointsMarkers = new ArrayList<>();
        wayPointsMarkers.add(new MarkerOptions());
        wayPointsMarkers.add(new MarkerOptions());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(contentView == null) {
            contentView = inflater.inflate(R.layout.activity_maps, null);
            displayMetrics = getResources().getDisplayMetrics();
            orange = getResources().getColor(R.color.orange);
            green = getResources().getColor(R.color.green);
            searchHidden = (RelativeLayout) contentView.findViewById(R.id.rl_search);
            polylineOptions = new PolylineOptions();
            polylineOptions.color(orange);
            polylineOptions.width(polyWidth);
            directPoly = new PolylineOptions();
            directPoly.color(green);
            directPoly.width(polyWidth);
            recommendIPresenter = new RecommendPresenter(this,polylineOptions);
            datas = new ArrayList<>();
//          recommendIPresenter.recommend();
            popView = (View) contentView.findViewById(R.id.ll_pop);
            popView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDes();
                }
            });

            initializeMap();
            selectDes();

            cancelSearch = (ImageView) contentView.findViewById(R.id.iv_cancelSearch);
            cancelSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupView.setAlpha(1f);
                    searchHidden.setVisibility(View.GONE);
                    cancelSearch.setVisibility(View.GONE);
                    popupWindow.showAtLocation(contentView, Gravity.BOTTOM|Gravity.CENTER,0,0);
                }
            });
            oPlaceSelectionListener = new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    sOriginTextView.setText(place.getAddress());
                    originLatLng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                    originAddress = place.getAddress().toString();
                    searchHidden.setVisibility(View.GONE);
                    cancelSearch.setVisibility(View.GONE);
                    if(popupView != null)
                        popupView.setAlpha(1f);
                    popupWindow.showAtLocation(contentView, Gravity.BOTTOM|Gravity.CENTER,0,0);
                }

                @Override
                public void onError(Status status) {
                    Toast.makeText(getContext(),"Error Address",Toast.LENGTH_SHORT).show();
                }
            };

            dPlaceSelectionListener = new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    sDesTextView.setText(place.getAddress());
                    destinationLatLng = place.getLatLng().latitude +"," +place.getLatLng().longitude;
                    destinationAddress = place.getAddress().toString();
                    searchHidden.setVisibility(View.GONE);
                    cancelSearch.setVisibility(View.GONE);
                    if(popupView != null)
                        popupView.setAlpha(1f);
                    popupWindow.showAtLocation(contentView, Gravity.BOTTOM|Gravity.CENTER,0,0);
                }

                @Override
                public void onError(Status status) {

                }
            };
            cancelSearch.setVisibility(View.GONE);
            placeSelectionListener = oPlaceSelectionListener;
            recommendIPresenter.initialMap(datas);
        }
        return contentView;
    }

    private void initializeMap() {
        if (mMap == null) {

            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            searchFragment = (SupportPlaceAutocompleteFragment)
                    getChildFragmentManager().findFragmentById(R.id.fg_searchAutocomplete);

        }
    }

    public void initAutoCompletePlace(SupportPlaceAutocompleteFragment autoFragment,PlaceSelectionListener listener){

        autoFragment.setBoundsBias(new LatLngBounds
                (new LatLng(22.480019,113.898851)
                        , new LatLng(22.527754,114.117012)));
        autoFragment.setOnPlaceSelectedListener(listener);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(originLatLng != null && destinationLatLng != null){
            String[] strings = originLatLng.split(",");
            double latD = Double.valueOf(strings[0]);
            double lngD = Double.valueOf(strings[1]);
            directPoly.add(new LatLng(latD,lngD));
            strings = destinationLatLng.split(",");
            latD = Double.valueOf(strings[0]);
            lngD = Double.valueOf(strings[1]);
            directPoly.add(new LatLng(latD,lngD));
            mMap.addPolyline(directPoly);
            zoomMap(directPoly);
        }
        else {
//        mMap.addPolyline(polylineOptions);

            LatLng hk = new LatLng(22.2829989, 114.13708480000001);
            zoomMap(polylineOptions);

            move(hk);
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
            LevelLog.log(LevelLog.DEBUG,"mylocation","granted");

        }

        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(originLatLng != null) {
            String[] strings = originLatLng.split(",");
            double latD = Double.valueOf(strings[0]);
            double lngD = Double.valueOf(strings[1]);
            move(new LatLng(latD,lngD));
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        activity.getExecutorService().submit(recommendIPresenter);

        readUserInfo();

        if(originAddress != null){
            sOriginTextView.setText(originAddress);
        }
        if(destinationAddress != null){
            sDesTextView.setText(destinationAddress);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == ResponseExtractor.E_SUCCESS){
            //    mMap.clear();
                LatLng s = polylineOptions.getPoints().get(0);

                if(wayPointsMarkers.get(0).getPosition() != null) {
                    mMap.addMarker(wayPointsMarkers.get(0));
                    mMap.addMarker(wayPointsMarkers.get(1));
                }

                mMap.addPolyline(polylineOptions);
                zoomMap(polylineOptions);
                move(s);
            }

            else if(msg.what == ResponseExtractor.D_SUCCESS){
                LatLng s = directPoly.getPoints().get(0);
                LatLng e = directPoly.getPoints().get(directPoly.getPoints().size() - 1);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(s);
                mMap.addMarker(markerOptions);
                markerOptions = new MarkerOptions();
                markerOptions.position(e);
                mMap.addMarker(markerOptions);
                mMap.addPolyline(directPoly);
            }

            else if(msg.what == ResponseExtractor.E_Error){
                Toast.makeText(activity,"Can not find appropriate route",Toast.LENGTH_LONG).show();
            }


            else if(msg.what == ORIGIN_REVERSE_CODE){
                originAddress = (String) msg.obj;
                sOriginTextView.setText(originAddress);
            }

            else if(msg.what == DES_REVERSE_CODE){
                destinationAddress = (String) msg.obj;
                sDesTextView.setText(destinationAddress);
            }

            else{
                LevelLog.log(LevelLog.ERROR,"failed","failed");
            }
        }
    };

    private void selectDes(){
        if(popupWindow == null) {

            popupWindow = new PopupWindow(getActivity());
            popupWindow.setWidth(displayMetrics.widthPixels);
            popupWindow.setHeight(displayMetrics.heightPixels/5*2);
            popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_layout,null);

            sOriginImageView = (ImageView) popupView.findViewById(R.id.iv_searchOrigin);
            sOriginTextView = (TextView) popupView.findViewById(R.id.tv_searchOrigin);
            sDesImageView = (ImageView) popupView.findViewById(R.id.iv_searchDes);
            sDesTextView = (TextView) popupView.findViewById(R.id.tv_searchDes);

            lOriginImageView = (ImageView) popupView.findViewById(R.id.iv_currentOrigin);
            lDesImageView = (ImageView)popupView.findViewById(R.id.iv_currentDes);

            initAutoCompletePlace(searchFragment,placeSelectionListener);

            View.OnClickListener oSearchListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(searchHidden.getVisibility() != View.VISIBLE)
                        searchHidden.setVisibility(View.VISIBLE);
                    searchHidden.setAlpha(0.6f);
                    if(popupView!=null)
                        popupView.setAlpha(0.2f);
                    cancelSearch.setVisibility(View.VISIBLE);
                    if(searchFragment != null){
                        if(searchFragment.getView() != null) {
                            searchFragment.getView().setBackgroundColor(orange);
                            if(searchEditText == null)
                                searchEditText = (EditText) searchFragment.getView().findViewById(R.id.place_autocomplete_search_input);
                            searchEditText.setText("");
                        }
                    }
                    placeSelectionListener = oPlaceSelectionListener;
                    popupWindow.dismiss();
                    initAutoCompletePlace(searchFragment,placeSelectionListener);
                }
            };

            View.OnClickListener dSearchListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(searchHidden.getVisibility() != View.VISIBLE)
                        searchHidden.setVisibility(View.VISIBLE);
                    searchHidden.setAlpha(0.6f);
                    if(popupView != null)
                        popupView.setAlpha(0.2f);
                    cancelSearch.setVisibility(View.VISIBLE);
                    if(searchFragment != null){
                        if(searchFragment.getView() != null) {
                            searchFragment.getView().setBackgroundColor(green);
                            if(searchEditText == null)
                                searchEditText = (EditText) searchFragment.getView().findViewById(R.id.place_autocomplete_search_input);
                            searchEditText.setText("");
                        }
                    }
                    placeSelectionListener = dPlaceSelectionListener;
                    popupWindow.dismiss();
                    initAutoCompletePlace(searchFragment,placeSelectionListener);
                }
            };

            View.OnClickListener loLocateListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng latLng = LocationPresenter.getCurrentLatLng(getActivity());
                    if(latLng == null) {
                        Toast.makeText(activity,"Something wrong with GPS, Try again Later",Toast.LENGTH_LONG).show();
                        return;
                    }
                    originLatLng = latLng.latitude + "," + latLng.longitude;
                    NetworkPresenter networkPresenter = new NetworkPresenter
                            (ORIGIN_REVERSE_CODE,NetworkPresenter.UrlBuilder.buildRGEO(
                                    originLatLng)
                                    , null, handler,ResponseExtractor.BuildReverseGeoExtractor(handler));
                    activity.getExecutorService().submit(networkPresenter);
                    move(latLng);
                }
            };

            View.OnClickListener ldLocateListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng latLng = LocationPresenter.getCurrentLatLng(getActivity());
                    if(latLng == null){
                        Toast.makeText(activity,"Something wrong with GPS, Try again Later",Toast.LENGTH_LONG).show();
                        return;
                    }
                    destinationLatLng = latLng.latitude + "," + latLng.longitude;
                    NetworkPresenter networkPresenter = new NetworkPresenter
                            (DES_REVERSE_CODE,NetworkPresenter.UrlBuilder.buildRGEO(
                                    destinationLatLng)
                                    , null, handler,ResponseExtractor.BuildReverseGeoExtractor(handler));
                    activity.getExecutorService().submit(networkPresenter);
                    move(latLng);
                }
            };

            lOriginImageView.setOnClickListener(loLocateListener);
            lDesImageView.setOnClickListener(ldLocateListener);

            sOriginImageView.setOnClickListener(oSearchListener);
            sDesImageView.setOnClickListener(dSearchListener);

            popupWindow.setContentView(popupView);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(false);
            popupWindow.setAnimationStyle(R.style.popup_style);

            recyclerView = (RecyclerView) popupView.findViewById(R.id.rcv_recommend);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM|Gravity.CENTER,0,0);
    }

    public void move(Place place){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
    }

    public void move(LatLng latLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void zoomMap(PolylineOptions polylineOptions){
        if(polylineOptions == null)
            return;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0;i < polylineOptions.getPoints().size();i ++){
            builder.include(polylineOptions.getPoints().get(i));
        }
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        popupWindow.dismiss();
        popupWindow = null;
    }

    @Override
    public void onDestroyView() {
        writeUserInfo(originLatLng,destinationLatLng,originAddress,destinationAddress);
        super.onDestroyView();
    }

    @Override
    public void onRecommendFinished() {
        move(polylineOptions.getPoints().get(0));
    }

    @Override
    public void onMapInitialFinished() {
        baseAdapter = new BaseAdapter<DestinationModel>(datas) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.dselector;
            }

            @Override
            public void convert(final DestinationModel data, BaseAdapter.GeneralViewHolder viewHolder, final int position) {
                viewHolder.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatLng origin = datas.get(position).getItem().getStart();
                        LatLng des = datas.get(position).getItem().getEnd();
                        NetworkPresenter networkPresenter;
                        NetworkPresenter directPresenter;
                        mMap.clear();
                        if(originLatLng == null || destinationLatLng == null) {
                            directPoly = new PolylineOptions();
                            directPoly.color(green);
                            directPoly.width(polyWidth);

                            Extractor responseExtractor = ResponseExtractor.BuildRouteExtractor(directPoly,handler);
                            networkPresenter = new NetworkPresenter
                                    (ResponseExtractor.D_SUCCESS,NetworkPresenter.UrlBuilder.buildRoute(
                                            origin.latitude + "," + origin.longitude,
                                            des.latitude + "," + des.longitude,
                                            "walking")
                                            , null, handler,responseExtractor);
                            activity.getExecutorService().submit(networkPresenter);
                        }
                        else{
                            polylineOptions = new PolylineOptions();
                            polylineOptions.color(orange);
                            polylineOptions.width(polyWidth);
                            directPoly = new PolylineOptions();
                            directPoly.color(green);
                            directPoly.width(polyWidth);
                            networkPresenter = new NetworkPresenter (ResponseExtractor.E_SUCCESS,NetworkPresenter.UrlBuilder.buildRoute(originLatLng,
                                 destinationLatLng,
                                 "walking",des.latitude + "%2C"
                                         + des.longitude + "%7C"+
                                         origin.latitude + "%2C" + origin.longitude)
                                 ,null,handler, ResponseExtractor.BuildRouteExtractor(polylineOptions,handler));
                            wayPointsMarkers.get(0).position(new LatLng(origin.latitude,origin.longitude))
                                    .title(data.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                            wayPointsMarkers.get(1).position(new LatLng(des.latitude,des.longitude))
                                    .title(data.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            directPresenter = new NetworkPresenter(ResponseExtractor.D_SUCCESS,
                                    NetworkPresenter.UrlBuilder.buildRoute(
                                            originLatLng,destinationLatLng,
                                            "walking")
                                    , null, handler,ResponseExtractor.BuildRouteExtractor(directPoly,handler));
                            activity.getExecutorService().submit(networkPresenter);
                            activity.getExecutorService().submit(directPresenter);
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        };
        recyclerView.setAdapter(baseAdapter);
        recyclerView.addItemDecoration(new SimpleItemDecorator(getContext(),SimpleItemDecorator.VERTICAL_LIST));

    }

    private void writeUserInfo(String originLatLng,String destinationLatLng,String originAddress,String  destinationAddress){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.shared_originLatLng,originLatLng);
        editor.putString(MainActivity.shared_desLatLng,destinationLatLng);
        editor.putString(MainActivity.shared_originAddress,originAddress);
        editor.putString(MainActivity.shared_desAddress,destinationAddress);
        editor.apply();
    }
    private void readUserInfo(){
        sharedPreferences = activity.getSharedPreferences(MainActivity.shared_table,Context.MODE_PRIVATE);
        originLatLng = sharedPreferences.getString(MainActivity.shared_originLatLng,null);
        destinationLatLng = sharedPreferences.getString(MainActivity.shared_desLatLng,null);
        originAddress = sharedPreferences.getString(MainActivity.shared_originAddress,null);
        destinationAddress = sharedPreferences.getString(MainActivity.shared_desAddress,null);
    }
}

