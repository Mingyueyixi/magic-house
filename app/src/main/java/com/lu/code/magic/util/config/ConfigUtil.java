package com.lu.code.magic.util.config;

import android.content.Context;

import com.google.gson.JsonObject;
import com.lu.code.magic.bean.AMapConfig;
import com.lu.code.magic.bean.FuckDialogConfig;
import com.lu.code.magic.provider.XPreference;
import com.lu.code.magic.util.GsonUtil;
import com.lu.code.magic.util.log.LogUtil;

import java.lang.reflect.Type;
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

    public static <T> Map<String, T> getConfigSheet(String sheet, Class<T> tClass) {
        String json = sp.getString(sheet, "{}");
        Type type = GsonUtil.getMapType(String.class, tClass);
        Map<String, T> map = GsonUtil.fromJson(json, type);
        return map;
    }

    public static <T> T getConfigCell(String sheet, String key, Class<T> cls) {
        Map<String, JsonObject> map = getConfigSheet(sheet, JsonObject.class);
        JsonObject json = map.get(key);
        return GsonUtil.fromJson(json, cls);
    }

    public static void setConfigCell(String sheet, String key, Object value) {
        Map<String, JsonObject> map = getConfigSheet(sheet, JsonObject.class);
        if (value instanceof JsonObject) {
            map.put(key, (JsonObject) value);
        } else {
            JsonObject vJson = (JsonObject) GsonUtil.toJsonTree(value);
            map.put(key, vJson);
        }
        String json = GsonUtil.toJson(map);
        sp.edit().putString(sheet, json).apply();
    }

    public static void enableConfigCell(String sheet, String key, boolean enable) {
        JsonObject config = getConfigCell(sheet, key, JsonObject.class);
        if (config == null) {
            config = new JsonObject();
        }
        config.addProperty("enable", enable);

        setConfigCell(SheetName.FUCK_DIALOG, key, config);
    }

    public static FuckDialogConfig getFuckDialogConfig(String processName) {
        return getConfigCell(SheetName.FUCK_DIALOG, processName, FuckDialogConfig.class);
    }

    public static void setFuckDialogConfig(String processName, FuckDialogConfig fuckDialogConfig) {
        setConfigCell(SheetName.FUCK_DIALOG, processName, fuckDialogConfig);
    }

    public static Map<String, FuckDialogConfig> getFuckDialogConfigAll() {
        return getConfigSheet(SheetName.FUCK_DIALOG, FuckDialogConfig.class);
    }

    public static Map<String, AMapConfig> getAllAMapConfig() {
        return getConfigSheet(SheetName.AMAP_LOCATION, AMapConfig.class);
    }

    public static AMapConfig getAMapConfig(String processName) {
        return getConfigCell(SheetName.AMAP_LOCATION, processName, AMapConfig.class);
    }

    public static void setAMapConfig(String processName, AMapConfig config) {
        setConfigCell(SheetName.AMAP_LOCATION, processName, config);
    }
}
