package com.lu.magic.util;

import androidx.core.util.Predicate;

import com.lu.magic.util.log.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lu
 * @date 2023/9/21 15:02
 * @description org.JSON封装
 */
public class JSONX {
    private static final String TAG = "JSONX";

    public static Map<String, Object> toMap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Iterator<String> it = jsonObject.keys();
        Map<String, Object> map = new LinkedHashMap<>();
        while (it.hasNext()) {
            String k = it.next();
            map.put(k, jsonObject.opt(k));
        }
        return map;
    }

    public static Map<String, Object> toMapDeepInternal(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        if (jsonObject.length() == 0) {
            return result;
        }
        Iterator<String> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = it.next();
            Object value = jsonObject.opt(key);
            if (value instanceof JSONObject) {
                result.put(key, toMapDeepInternal((JSONObject) result.get(key)));
            } else if (value instanceof JSONArray) {
                result.put(key, toListDeepInternal((JSONArray) result.get(key)));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    public static Map<String, Object> toMapDeep(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        if (jsonObject.length() == 0) {
            return result;
        }
        JSONObject json;
        try {
            json = new JSONObject(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            json = jsonObject;
        }
        // 使用转换后的 json，避免存在非json类型
        return toMapDeepInternal(json);
    }

    private static List<Object> toListDeepInternal(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Object> list = new ArrayList<>();
        if (jsonArray.length() == 0) {
            return list;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            Object v = jsonArray.opt(i);
            if (v instanceof JSONObject) {
                list.add(toMapDeep((JSONObject) v));
            } else if (v instanceof JSONArray) {
                list.add(toListDeepInternal((JSONArray) v));
            } else {
                list.add(v);
            }
        }
        return list;
    }

    public static List<Object> toListDeep(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Object> list = new ArrayList<>();
        if (jsonArray.length() == 0) {
            return list;
        }
        JSONArray json;
        try {
            json = new JSONArray(jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            json = jsonArray;
        }
        return toListDeepInternal(json);
    }

    public static double optDouble(JSONObject jsonObject, String key) {
        return optDouble(jsonObject, key, Double.NaN);
    }

    public static double optDouble(JSONObject jsonObject, String key, double fallback) {
        if (jsonObject == null) {
            return fallback;
        }
        return jsonObject.optDouble(key, fallback);
    }

    public static long optLong(JSONObject jsonObject, String key) {
        return optLong(jsonObject, key, 0L);
    }

    /**
     * 获取long类型数据（修正原org.json 库的bug，string 的数值转long存在精度缺失）
     *
     * @param jsonObject
     * @param key
     * @param fallback
     * @return
     */
    public static long optLong(JSONObject jsonObject, String key, long fallback) {
        if (jsonObject == null) {
            return fallback;
        }
        //return jsonObject.optLong(key, fallback);
        Object object = opt(jsonObject, key);
        Long result = null;
        try {
            result = toLong(object);
        } catch (Exception e) {

        }
        return result != null ? result : fallback;
    }

    static Long toLong(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    public static int optInt(JSONObject source, String key) {
        return optInt(source, key, 0);
    }

    public static int optInt(JSONObject jsonObject, String key, int fallback) {
        if (jsonObject == null) {
            return fallback;
        }
        return jsonObject.optInt(key, fallback);
    }

    public static boolean optBoolean(JSONObject source, String key) {
        return optBoolean(source, key, false);
    }

    public static boolean optBoolean(JSONObject source, String key, boolean fallback) {
        if (source == null) {
            return fallback;
        }
        return source.optBoolean(key, fallback);
    }

    public static String optString(JSONObject jsonObject, String key) {
        return optString(jsonObject, key, null);
    }

    public static String optString(JSONObject jsonObject, String key, String fallback) {
        if (jsonObject == null) {
            return fallback;
        }
        if (jsonObject.isNull(key)) {
            return fallback;
        }
        return jsonObject.optString(key, fallback);
    }

    public static void put(JSONObject jsonObject, String key, boolean value) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            LogUtil.d(TAG, "put error " + e);
        }
    }

    public static void put(JSONObject jsonObject, String key, double value) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            LogUtil.d(TAG, "put error " + e);
        }
    }

    public static void put(JSONObject jsonObject, String key, long value) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            LogUtil.d(TAG, "put error " + e);
        }
    }

    public static void put(JSONObject jsonObject, String key, String value) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            LogUtil.d(TAG, "put error " + e);
        }
    }

    public static void put(JSONObject jsonObject, String key, int value) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            LogUtil.d(TAG, "put error " + e);
        }
    }

    public static void put(JSONObject jsonObject, String key, Object value) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            LogUtil.d(TAG, "put error " + e);
        }
    }

    public static void putOpt(JSONObject jsonObject, String key, Object value) {
        if (jsonObject == null) {
            return;
        }
        try {
            jsonObject.putOpt(key, value);
        } catch (JSONException e) {
            LogUtil.d(TAG, "putOpt error " + e);
        }
    }

    public static Object opt(JSONObject source, String text) {
        if (source == null) {
            return null;
        }
        return source.opt(text);
    }

    public static JSONObject optJSONObject(JSONObject json, String account) {
        return optJSONObject(json, account, null);
    }

    public static JSONObject optJSONObject(JSONObject json, String account, JSONObject fallback) {
        if (json == null) {
            return fallback;
        }
        JSONObject jsonObject = json.optJSONObject(account);
        if (jsonObject != null) {
            return jsonObject;
        }
        return fallback;
    }

    public static boolean isEmpty(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return true;
        }
        return false;
    }

    public static JSONObject optJSONObject(JSONArray jsonArray, int i) {
        return optJSONObject(jsonArray, i, null);
    }

    public static JSONObject optJSONObject(JSONArray jsonArray, int i, JSONObject fallback) {
        if (jsonArray == null) {
            return fallback;
        }
        JSONObject result = null;
        try {
            result = jsonArray.optJSONObject(i);
        } catch (Exception e) {
            LogUtil.d(TAG, "optJSONObject error " + e);
        }
        if (result != null) {
            return result;
        }
        return fallback;
    }

    public static ArrayList<Object> toList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        ArrayList<Object> list = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.opt(i));
        }
        return list;
    }

    public static Object filter(JSONArray jsonArray, boolean reverse, Predicate<Object> filter) {
        if (JSONX.isEmpty(jsonArray)) {
            return null;
        }
        int len = jsonArray.length();
        for (int i = 0; i < len; i++) {
            int index = i;
            if (reverse) {
                index = len - i - 1;
            }
            Object json = jsonArray.opt(index);
            if (filter.test(json)) {
                return json;
            }
        }
        return null;
    }

    public static <T> T filter(JSONObject jsonObject, boolean reverse, Class<
            T> tClass, Predicate<Object> filter) {
        Object result = filter(jsonObject.names(), reverse, filter);
        if (tClass != null && tClass.isInstance(result)) {
            return tClass.cast(result);
        }
        return null;
    }

    public static Object filter(JSONObject jsonObject, boolean reverse, Predicate<Object> filter) {
        if (JSONX.isEmpty(jsonObject)) {
            return null;
        }
        if (reverse) {
            JSONX.filter(jsonObject.names(), true, o -> {
                Object value = jsonObject.opt(o + "");
                return filter.test(value);
            });
        } else {
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                if (filter.test(jsonObject.opt(key))) {
                    return jsonObject.opt(key);
                }
            }
        }
        return null;
    }


    private static boolean isEmpty(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.length() == 0) {
            return true;
        }
        return false;
    }

    public static Object remove(JSONObject mSourceData, String name) {
        if (mSourceData != null) {
            return mSourceData.remove(name);
        }
        return null;
    }

    public static JSONArray optJSONArray(JSONObject jsonObject, String keywords) {
        if (jsonObject != null) {
            return jsonObject.optJSONArray(keywords);
        }
        return null;
    }

    public static List<String> optStringList(JSONObject jsonObject, String keywords) {
        return optStringList(jsonObject, keywords, null);
    }

    public static List<String> optStringList(JSONObject jsonObject, String keywords, List<String> fallback) {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray(keywords);
            if (jsonArray != null) {
                List<String> list = new ArrayList<>(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.optString(i));
                }
                return list;
            }
        }
        return fallback;
    }

    public static String[] optStringArray(JSONObject jsonObject, String keywords) {
        return optStringArray(jsonObject, keywords, null);
    }

    public static String[] optStringArray(JSONObject jsonObject, String keywords, String[] fallback) {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray(keywords);
            if (jsonArray != null) {
                String[] list = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    list[i] = jsonArray.optString(i);
                }
                return list;
            }
        }
        return fallback;
    }

    public static void putStringList(JSONObject jsonObject, String key, Collection<String> list) {
        if (jsonObject == null) {
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray(list);
            jsonObject.putOpt(key, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static <T extends JsonEncoder> JSONArray toJsonArray(List<T> list) {
        if (list == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (JsonEncoder item : list) {
            jsonArray.put(item.toJson());
        }
        return jsonArray;
    }

    public static <T> List<T> optList(JSONObject jsonObject, String key, JsonDecoder<T> decodeFactory) {
        JSONArray jsonArray = JSONX.optJSONArray(jsonObject, key);
        if (jsonArray != null) {
            List<T> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.optJSONObject(i);
                T t = decodeFactory.fromJson(item);
                list.add(t);
            }
            return list;
        }
        return null;
    }
}
