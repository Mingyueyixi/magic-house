package com.lu.code.magic.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.lu.code.magic.util.config.ConfigUtil;

/**
 * @Author: Lu
 * Date: 2022/02/21
 * Description:
 */
public class AppUtil {
    private static Context sContext;

    private AppUtil() {

    }

    public static boolean hasInit() {
        return sContext != null;
    }

    public static void doInit(Context context) {
        sContext = context.getApplicationContext();
        ConfigUtil.init(context);
    }

    public static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("can't find application from ActivityThread!!!");
            }
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
