package com.lu.code.foolish.egg.util;

import de.robv.android.xposed.XposedBridge;

public class EnvUtil {

    private static Class<XposedBridge> xposedBridge;

    static {
        check();
    }

    public static void check() {
        try {
            xposedBridge = (Class<XposedBridge>) Class.forName("de.robv.android.XposedBridge");
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
    }

    public static boolean isOnXposed() {
        return xposedBridge != null;
    }

}
