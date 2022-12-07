package com.lu.magic.util;

public class EnvUtil {

    private static Class xposedBridge;

    static {
        check();
    }

    public static void check() {
        try {
            xposedBridge = Class.forName("de.robv.android.XposedBridge");
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
    }

    public static boolean isOnXposed() {
        return xposedBridge != null;
    }

}
