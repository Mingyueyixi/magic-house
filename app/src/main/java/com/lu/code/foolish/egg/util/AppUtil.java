package com.lu.code.foolish.egg.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author: Lu
 * Date: 2022/02/21
 * Description:
 */
public class AppUtil {
    private static AppUtil sInstance;
    private Context mContext;

    private AppUtil() {

    }

    public static AppUtil getInstance() {
        if (sInstance == null) {
            sInstance = new AppUtil();
        }
        return sInstance;
    }

    public static boolean init() {
        Application app = null;
        try {
            app = getApplicationByReflect();
        } catch (NullPointerException e) {

        }
        if (app == null) {
            return false;
        }
        init(app);
        return true;
    }

    public static void init(Context context) {
        AppUtil instance = getInstance();
        instance.attachContext(context);
    }


    public Context getAppContext() {
        return mContext;
    }

    public void attachContext(Context context) {
        mContext = context;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

}
