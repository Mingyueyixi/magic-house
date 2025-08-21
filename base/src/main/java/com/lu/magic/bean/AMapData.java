package com.lu.magic.bean;

import com.lu.magic.util.JSONX;
import com.lu.magic.util.JsonEncoder;

import org.json.JSONObject;

/**
 * @author Lu
 * @date 2025/8/11
 * @description
 */
public class AMapData extends JsonBean {
    private double lat;
    private double lng;
    private String name;

    public AMapData() {
        this(0, 0);
    }

    public AMapData(double lan, double lng) {
        this.lat = lan;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public static AMapData fromJson(JSONObject jsonObject) {
        AMapData result = new AMapData();
        result.lat = JSONX.optDouble(jsonObject,"lat");
        result.lng = JSONX.optDouble(jsonObject,"lng");
        result.name = JSONX.optString(jsonObject, "name");
        return result;
    }

    @Override
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        JSONX.putOpt(result, "lat", lat);
        JSONX.putOpt(result, "lng", lng);
        JSONX.putOpt(result, "name", name);
        return result;
    }
}
