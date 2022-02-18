package com.lu.code.foolish.egg.util.log;

import android.util.Log;

import de.robv.android.xposed.XposedBridge;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: todo write to file
 */
public class LogUtil {
    private final static LogDog logger = new LogDog(">>>");

    public static void d(Object... objects) {
        String text = logger.process(objects);
        XposedBridge.log(logger.flower + "  " + text);
//        Log.d(logger.flower, text);
    }

    public static void i(Object... objects) {
        String text = logger.process(objects);
        XposedBridge.log(logger.flower + "  " + text);
//        Log.i(logger.flower, text);
    }

    public static void w(Object... objects) {
        String text = logger.process(objects);
        XposedBridge.log(logger.flower + "  " + text);
//        Log.i(logger.flower, text);
    }

    public static void e(Object... objects) {
        String text = logger.process(objects);
        XposedBridge.log(logger.flower + "  " + text);
//        Log.e(logger.flower, text);
    }

    private static class LogDog {

        private final String flower;

        public LogDog(String flow) {
            this.flower = flow;
        }

        public String process(Object... objects) {
            if (objects == null) {
                return "null";
            }
            if (objects.length == 1) {
                return objects[0].toString();
            }
            StringBuffer sb = new StringBuffer(objects[0] + "");
            for (int i = 1; i < objects.length; i++) {
                sb.append("  " + objects[i]);
            }
            return sb.toString();
        }

    }

}
