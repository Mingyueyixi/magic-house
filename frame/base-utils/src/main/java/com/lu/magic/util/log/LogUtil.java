package com.lu.magic.util.log;

/**
 * @Author: Lu
 * Date: 2022/02/18
 */
public class LogUtil {
    private static ILogger sLogger = new SimpleLogger();

    public static void setLogger(ILogger logger) {
        sLogger = logger;
    }

    public static void d(Object... objects) {
        sLogger.d(objects);
    }

    public static void i(Object... objects) {
        sLogger.i(objects);
    }

    public static void w(Object... objects) {
        sLogger.w(objects);
    }

    public static void e(Object... objects) {
        sLogger.e(objects);
    }

}
