package com.lu.code.magic.share;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XPreference implements SharedPreferences {
    private final String name;
    private ContentResolver contentResolver;
    private String baseUri = StoreContentProvider.baseUri + "/sp";

    public XPreference(String name, ContentResolver contentResolver) {
        this.name = name;
        this.contentResolver = contentResolver;
    }

    @Override
    public Map<String, ?> getAll() {
        Uri uri = Uri.parse(baseUri + "/getAll?table=" + name);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToNext();
        Bundle bundle = cursor.getExtras();
        Object value = bundle.get("VALUE_KEY");
        cursor.close();
        return (Map<String, ?>) value;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        Uri uri = Uri.parse(baseUri + "/getString?table=" + name);
        Object value = getValue(uri, key, defValue);
        return (String) value;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        Uri uri = Uri.parse(baseUri + "/getStringSet?table=" + name);
        String[] defValueArray = defValues.toArray(new String[defValues.size()]);
        Cursor cursor = contentResolver.query(uri, defValueArray, null, new String[]{key, null}, null);
        cursor.moveToNext();
        Bundle bundle = cursor.getExtras();
        Object value = bundle.get("VALUE_KEY");
        cursor.close();
        return (Set<String>) value;
    }

    @Override
    public int getInt(String key, int defValue) {
        Uri uri = Uri.parse(baseUri + "/getInt?table=" + name);
        Object v = getValue(uri, key, defValue + "");
        return (int) v;
    }

    @Override
    public long getLong(String key, long defValue) {
        Uri uri = Uri.parse(baseUri + "/getLong?table=" + name);
        Object v = getValue(uri, key, defValue + "");
        return (long) v;
    }

    @Override
    public float getFloat(String key, float defValue) {
        Uri uri = Uri.parse(baseUri + "/getFloat?table=" + name);
        Object v = getValue(uri, key, defValue + "");
        return (float) v;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Uri uri = Uri.parse(baseUri + "/getBoolean?table=" + name);
        Object v = getValue(uri, key, defValue + "");
        return (boolean) v;
    }

    @Override
    public boolean contains(String key) {
        return false;
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


    private Object getValue(Uri uri, String key, String defValue) {
        Cursor cursor = contentResolver.query(uri, null, null, new String[]{key, defValue}, null);
        cursor.moveToNext();
        Bundle bundle = cursor.getExtras();
        Object value = bundle.get("VALUE_KEY");
        cursor.close();
        return value;
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

            contentResolver.update(baseUri + "/commit?table=" + name, values, null, null);
            return false;
        }

        @Override
        public void apply() {

        }
    }
}
