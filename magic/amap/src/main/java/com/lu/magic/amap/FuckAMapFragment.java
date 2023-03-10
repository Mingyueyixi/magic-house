package com.lu.magic.amap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lu.code.amagic.R;
import com.lu.magic.bean.AMapConfig;
import com.lu.magic.main.AppListModel;
import com.lu.magic.ui.BaseFragment;
import com.lu.magic.ui.BaseToolBarActivity;
import com.lu.magic.ui.recycler.MultiAdapter;
import com.lu.magic.ui.recycler.MultiViewHolder;
import com.lu.magic.ui.recycler.SimpleItemType;
import com.lu.magic.util.AMapUtil;
import com.lu.magic.util.CollectionUtil;
import com.lu.magic.util.LocationUtil;
import com.lu.magic.util.SingleClassStoreUtil;
import com.lu.magic.util.ToastUtil;
import com.lu.magic.config.ConfigUtil;
import com.lu.magic.util.dialog.DialogUtil;
import com.lu.magic.util.dialog.EditDialog;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.permission.PermissionUtil;
import com.lu.magic.util.view.ViewUtil;
import java.util.ArrayList;
import java.util.List;

public class FuckAMapFragment extends BaseFragment implements LocationSource {

    private MapView mMapView;
    private RecyclerView mListView;
    private View mTvPrompt;
    private AMap mAMap;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationClientOption;
    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    private GeocodeSearch mGeocodeSearch;
    private PoiSearch.Query mQuery;
    private PoiSearch mPoiSearch;

