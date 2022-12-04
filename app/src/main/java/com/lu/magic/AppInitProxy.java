package com.lu.magic;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lu.magic.config.ConfigUtil;
import com.lu.magic.util.AppUtil;
import com.lu.magic.util.EnvUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.log.SimpleLogger;

import de.robv.android.xposed.XposedBridge;

/**
 * 这个类将此app与被hook的app的配置的初始化任务，统一到一起
 */
public class AppInitProxy {

    private static Context context;

    public static boolean hasAttachContext() {
        return context != null;
    }

    public static void callInit(Context ctx) {
        context = ctx.getApplicationContext();
        ConfigUtil.init(ctx);
        AppUtil.attachContext(ctx);
        LogUtil.setLogger(new SimpleLogger() {
            @Override
            public void afterLog(int level, @NonNull Object[] objects, @NonNull String msg) {
                if (EnvUtil.isOnXposed()) {
                    try {
                        XposedBridge.log(SimpleLogger.TAG + "  " + msg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


}
