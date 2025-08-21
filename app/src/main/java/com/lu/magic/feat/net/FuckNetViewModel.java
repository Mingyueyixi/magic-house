
package com.lu.magic.feat.net;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lu.magic.App;
import com.lu.magic.bean.Config;
import com.lu.magic.bean.FuckNetData;
import com.lu.magic.config.ConfigUtil;
import com.lu.magic.main.AppListModel;
import com.lu.magic.store.ItemModel;
import com.lu.magic.util.CollectionUtil;
import com.lu.magic.util.SingleClassStoreUtil;
import com.lu.magic.util.ToastUtils;
import com.lu.magic.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lu
 * @date 2025/7/29
 * @description
 */
public class FuckNetViewModel extends ViewModel {
    private Config<FuckNetData> mConfig;
    public String mSourceJson;
    private MutableLiveData<FuckNetData> netInfoLive = new MutableLiveData<>();
    public ItemModel mRouteModel;
    public AppListModel mSelectAppModel;

    public FuckNetViewModel() {
        init();
    }

    public void init() {
        mRouteModel = SingleClassStoreUtil.get(ItemModel.class);
        mSelectAppModel = SingleClassStoreUtil.get(AppListModel.class);
        mConfig = ConfigUtil.getFuckNetConfig(mSelectAppModel.getPackageName());
        if (mConfig == null) {
            mConfig = new Config<>();
        }
        mSourceJson = mConfig.toJson().toString();
        netInfoLive.postValue(mConfig.getData());
    }

    /**
     * 获取当前网络信息
     *
     * @return
     */
    public void getNetInfo() {
        FuckNetData result = mConfig.getData();
        if (result == null) {
            result = new FuckNetData();
        }
        Context context = App.instance();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            LogUtil.w("ConnectivityManager is null");
            return;
        }

        // 获取WiFi管理器
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            if (!wifiManager.isWifiEnabled()) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                context.startActivity(intent);
            }
            // 填充WiFi基本信息
            result.setWifiEnabled(wifiManager.isWifiEnabled());

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID();
                if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                result.setWifiSsId(ssid);
                result.setWifiBssId(wifiInfo.getBSSID());
                result.setWifiMacAddress(wifiInfo.getMacAddress());
                result.setWifiState(wifiInfo.getNetworkId());
                result.setEnableVirtualWifi(true);
                result.setWifiIpAddress(wifiInfo.getIpAddress());

            }
        }

        mConfig.setData(result);
        netInfoLive.postValue(result);
    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE})
    private List<FuckNetData.ScanResult> getNearbyWifiList(WifiManager wifiManager) {
        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (CollectionUtil.isEmpty(scanResults)) {
            ToastUtils.show("没有扫描到WiFi");
            return null;
        }
        List<FuckNetData.ScanResult> result = new ArrayList<>();
        for (ScanResult ele : scanResults) {
            result.add(new FuckNetData.ScanResult(ele.SSID, ele.BSSID));
        }
        return result;
    }

    public MutableLiveData<FuckNetData> getNetInfoLive() {
        return netInfoLive;
    }

    public boolean isConfigChanged() {
        return TextUtils.equals(mSourceJson, mConfig.toJson().toString());
    }

    public void saveConfig() {
        ConfigUtil.setNetConfig(mSelectAppModel.getPackageName(), mConfig);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void loadNearbyWifiList() {
        WifiManager wifiManager = (WifiManager) App.instance().getSystemService(WifiManager.class);
        List<FuckNetData.ScanResult> wifiList = getNearbyWifiList(wifiManager);
        FuckNetData data = mConfig.getData();
        if (data == null) {
            data = new FuckNetData();
            mConfig.setData(data);
        }
        data.setScanResults(wifiList);
        netInfoLive.postValue(mConfig.getData());
    }
}