package com.lu.magic.bean;

import com.lu.magic.util.JSONX;
import com.lu.magic.util.JsonEncoder;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lu
 * @date 2025/7/29
 * @description
 */
public class FuckNetData extends JsonBean {
    private boolean wifiEnabled;
    private boolean enableVirtualWifi;
    private int wifiState;
    private String wifiSsId;
    private String wifiBssId;
    private String wifiMacAddress;
    private List<ScanResult> scanResults;
    private int wifiIpAddress;

    public FuckNetData() {
    }


    public boolean isWifiEnabled() {
        return wifiEnabled;
    }

    public void setWifiEnabled(boolean wifiEnabled) {
        this.wifiEnabled = wifiEnabled;
    }


    public boolean isEnableVirtualWifi() {
        return enableVirtualWifi;
    }

    public void setEnableVirtualWifi(boolean enableVirtualWifi) {
        this.enableVirtualWifi = enableVirtualWifi;
    }

    public int getWifiState() {
        return wifiState;
    }

    public void setWifiState(int wifiState) {
        this.wifiState = wifiState;
    }

    public String getWifiSsId() {
        return wifiSsId;
    }

    public void setWifiSsId(String wifiSsId) {
        this.wifiSsId = wifiSsId;
    }

    public String getWifiBssId() {
        return wifiBssId;
    }

    public void setWifiBssId(String wifiBssId) {
        this.wifiBssId = wifiBssId;
    }

    public String getWifiMacAddress() {
        return wifiMacAddress;
    }

    public void setWifiMacAddress(String wifiMacAddress) {
        this.wifiMacAddress = wifiMacAddress;
    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    @Override
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        JSONX.putOpt(result, "wifiEnabled", wifiEnabled);
        JSONX.putOpt(result, "enableVirtualWifi", enableVirtualWifi);
        JSONX.putOpt(result, "wifiState", wifiState);
        JSONX.putOpt(result, "wifiSsId", wifiSsId);
        JSONX.putOpt(result, "wifiBssId", wifiBssId);
        JSONX.putOpt(result, "wifiMacAddress", wifiMacAddress);
        JSONX.putOpt(result, "scanResults", JSONX.toJsonArray(scanResults));
        JSONX.putOpt(result, "wifiIpAddress", wifiIpAddress);
        return result;
    }

    public static FuckNetData fromJson(JSONObject jsonObject) {
        FuckNetData result = new FuckNetData();
        result.wifiEnabled = JSONX.optBoolean(jsonObject, "wifiEnabled");
        result.enableVirtualWifi = JSONX.optBoolean(jsonObject, "enableVirtualWifi");
        result.wifiState = JSONX.optInt(jsonObject, "wifiState");
        result.wifiSsId = JSONX.optString(jsonObject, "wifiSsId");
        result.wifiBssId = JSONX.optString(jsonObject, "wifiBssId");
        result.wifiMacAddress = JSONX.optString(jsonObject, "wifiMacAddress");
        result.scanResults = JSONX.optList(jsonObject, "scanResults", ScanResult::fromJson);
        result.wifiIpAddress = JSONX.optInt(jsonObject, "wifiIpAddress");
        return result;
    }

    public void setWifiIpAddress(int ipAddress) {
        this.wifiIpAddress = ipAddress;
    }

    public int getWifiIpAddress() {
        return wifiIpAddress;
    }

    public static final class ScanResult extends JsonBean {

        private String mSsId;
        private String mBssId;

        public ScanResult() {
        }

        public ScanResult(String ssId, String bssId) {
            mSsId = ssId;
            mBssId = bssId;
        }

        public String getSsId() {
            return mSsId;
        }

        public void setSsId(String ssId) {
            mSsId = ssId;
        }

        public String getBssId() {
            return mBssId;
        }

        public void setBssId(String bssId) {
            mBssId = bssId;
        }

        @Override
        public JSONObject toJson() {
            JSONObject result = new JSONObject();
            JSONX.putOpt(result, "ssId", mSsId);
            JSONX.putOpt(result, "bssId", mBssId);
            return result;
        }

        public static ScanResult fromJson(JSONObject jsonObject) {
            ScanResult result = new ScanResult();
            result.mSsId = JSONX.optString(jsonObject, "ssId");
            result.mBssId = JSONX.optString(jsonObject, "bssId");
            return result;
        }
    }

}
