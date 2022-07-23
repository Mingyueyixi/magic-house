package com.lu.magic.util.log;

import android.util.Log;

import com.lu.magic.util.EnvUtil;

import de.robv.android.xposed.XposedBridge;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description: todo write to file
 */
public class LogUtil {
    private final static LogDog logger = new LogDog(">>>");

    public static void d(Object... objects) {
        logger.d(objects);
    }

    public static void i(Object... objects) {
        logger.i(objects);
    }

    public static void w(Object... objects) {
        logger.w(objects);
    }

    public static void e(Object... objects) {
        logger.e(objects);
    }

    private static class LogDog {

        private String TAG;

        public LogDog(String tag) {
            this.TAG = tag;
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

        public void d(Object... obj) {
            String text = logger.process(obj);
            Log.d(logger.TAG, text);
            xposedLog(text);
        }

        public void i(Object... obj) {
            String text = logger.process(obj);
            Log.i(logger.TAG, text);
            xposedLog(text);
        }

        public void w(Object... obj) {
            String text = logger.process(obj);
            Log.w(logger.TAG, text);
            xposedLog(text);
        }

        public void e(Object... obj) {
            String text = logger.process(obj);
            Log.e(logger.TAG, text);
            xposedLog(text);
        }

        private void xposedLog(String text) {
            if (!EnvUtil.isOnXposed()) {
                return;
            }
            try {
                XposedBridge.log(logger.TAG + "  " + text);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


}
