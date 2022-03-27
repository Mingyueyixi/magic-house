package com.lu.code.magic.util;

import com.lu.code.magic.magic.BuildConfig;

import de.robv.android.xposed.XSharedPreferences;

public class XShareUtil {
    private static XSharedPreferences spConfig = null;

    public static synchronized XSharedPreferences config() {
        if (spConfig == null) {
            spConfig = new XSharedPreferences(BuildConfig.APPLICATION_ID, "config");
            spConfig.makeWorldReadable();
        } else {
            spConfig.reload();
        }
        return spConfig;
    }


}
