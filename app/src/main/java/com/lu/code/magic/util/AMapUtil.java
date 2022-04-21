package com.lu.code.magic.util;


import android.location.Location;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;


public class AMapUtil {

    private AMapUtil() {

    }

    public static LatLonPoint newLatLonPoint(LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }

    public static LatLng newLatLng(Location location) {
        if (location == null) {
            return null;
        }
        return newLatLng(location.getLatitude(), location.getLongitude());
    }


    public static LatLng newLatLng(LatLonPoint location) {

        if (location == null) return null;

        return newLatLng(location.getLatitude(), location.getLongitude());
    }

    public static LatLng newLatLng(double latitude, double longitude) {
        return new LatLng(latitude, longitude);
    }

}
