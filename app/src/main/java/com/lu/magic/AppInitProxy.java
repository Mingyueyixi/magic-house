package com.lu.magic;

import android.content.Context;

import com.lu.magic.util.AppUtil;
import com.lu.magic.util.config.ConfigUtil;

public class AppInitProxy {

    private static Context context;

    public static boolean hasAttachContext() {
        return context == null;
    }

    public static void callInit(Context ctx) {
        context = ctx.getApplicationContext();
        ConfigUtil.init(ctx);
        AppUtil.attachContext(ctx);
    }
}
