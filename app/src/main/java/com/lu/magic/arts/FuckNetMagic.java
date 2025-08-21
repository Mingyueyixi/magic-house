package com.lu.magic.arts;

import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.lu.magic.bean.Config;
import com.lu.magic.bean.FuckNetData;
import com.lu.magic.config.ConfigUtil;
import com.lu.magic.util.CollectionUtil;
import com.lu.magic.util.Inet4AddressUtils;
import com.lu.magic.util.log.LogUtil;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Lu
 * @date 2025/7/29
 * @description
 */
public class FuckNetMagic extends BaseMagic {
    private Config<FuckNetData> mConfig;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        mConfig = ConfigUtil.getFuckNetConfig(lpparam.packageName);
        LogUtil.d(this.getClass().getName(), " init pugin....", mConfig);
        if (mConfig == null) {
            return;
        }
        FuckNetData netData = mConfig.getData();


        if (!mConfig.isEnable() || netData == null) {
            LogUtil.d(this.getClass().getName(), " ignore Loading and init pugin....");
            return;
        }
        XposedHelpers.findAndHookMethod(WifiManager.class, "setWifiEnabled", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(netData.isWifiEnabled());
            }
        });
        XposedHelpers.findAndHookMethod(WifiManager.class, "getScanResults", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                handlerGetScanResults(param);
            }
        });

        XposedHelpers.findAndHookMethod(WifiManager.class, "getConnectionInfo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                handlerGetConnectionInfo(lpparam, param);
            }
        });

    }


    private void handlerGetScanResults(XC_MethodHook.MethodHookParam param) throws Exception {
        FuckNetData data = mConfig.getData();
        // 获取当前保存的记录信息
        List<FuckNetData.ScanResult> list = data.getScanResults();

        if (CollectionUtil.isEmpty(list)) {
            // 不需要处理
            return;
        }

        List<ScanResult> scanResults = new ArrayList<>();

        for (FuckNetData.ScanResult result : list) {

            ScanResult scanResult = ScanResult.class.newInstance();
            scanResult.SSID = result.getSsId();
            scanResult.BSSID = result.getBssId();

            scanResults.add(scanResult);
        }

        param.setResult(scanResults);

        LogUtil.d(">>>>>>>>>>>>>>> 设置ScanResults " + scanResults);
    }

    private void handlerGetConnectionInfo(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) {
        FuckNetData data = mConfig.getData();

        if (!data.isEnableVirtualWifi()) {
            // 没有启用不需要处理
            return;
        }
//
//        int state = data.getWifiState();
//        if (state == -99) {
//            // 暂未获取信息
//            return;
//        }

        String ssId = data.getWifiSsId();
        String bssId = data.getWifiBssId();
        String macAddress = data.getWifiMacAddress();
        int wifiIpAddress = data.getWifiIpAddress();
        Inet4Address ipAddress = Inet4AddressUtils.intToInet4AddressHTL(wifiIpAddress);

        WifiInfo wifiInfo = (WifiInfo) XposedHelpers.newInstance(WifiInfo.class);

        XposedHelpers.setIntField(wifiInfo, "mNetworkId", 68); // MAX_RSSI
        XposedHelpers.setObjectField(wifiInfo, "mSupplicantState", SupplicantState.COMPLETED);
        XposedHelpers.setObjectField(wifiInfo, "mBSSID", bssId);
        XposedHelpers.setObjectField(wifiInfo, "mMacAddress", macAddress);
        XposedHelpers.setIntField(wifiInfo, "mLinkSpeed", 433);  // Mbps
        XposedHelpers.setIntField(wifiInfo, "mFrequency", 5785); // MHz
        XposedHelpers.setIntField(wifiInfo, "mRssi", -49); // MAX_RSSI
        XposedHelpers.setObjectField(wifiInfo, "mIpAddress", ipAddress);

        Class<?> tClass = XposedHelpers.findClass("android.net.wifi.WifiSsid", lpparam.classLoader);
        Object wifiSsId = XposedHelpers.callStaticMethod(tClass, "createFromAsciiEncoded", ssId);
        XposedHelpers.setObjectField(wifiInfo, "mWifiSsid", wifiSsId);

        param.setResult(wifiInfo);

        LogUtil.d(">>>>>>>>>>>>>>> 设置ConnectionInfo " + wifiInfo);
    }
}
