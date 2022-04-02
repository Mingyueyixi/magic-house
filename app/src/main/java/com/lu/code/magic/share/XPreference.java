package com.lu.code.magic.share;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.lu.code.magic.util.log.LogUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class XPreference implements SharedPreferences {
    private final String tableName;
    private ContentResolver contentResolver;
    private String baseUri;

    public XPreference(String name, ContentResolver contentResolver) {
        this.tableName = name;
        this.contentResolver = contentResolver;
        baseUri = DataShareProvider.baseUri + "/sp";
    }

    private Uri buildUri(String path) {
        Uri.Builder builder = Uri.parse(baseUri).buildUpon();
        return builder.path(path).appendQueryParameter("table", tableName).build();
    }

    @Override
    public Map<String, ?> getAll() {
        Uri uri = buildUri("getAll");
        try {
            return (Map<String, ?>) getValue(uri, "getAll", tableName, null, new HashMap<>());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        Uri uri = buildUri("getString");
        try {
            return (String) getValue(uri, "getString", tableName, key, defValue);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        Uri uri = buildUri("getStringSet");
        try {
            return (Set<String>) getValue(uri, "getStringSet", tableName, key, defValues.toArray(new String[defValues.size()]));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        Uri uri = buildUri("getInt");
        try {
            return (int) getValue(uri, "getInt", tableName, key, defValue);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        Uri uri = buildUri("getLong");
        try {
            return (long) getValue(uri, "getLong", tableName, key, defValue);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        Uri uri = buildUri("getFloat");
        try {
            return (float) getValue(uri, "getFloat", tableName, key, defValue);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Uri uri = buildUri("getBoolean");
        try {
            return (boolean) getValue(uri, "getBoolean", tableName, key, defValue);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        Uri uri = buildUri("contains");
        Bundle send = new Bundle();
        send.putString("k", key);
        Bundle res = contentResolver.call(uri, "contains", tableName, send);
        return res.getBoolean(key);
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }


    private Object getValue(Uri uri, String method, String table, String key, Serializable defValue) throws Throwable {
        Bundle send = new Bundle();
        send.putString("k", key);
        send.putSerializable("v", defValue);
//        switch (method) {
//            case "getString":
//                send.putString("v", (String) defValue);
//                break;
//            case "getInt":
//                send.putInt("v", (Integer) defValue);
//                break;
//            case "getLong":
//                send.putLong("v", (Long) defValue);
//                break;
//            case "getFloat":
//                send.putFloat("v", (Float) defValue);
//                break;
//            case "getStringSet":
//                send.putStringArray("v", (String[]) defValue);
//                break;
//            case "getAll":
//                send.putSerializable("v", new LinkedHashMap<>());
//                break;
//            case "getBoolean":
//                send.putBoolean("v", (Boolean) defValue);
//                break;
//            default:
//                break;
//        }

        Bundle res = contentResolver.call(uri, method, table, send);
        if (res == null) {
            return defValue;
        }
        Object throwResult = res.get("throw");
        if (throwResult != null) {
            LogUtil.e(">>>", "ContentProvider查询出错了！！！可能是数据类型错误");
            throw (Throwable) throwResult;
        }
        return res.get(key);
    }

    public class EditorImp implements Editor {
        private HashMap<String, Object> map = new HashMap<>();

        @Override
        public Editor putString(String key, @Nullable String value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            map.put(key, values);
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Editor remove(String key) {
            map.remove(key);
            return this;
        }

        @Override
        public Editor clear() {
            map.clear();
            return this;
        }

        @Override
        public boolean commit() {
            ContentValues values = new ContentValues();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            for (Map.Entry<String, Object> ele : entrySet) {
                Object v = ele.getValue();
                if (v instanceof String) {
                    values.put(ele.getKey(), (String) v);
                }
            }

            contentResolver.call(Uri.parse(baseUri + "/commit?table=" + tableName), "", null, null);
            return false;
        }

        @Override
        public void apply() {

        }
    }
}
