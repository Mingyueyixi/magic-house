package com.lu.code.magic.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.amap.api.maps2d.model.LatLng;
import com.lu.code.magic.util.log.LogUtil;

public class LocationUtil {

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isEnableLocationSetting(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return lm.isLocationEnabled();
    }

    public static void startOpenLocationSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    public static double[] parseLngLat(String latLng) {
        if (latLng.contains(",")) {
            String[] latLngArray = latLng.split(",");
            if (latLngArray.length != 2) {
                return null;
            }
            double lat = 0;
            double lng = 0;
            try {
                lng = Double.parseDouble(latLngArray[0]);
                lat = Double.parseDouble(latLngArray[1]);
            } catch (NumberFormatException e) {
                LogUtil.e(e);
                return null;
            }
            return new double[]{lat, lng};
        }
        return null;
    }

    public static String composeLngLat(double lng, double lat) {
        return lng + "," + lat;
    }

}
