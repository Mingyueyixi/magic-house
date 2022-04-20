package com.lu.code.magic.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

public class LocationUtil {

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isEnableLocationSetting(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return lm.isLocationEnabled();
    }

    public void startOpenLocationSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }
}
