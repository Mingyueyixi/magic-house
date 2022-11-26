package com.lu.magic;

import android.content.Context;

import com.lu.magic.util.AppUtil;
import com.lu.magic.util.config.ConfigUtil;

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
    }


}
