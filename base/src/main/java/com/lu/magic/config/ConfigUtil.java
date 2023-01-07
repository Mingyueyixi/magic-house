package com.lu.magic.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.lu.magic.bean.AMapConfig;
import com.lu.magic.bean.BaseConfig;
import com.lu.magic.bean.FuckDialogConfig;
import com.lu.magic.frame.xp.annotation.PreferenceIdValue;
import com.lu.magic.frame.xp.SPreference;
import com.lu.magic.util.GsonUtil;

import java.lang.reflect.Type;
import java.util.Map;

public class ConfigUtil {
    private static SharedPreferences sp;

    public static void init(Context context) {
        if (sp == null) {
            sp = SPreference.getLocalImpl(context, "config");
        }
    }

    public static void initWithReadable(Context context) {
        if (sp == null) {
            sp = SPreference.getRemoteImpl(context, "config", PreferenceIdValue.SP, 10087);
        }

    }

    public static Map<String, ?> getAll() {
        return sp.getAll();
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
        } else if (value instanceof CharSequence) {
            JsonObject vJson = GsonUtil.fromJson(value.toString(), JsonObject.class);
            map.put(key, vJson);
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
        return getCell(ModuleId.FUCK_DIALOG, processName, FuckDialogConfig.class);
    }

    public static void setFuckDialogConfig(String processName, FuckDialogConfig fuckDialogConfig) {
        setCell(ModuleId.FUCK_DIALOG, processName, fuckDialogConfig);
    }

    public static Map<String, FuckDialogConfig> getFuckDialogConfigAll() {
        return getSheet(ModuleId.FUCK_DIALOG, FuckDialogConfig.class);
    }

    public static Map<String, AMapConfig> getAllAMapConfig() {
        return getSheet(ModuleId.AMAP_LOCATION, AMapConfig.class);
    }

    public static AMapConfig getAMapConfig(String processName) {
        return getCell(ModuleId.AMAP_LOCATION, processName, AMapConfig.class);
    }

    public static void setAMapConfig(String processName, AMapConfig config) {
        setCell(ModuleId.AMAP_LOCATION, processName, config);
    }


}
