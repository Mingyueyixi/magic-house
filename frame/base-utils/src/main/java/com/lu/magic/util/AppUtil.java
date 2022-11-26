package com.lu.magic.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * @Author: Lu
 * Date: 2022/02/21
 * Description:
 */
public class AppUtil {
    private static Context sContext;

    private AppUtil() {

    }

    public static void attachContext(Context ctx) {
        sContext = ctx.getApplicationContext();
    }

    public static Context getContext() {
        if (sContext == null) {
            sContext = getApplicationByReflect();
        }
        return sContext;
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

    public static String getSHA1(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1) {
                    hexString.append("0");
                }
                if (i >= publicKey.length - 1) {
                    hexString.append(appendString);
                } else {
                    hexString.append(appendString + ":");
                }
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
