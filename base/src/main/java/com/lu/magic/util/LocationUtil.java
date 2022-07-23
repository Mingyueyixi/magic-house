package com.lu.magic.util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.lu.magic.util.log.LogUtil;

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

    public static Location parseLngLat(String latLng) {
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

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLongitude(lng);
            location.setLatitude(lat);
            return location;
        }
        return null;
    }

    public static String composeLngLat(double lng, double lat) {
        return lng + "," + lat;
    }

}