    private Marker mCurMarker;
    private Marker mLocationMarker;
    private boolean isItemClickAction;
    private SearchPoiAdapter mSearchPoiAdapter;
    private AMapLocationListener mMapLocationListener;
    private TextView mTvCurrLatLng;
    private AppListModel appListModel;
    private AMapConfig mAMapConfig;
    /**
     * ???????????????poi??????
     */
    private PoiSearch.OnPoiSearchListener mOnPoiSearchListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appListModel = SingleClassStoreUtil.get(AppListModel.class);
        mAMapConfig = ConfigUtil.getAMapConfig(appListModel.getPackageName());
        if (mAMapConfig == null) {
            mAMapConfig = new AMapConfig();
        }
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_layout_amap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.map_view);
        mTvPrompt = view.findViewById(R.id.tv_prompt);
        mListView = view.findViewById(R.id.list_view_poi);
        mTvCurrLatLng = view.findViewById(R.id.tv_curr_latLng);
        mListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mSearchPoiAdapter = new SearchPoiAdapter();
        mSearchPoiAdapter.addItemType(new SimpleItemType<PoiItem>() {
            @NonNull
            @Override
            public MultiViewHolder<PoiItem> createViewHolder(@NonNull MultiAdapter<PoiItem> adapter, @NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_poi, parent, false);
                return new MultiViewHolder<PoiItem>(itemView) {
                    private TextView tvTitle;
                    private TextView tvSubTitle;
                    private CheckBox cvCheckBox;

                    @Override
                    public void onInit() {
                        tvTitle = itemView.findViewById(R.id.tv_title);
                        tvSubTitle = itemView.findViewById(R.id.tv_title_sub);
                        cvCheckBox = itemView.findViewById(R.id.cv_check);
                        cvCheckBox.setClickable(false);

                        itemView.setOnClickListener(v -> {
                            int lastPosition = mSearchPoiAdapter.getSelectedPosition();
                            int position = getLayoutPosition();
                            if (position != lastPosition) {
                                PoiItem poiItem = mSearchPoiAdapter.getItem(position);
                                if (poiItem != null) {
                                    LatLng latLng = AMapUtil.newLatLng(poiItem.getLatLonPoint());
                                    isItemClickAction = true;
                                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                                }
                                mSearchPoiAdapter.notifyItemChanged(lastPosition);
                                mSearchPoiAdapter.setSelectedPosition(position);
                                mSearchPoiAdapter.notifyItemChanged(position);
                            }

                        });
                    }

                    @Override
                    public void onBindView(@NonNull MultiAdapter<PoiItem> adapter, PoiItem itemModel, int position) {
                        PoiItem poiItem = adapter.getItem(position);

                        tvTitle.setText(poiItem.getTitle());
                        tvSubTitle.setText(mSearchPoiAdapter.poiItemToString(poiItem));

                        if (position == mSearchPoiAdapter.getSelectedPosition()) {
                            cvCheckBox.setChecked(true);
                            cvCheckBox.setVisibility(View.VISIBLE);
                        } else {
                            cvCheckBox.setChecked(false);
                            cvCheckBox.setVisibility(View.INVISIBLE);
                        }

                        ViewUtil.setVisibility(View.VISIBLE, tvSubTitle);
                    }
                };
            }
        });
        mListView.setAdapter(mSearchPoiAdapter);

        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.setLocationSource(this);
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        mAMap.setMyLocationEnabled(true);

        mAMap.setOnCameraChangeListener(new MyOnCameraChangeListener());
        mAMap.setOnMapLoadedListener(new MyOnMapLoadedListener());

        try {
            mGeocodeSearch = new GeocodeSearch(getContext().getApplicationContext());
            mGeocodeSearch.setOnGeocodeSearchListener(new MyOnGeocodeSearchListener());
        } catch (AMapException e) {
            e.printStackTrace();
        }

        requestPermissions();
    }

    private void requestPermissions() {
        // ????????????
        PermissionUtil.Companion.permission(
                10086,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        ).onResult((requestCode, result, allGranted) -> {
            if (10086 != requestCode) {
                return;
            }
            boolean prompt = false;
            for (int value : result.values()) {
                if (value == PackageManager.PERMISSION_DENIED) {
                    prompt = true;
                    break;
                }
            }
            if (prompt) {
                ToastUtil.show("??????????????????,????????????????????????");
            }
        }).call(getContext());

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_location, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        View closeBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearchQuery(query);
                closeBtn.performClick();
                closeBtn.performClick();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Activity activity =  getActivity();
        if (activity == null) {
            return;
        }
        if (activity instanceof BaseToolBarActivity) {
            ((BaseToolBarActivity) activity).getToolBar().setNavigationOnClickListener(v -> {
                onBackPressed();
            });

        }
     }

    @Override
    public boolean onBackPressed() {
        LatLng lanLng = mAMap.getCameraPosition().target;
        AMapConfig aMapConfig = ConfigUtil.getAMapConfig(appListModel.getPackageName());
        boolean hasLocalConfig = true;
        if (aMapConfig == null) {
            hasLocalConfig = false;
            aMapConfig = new AMapConfig();
        }

        final AMapConfig finalAMapConfig = aMapConfig;
        if (hasLocalConfig
                && finalAMapConfig.getLng() == lanLng.longitude
                && finalAMapConfig.getLat() == lanLng.latitude) {
            //????????????
            ToastUtil.show("??????????????????");
            requireActivity().finish();
        } else {
            //???????????????????????????????????????
            finalAMapConfig.setLng(lanLng.longitude);
            finalAMapConfig.setLat(lanLng.latitude);

            EditDialog.Builder builder = DialogUtil.buildEditDialog(getContext());
            builder.setTitle("????????????????????????????????????")
                    .setContent((lanLng.longitude + "," + lanLng.latitude))
                    .setContentHint("?????????????????????????????????,??????")
                    .setNegativeButton("??????", (dialog, which) -> {
                        dialog.dismiss();
                        requireActivity().finish();
                    })
                    .setPositiveButton("??????", (dialog, which) -> {
                        String text = builder.getEditText().getText() + "";
                        Location location = LocationUtil.parseLngLat(text);
                        if (location == null) {
                            ToastUtil.show("????????????");
                            return;
                        }
                        finalAMapConfig.setLng(location.getLongitude());
                        finalAMapConfig.setLat(location.getLatitude());
                        ConfigUtil.setAMapConfig(appListModel.getPackageName(), finalAMapConfig);
                        dialog.dismiss();
                        requireActivity().finish();
                    })
                    .show();

        }
        return true;
    }

    @Override
    public void onDestroyView() {
        if (mAMap != null) {
            mAMap.setOnCameraChangeListener(null);
            mAMap.setOnMapLoadedListener(null);
            mAMap.setOnMyLocationChangeListener(null);
            mAMap.setLocationSource(null);
        }
        if (mPoiSearch != null) {
            mOnPoiSearchListener = null;
            mPoiSearch.setOnPoiSearchListener(null);
        }
        if (mCurMarker != null) {
            mCurMarker.destroy();
        }
        if (mGeocodeSearch != null) {
            mGeocodeSearch.setOnGeocodeSearchListener(null);
        }
        if (null != mLocationClient) {
            mLocationClient.unRegisterLocationListener(mMapLocationListener);
            mLocationClient.setLocationListener(null);
            mLocationClient.onDestroy();
        }

        mOnLocationChangedListener = null;
        mMapLocationListener = null;
        mMapView.onDestroy();

        super.onDestroyView();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * ???????????????????????????demo???
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangedListener = onLocationChangedListener;
        if (mLocationClient != null) {
            return;
        }
        try {
            Context appContext = getContext().getApplicationContext();
            AMapLocationClient.updatePrivacyShow(appContext, true, true);
            AMapLocationClient.updatePrivacyAgree(appContext, true);
            mLocationClient = new AMapLocationClient(appContext);

            if (mMapLocationListener == null) {
                mMapLocationListener = new MyAMapLocationListener();
            }
            // ??????????????????
            mLocationClient.setLocationListener(mMapLocationListener);

            mLocationClientOption = getLocationClientDefaultOption();
            // ??????????????????
            mLocationClient.setLocationOption(mLocationClientOption);
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????????????????demo???
     */
    @Override
    public void deactivate() {
        mOnLocationChangedListener = null;

        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    /**
     * ?????????????????????
     * ????????????Demo
     */
    private AMapLocationClientOption getLocationClientDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//????????????????????????????????????????????????????????????????????????????????????????????????????????????
        mOption.setGpsFirst(false);//?????????????????????gps??????????????????????????????????????????????????????
        mOption.setHttpTimeOut(30000);//???????????????????????????????????????????????????30?????????????????????????????????
        mOption.setInterval(2000);//???????????????????????????????????????2???
        mOption.setNeedAddress(true);//????????????????????????????????????????????????????????????true
        //?????????????????????
        mOption.setOnceLocation(true);//?????????????????????????????????????????????false
        mOption.setOnceLocationLatest(false);//???????????????????????????wifi??????????????????false.???????????????true,?????????????????????????????????????????????????????????
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//????????? ????????????????????????????????????HTTP??????HTTPS????????????HTTP
        mOption.setSensorEnable(false);//????????????????????????????????????????????????false
        mOption.setWifiScan(true); //???????????????????????????wifi??????????????????true??????????????????false??????????????????????????????????????????????????????????????????????????????????????????????????????
        mOption.setLocationCacheEnable(true); //???????????????????????????????????????????????????true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//??????????????????????????????????????????????????????????????????????????????????????????????????????
        return mOption;
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param locationLatLng
     */
    private void addMarkerInScreenCenter(LatLng locationLatLng) {
        LatLng latLng = mAMap.getCameraPosition().target;
        Point screenPosition = mAMap.getProjection().toScreenLocation(latLng);
        if (mLocationMarker == null) {
//            https://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html
            mLocationMarker = mAMap.addMarker(new MarkerOptions()
                    //???????????????marker??????????????????0,0???????????????1,1
                    // ??????0.5,1????????????????????????????????????????????????
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        }
        //??????Marker????????????,?????????????????????
        mLocationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        mLocationMarker.setZIndex(1);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param latLonpoint
     */
    private void searchGeocodeAddress(LatLonPoint latLonpoint) {
        if (latLonpoint == null) {
            return;
        }
        RegeocodeQuery query = new RegeocodeQuery(latLonpoint, 2000, GeocodeSearch.AMAP);
        //???????????? base??????????????????????????????all?????????????????? ????????? base?????????geo?????????poi?????????
        // https://blog.csdn.net/qq_37184731/article/details/122366895
        query.setExtensions("all");
        mGeocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * ???????????????????????????
     */
    private void moveLocationMarker() {
        if (mLocationMarker == null) {
            return;
        }

        // ????????????????????????????????????????????????
        LatLng latLng = mAMap.getCameraPosition().target;
        Point screenPosition = mAMap.getProjection().toScreenLocation(latLng);
        mLocationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

        mTvCurrLatLng.setText(LocationUtil.composeLngLat(latLng.longitude, latLng.latitude));
    }

    /**
     * ??????Poi??????
     *
     * @param keyWord
     */
    private void doSearchQuery(String keyWord) {

        mQuery = new PoiSearch.Query(keyWord, "", "");
        mQuery.setPageSize(20);
        mQuery.setPageNum(0);

        try {
            mPoiSearch = new PoiSearch(getContext(), mQuery);
            if (mOnPoiSearchListener == null) {
                mOnPoiSearchListener = new MyOnPoiSearchListener();
            }
            mPoiSearch.setOnPoiSearchListener(mOnPoiSearchListener);
            mPoiSearch.searchPOIAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }


    /**
     * ?????????????????????
     */
    private class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (mOnLocationChangedListener == null || mLocationClient == null
                    || aMapLocation == null || aMapLocation.getErrorCode() != 0) {
                // ???????????????
                deactivate();
                LogUtil.e(aMapLocation.getErrorInfo());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                        && !LocationUtil.isEnableLocationSetting(getContext())) {
                    ToastUtil.show("????????????????????????????????????");
                } else if (aMapLocation.getErrorCode() == 7) {
                    ToastUtil.showLong("??????key????????????");
                } else {
                    ToastUtil.show("???????????????????????????????????????");
                }
                return;
            }

            // ????????????
            LatLng curLatLng = AMapUtil.newLatLng(aMapLocation);

            // ??????????????????
            mOnLocationChangedListener.onLocationChanged(aMapLocation);

            if (mCurMarker == null) {
                // ??????????????????
                mCurMarker = mAMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
            }

            // ?????????????????????
            mCurMarker.setPosition(curLatLng);

            // ?????????????????????
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16f));
        }
    }

    private class MyOnCameraChangeListener implements AMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            //???????????????????????????
            if (!isItemClickAction) {
                //???????????????????????????????????????????????????????????????????????????
                LatLonPoint latLonpoint = AMapUtil.newLatLonPoint(cameraPosition.target);
                searchGeocodeAddress(latLonpoint);
            }
            moveLocationMarker();
            isItemClickAction = false;
        }
    }

    /**
     * ??????????????????
     */
    private class MyOnMapLoadedListener implements AMap.OnMapLoadedListener {

        @Override
        public void onMapLoaded() {
            addMarkerInScreenCenter(null);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private class MyOnGeocodeSearchListener implements GeocodeSearch.OnGeocodeSearchListener {

        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int resultCode) {

            if (resultCode != AMapException.CODE_AMAP_SUCCESS) {
                LogUtil.e("???????????????");
                return;
            }

            if (result == null
                    || result.getRegeocodeAddress() == null
                    || result.getRegeocodeAddress().getPois() == null) {
                LogUtil.e("??????????????????!");
                ToastUtil.show("??????????????????!");
                return;
            }

            RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
            List<PoiItem> poiItemList = new ArrayList<>();
            LatLng cameraLatLng = mAMap.getCameraPosition().target;
            LatLonPoint latLonPoint = new LatLonPoint(cameraLatLng.latitude, cameraLatLng.longitude);
            List<PoiItem> pois = regeocodeAddress.getPois();

            poiItemList.add(new PoiItem(Const.ID_POI_CURR, latLonPoint, "?????????????????????", ""));
            poiItemList.addAll(pois);

            ViewUtil.setVisibility(View.GONE, mTvPrompt);
            mSearchPoiAdapter.setBeginAddress(regeocodeAddress.getProvince() + regeocodeAddress.getCity() + regeocodeAddress.getDistrict());
            mSearchPoiAdapter.setSelectedPosition(0);
            mSearchPoiAdapter.updateData(poiItemList);
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int code) {

        }
    }

    /**
     * Poi????????????
     */
    private class MyOnPoiSearchListener implements PoiSearch.OnPoiSearchListener {

        @Override
        public void onPoiSearched(PoiResult poiResult, int resultCode) {

            if (resultCode != AMapException.CODE_AMAP_SUCCESS) {
                LogUtil.e("???????????????");
                return;
            }

            if (poiResult == null || poiResult.getQuery() == null) {
                LogUtil.e("?????????????????????");
                ToastUtil.show("??????????????????!");
                return;
            }

            // ?????????????????????
            List<PoiItem> poiItems = poiResult.getPois();

            ViewUtil.setVisibility(CollectionUtil.isEmpty(poiItems) ? View.VISIBLE : View.GONE, mTvPrompt);

            mSearchPoiAdapter.setSelectedPosition(0);
            mSearchPoiAdapter.setBeginAddress(null);
            mSearchPoiAdapter.updateData(poiItems);

            if (!CollectionUtil.isEmpty(poiItems)) {
                // ????????????????????????
                PoiItem poiItem = mSearchPoiAdapter.getItem(0);
                LatLng latLng = AMapUtil.newLatLng(poiItem.getLatLonPoint());
                isItemClickAction = true;
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
            }

        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {
        }
    }
}
