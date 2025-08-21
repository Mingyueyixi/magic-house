package com.lu.magic.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.lu.magic.bean.AMapData;
import com.lu.magic.bean.Config;
import com.lu.magic.bean.FuckDialogData;
import com.lu.magic.bean.FuckNetData;
import com.lu.magic.frame.xp.SPreference;
import com.lu.magic.frame.xp.annotation.PreferenceIdValue;
import com.lu.magic.util.JSONX;
import com.lu.magic.util.JsonDecoder;
import com.lu.magic.util.JsonEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
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

    public static JSONObject getSheet(String sheet) {
        String json = sp.getString(sheet, "{}");
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static <T extends JsonEncoder> Map<String, T> getSheet(String sheet, JsonDecoder<T> factory) {
        JSONObject json = getSheet(sheet);
        Iterator<String> iter = json.keys();
        LinkedHashMap<String, T> result = new LinkedHashMap<>();
        while (iter.hasNext()) {
            String key = iter.next();
            JSONObject value = json.optJSONObject(key);
            T data = factory.fromJson(value);
            result.put(key, data);
        }
        return result;

    }

    public static <T> T getCell(String sheet, String key, JsonDecoder<T> factory) {
        JSONObject json = getCell(sheet, key);
        return factory.fromJson(json);
    }

    public static JSONObject getCell(String sheet, String key) {
        JSONObject sheetJson = getSheet(sheet);
        return JSONX.optJSONObject(sheetJson, key, new JSONObject());
    }

    public static Config<JSONObject> getBaseConfigCell(String sheet, String key) {
        return Config.fromJson(getCell(sheet, key));
    }


    public static void setCell(String sheet, String key, Object value) {
        JSONObject map = getSheet(sheet);
        try {
            if (value instanceof JsonEncoder) {
                map.put(key, ((JsonEncoder) value).toJson());
            } else {
                map.putOpt(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sp.edit().putString(sheet, map.toString()).apply();
    }

    public static void enableCell(String sheet, String key, boolean enable) {
        JSONObject config = getCell(sheet, key);
        if (config == null) {
            Config<?> baseConfig = new Config<>();
            baseConfig.setEnable(enable);
            config = baseConfig.toJson();
        } else {
            //必须与BaseConfig字段名保持一致
            JSONX.putOpt(config, "enable", enable);
        }
        setCell(sheet, key, config);
    }

    public static Config<FuckDialogData> getFuckDialogConfig(String processName) {
        return getCell(ModuleId.FUCK_DIALOG,processName, jsonObject -> Config.fromJson(jsonObject, FuckDialogData::fromJson));
    }

    public static void setFuckDialogConfig(String processName, Config<FuckDialogData> fuckDialogConfig) {
        setCell(ModuleId.FUCK_DIALOG, processName, fuckDialogConfig);
    }

    public static Map<String, Config<FuckDialogData>> getFuckDialogConfigAll() {
        return getSheet(ModuleId.FUCK_DIALOG, jsonObject -> Config.fromJson(jsonObject, FuckDialogData::fromJson));
    }

    public static Map<String, Config<AMapData>> getAllAMapConfig() {
        return getSheet(ModuleId.AMAP_LOCATION, jsonObject -> Config.fromJson(jsonObject, AMapData::fromJson));
    }

    public static Config<AMapData> getAMapConfig(String processName) {
        return getCell(ModuleId.AMAP_LOCATION, processName, jsonObject -> Config.fromJson(jsonObject, AMapData::fromJson));
    }

    public static void setAMapConfig(String processName, Config<AMapData> config) {
        setCell(ModuleId.AMAP_LOCATION, processName, config);
    }

    public static Config<FuckNetData> getFuckNetConfig(String processName) {
        return getCell(ModuleId.FUCK_NET, processName, jsonObject -> Config.fromJson(jsonObject, FuckNetData::fromJson));
    }

    public static void setNetConfig(String processName, Config<FuckNetData> config) {
        setCell(ModuleId.FUCK_NET, processName, config);
    }

}
