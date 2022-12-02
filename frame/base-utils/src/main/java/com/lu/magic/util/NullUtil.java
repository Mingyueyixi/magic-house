package com.lu.magic.util;

public class NullUtil {

    public static boolean isAllNull(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNotNull(Object... objects) {
        return !isAllNull(objects);
    }


}
