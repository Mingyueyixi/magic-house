package com.lu.code.magic.amap;

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
import com.lu.code.magic.bean.AMapConfig;
import com.lu.code.magic.main.AppListModel;
import com.lu.code.magic.ui.BaseFragment;
import com.lu.code.magic.ui.BaseToolBarActivity;
import com.lu.code.magic.ui.recycler.MultiAdapter;
import com.lu.code.magic.ui.recycler.MultiViewHolder;
import com.lu.code.magic.ui.recycler.SimpleItemType;
import com.lu.code.magic.util.AMapUtil;
import com.lu.code.magic.util.CollectionUtil;
import com.lu.code.magic.util.LocationUtil;
import com.lu.code.magic.util.SingleStoreUtil;
import com.lu.code.magic.util.ToastUtil;
import com.lu.code.magic.util.config.ConfigUtil;
import com.lu.code.magic.util.dialog.DialogUtil;
import com.lu.code.magic.util.dialog.EditDialog;
import com.lu.code.magic.util.log.LogUtil;
import com.lu.code.magic.util.permission.PermissionUtil;
import com.lu.code.magic.util.view.ViewUtil;
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
     * 关键字搜索poi监听
     */
    private PoiSearch.OnPoiSearchListener mOnPoiSearchListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appListModel = SingleStoreUtil.get(AppListModel.class);
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
        // 请求权限
        PermissionUtil.Companion.permission(
                10086,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        ).onResult((requestCode, result) -> {
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
                ToastUtil.show("权限获取失败,功能将会受影响！");
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
            //没有变更
            ToastUtil.show("位置没有变更");
            requireActivity().finish();
        } else {
            //合并数据，保持保持开关状态
            finalAMapConfig.setLng(lanLng.longitude);
            finalAMapConfig.setLat(lanLng.latitude);

            EditDialog.Builder builder = DialogUtil.buildEditDialog(getContext());
            builder.setTitle("已选择新位置，是否保存？")
                    .setContent((lanLng.longitude + "," + lanLng.latitude))
                    .setContentHint("输入经纬度，以英文逗号,隔开")
                    .setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                        requireActivity().finish();
                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        String text = builder.getEditText().getText() + "";
                        Location location = LocationUtil.parseLngLat(text);
                        if (location == null) {
                            ToastUtil.show("格式错误");
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
     * 激活定位（参考高德demo）
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
            // 设置定位监听
            mLocationClient.setLocationListener(mMapLocationListener);

            mLocationClientOption = getLocationClientDefaultOption();
            // 设置定位参数
            mLocationClient.setLocationOption(mLocationClientOption);
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止定位（参考高德demo）
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
     * 默认的定位参数
     * 参考高德Demo
     */
    private AMapLocationClientOption getLocationClientDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        //这里就定位一下
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 添加水印到地图上，定位杆子、当前位置图片
     *
     * @param locationLatLng
     */
    private void addMarkerInScreenCenter(LatLng locationLatLng) {
        LatLng latLng = mAMap.getCameraPosition().target;
        Point screenPosition = mAMap.getProjection().toScreenLocation(latLng);
        if (mLocationMarker == null) {
//            https://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html
            mLocationMarker = mAMap.addMarker(new MarkerOptions()
                    //锚点设置，marker图片左定位为0,0，右底点为1,1
                    // 默认0.5,1，即底部中央位置。这个图片是居中
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        }
        //设置Marker在屏幕上,不跟随地图移动
        mLocationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        mLocationMarker.setZIndex(1);
    }

    /**
     * 根据经纬度查询附近地址列表
     *
     * @param latLonpoint
     */
    private void searchGeocodeAddress(LatLonPoint latLonpoint) {
        if (latLonpoint == null) {
            return;
        }
        RegeocodeQuery query = new RegeocodeQuery(latLonpoint, 2000, GeocodeSearch.AMAP);
        //扩展字段 base表示只返回基础数据，all表示所有数据 ，默认 base，新版geo返回的poi没数据
        // https://blog.csdn.net/qq_37184731/article/details/122366895
        query.setExtensions("all");
        mGeocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * 移动中心位置的标记
     */
    private void moveLocationMarker() {
        if (mLocationMarker == null) {
            return;
        }

        // 根据屏幕距离计算需要移动的目标点
        LatLng latLng = mAMap.getCameraPosition().target;
        Point screenPosition = mAMap.getProjection().toScreenLocation(latLng);
        mLocationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

        mTvCurrLatLng.setText(LocationUtil.composeLngLat(latLng.longitude, latLng.latitude));
    }

    /**
     * 搜索Poi信息
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
     * 定位回调的监听
     */
    private class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (mOnLocationChangedListener == null || mLocationClient == null
                    || aMapLocation == null || aMapLocation.getErrorCode() != 0) {
                // 定位失败了
                deactivate();
                LogUtil.e(aMapLocation.getErrorInfo());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                        && !LocationUtil.isEnableLocationSetting(getContext())) {
                    ToastUtil.show("定位失败，请打开定位开关");
                } else if (aMapLocation.getErrorCode() == 7) {
                    ToastUtil.showLong("高德key配置错误");
                } else {
                    ToastUtil.show("定位失败，请检查权限与设置");
                }
                return;
            }

            // 当前位置
            LatLng curLatLng = AMapUtil.newLatLng(aMapLocation);

            // 通知位置信息
            mOnLocationChangedListener.onLocationChanged(aMapLocation);

            if (mCurMarker == null) {
                // 设置当前位置
                mCurMarker = mAMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
            }

            // 移动到指定位置
            mCurMarker.setPosition(curLatLng);

            // 移动到当前位置
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16f));
        }
    }

    private class MyOnCameraChangeListener implements AMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            //移动地图的镜头完成
            if (!isItemClickAction) {
                //当不是点击列表触发时，才需要进行搜索，避免频繁请求
                LatLonPoint latLonpoint = AMapUtil.newLatLonPoint(cameraPosition.target);
                searchGeocodeAddress(latLonpoint);
            }
            moveLocationMarker();
            isItemClickAction = false;
        }
    }

    /**
     * 地图加载监听
     */
    private class MyOnMapLoadedListener implements AMap.OnMapLoadedListener {

        @Override
        public void onMapLoaded() {
            addMarkerInScreenCenter(null);
        }
    }

    /**
     * 地理搜索监听，经纬搜索
     */
    private class MyOnGeocodeSearchListener implements GeocodeSearch.OnGeocodeSearchListener {

        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int resultCode) {

            if (resultCode != AMapException.CODE_AMAP_SUCCESS) {
                LogUtil.e("搜索出错了");
                return;
            }

            if (result == null
                    || result.getRegeocodeAddress() == null
                    || result.getRegeocodeAddress().getPois() == null) {
                LogUtil.e("没有搜索结果!");
                ToastUtil.show("没有搜索结果!");
                return;
            }

            RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
            List<PoiItem> poiItemList = new ArrayList<>();
            LatLng cameraLatLng = mAMap.getCameraPosition().target;
            LatLonPoint latLonPoint = new LatLonPoint(cameraLatLng.latitude, cameraLatLng.longitude);
            List<PoiItem> pois = regeocodeAddress.getPois();

            poiItemList.add(new PoiItem(Const.ID_POI_CURR, latLonPoint, "当前标记的位置", ""));
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
     * Poi搜索监听
     */
    private class MyOnPoiSearchListener implements PoiSearch.OnPoiSearchListener {

        @Override
        public void onPoiSearched(PoiResult poiResult, int resultCode) {

            if (resultCode != AMapException.CODE_AMAP_SUCCESS) {
                LogUtil.e("搜索出错了");
                return;
            }

            if (poiResult == null || poiResult.getQuery() == null) {
                LogUtil.e("没有搜索到结果");
                ToastUtil.show("没有搜索结果!");
                return;
            }

            // 获取搜索的结果
            List<PoiItem> poiItems = poiResult.getPois();

            ViewUtil.setVisibility(CollectionUtil.isEmpty(poiItems) ? View.VISIBLE : View.GONE, mTvPrompt);

            mSearchPoiAdapter.setSelectedPosition(0);
            mSearchPoiAdapter.setBeginAddress(null);
            mSearchPoiAdapter.updateData(poiItems);

            if (!CollectionUtil.isEmpty(poiItems)) {
                // 移动到第一个位置
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
