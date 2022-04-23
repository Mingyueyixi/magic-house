package com.lu.code.magic.util.config;

import android.content.Context;

import com.google.gson.JsonObject;
import com.lu.code.magic.bean.AMapConfig;
import com.lu.code.magic.bean.BaseConfig;
import com.lu.code.magic.bean.FuckDialogConfig;
import com.lu.code.magic.provider.XPreference;
import com.lu.code.magic.util.GsonUtil;

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

    public static <T> Map<String, T> getSheet(String sheet, Class<T> tClass) {
        String json = sp.getString(sheet, "{}");
        Type type = GsonUtil.getMapType(String.class, tClass);
        Map<String, T> map = GsonUtil.fromJson(json, type);
        return map;
    }

    public static <T> T getCell(String sheet, String key, Class<T> cls) {
        return getCellForType(sheet, key, cls);
    }

    public static <T> T getCellForType(String sheet, String key, Type type) {
        Map<String, JsonObject> map = getSheet(sheet, JsonObject.class);
        JsonObject json = map.get(key);
        return GsonUtil.fromJson(json, type);
    }

    public static void setCell(String sheet, String key, Object value) {
        Map<String, JsonObject> map = getSheet(sheet, JsonObject.class);
        if (value instanceof JsonObject) {
            map.put(key, (JsonObject) value);
        } else {
            JsonObject vJson = (JsonObject) GsonUtil.toJsonTree(value);
            map.put(key, vJson);
        }
        String json = GsonUtil.toJson(map);
        sp.edit().putString(sheet, json).apply();
    }

    public static void enableCell(String sheet, String key, boolean enable) {
        JsonObject config = getCell(sheet, key, JsonObject.class);
        if (config == null) {
            BaseConfig baseConfig = new BaseConfig();
            baseConfig.setEnable(true);
            config = (JsonObject) GsonUtil.toJsonTree(baseConfig);
        } else {
            //必须与BaseConfig字段名保持一致
            config.addProperty("enable", enable);
        }
        setCell(sheet, key, config);
    }

    public static FuckDialogConfig getFuckDialogConfig(String processName) {
        return getCell(SheetName.FUCK_DIALOG, processName, FuckDialogConfig.class);
    }

    public static void setFuckDialogConfig(String processName, FuckDialogConfig fuckDialogConfig) {
        setCell(SheetName.FUCK_DIALOG, processName, fuckDialogConfig);
    }

    public static Map<String, FuckDialogConfig> getFuckDialogConfigAll() {
        return getSheet(SheetName.FUCK_DIALOG, FuckDialogConfig.class);
    }

    public static Map<String, AMapConfig> getAllAMapConfig() {
        return getSheet(SheetName.AMAP_LOCATION, AMapConfig.class);
    }

    public static AMapConfig getAMapConfig(String processName) {
        return getCell(SheetName.AMAP_LOCATION, processName, AMapConfig.class);
    }

    public static void setAMapConfig(String processName, AMapConfig config) {
        setCell(SheetName.AMAP_LOCATION, processName, config);
    }
}
