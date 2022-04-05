package com.lu.code.magic.util;

import android.content.Context;

import com.lu.code.magic.bean.FuckDialogConfig;
import com.lu.code.magic.provider.XPreference;

import java.util.Map;

public class ConfigUtil {
    private static XPreference sp;

    public static void init(Context context) {
        if (sp == null) {
            sp = new XPreference(context, "config");
        }
    }

    public static Map<String, Object> getAll() {
        return (Map<String, Object>) sp.getAll();
    }

    public static FuckDialogConfig getFuckDialogConfig(String processName) {
        String json = sp.getString(processName, "{}");
        FuckDialogConfig entity = GsonUtil.fromJson(json, FuckDialogConfig.class);
        return entity;
    }

    public static void setFuckDialogConfig(String packageName, FuckDialogConfig fuckDialogConfig) {


    }
}
