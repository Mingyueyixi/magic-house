package com.lu.magic.frame.xp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.lu.magic.frame.xp.annotation.FunctionValue;
import com.lu.magic.frame.xp.annotation.GroupValue;
import com.lu.magic.frame.xp.annotation.ModeValue;
import com.lu.magic.frame.xp.annotation.PreferenceIdValue;
import com.lu.magic.frame.xp.bean.ContractRequest;
import com.lu.magic.frame.xp.bean.ContractResponse;
import com.lu.magic.frame.xp.provider.ContractUtil;
import com.lu.magic.frame.xp.provider.ProviderConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CPPreference implements SharedPreferences {
    private final String tableName;
    private final ContentResolver contentResolver;
    private final String preferenceId;
    private final ProviderConfig providerConfig;


    public CPPreference(Context context, String tableName, @PreferenceIdValue String preferenceId, String authority) {
        this.tableName = tableName;
        this.contentResolver = context.getContentResolver();
        this.preferenceId = preferenceId;
        this.providerConfig = new ProviderConfig(authority);
    }

    public Uri buildUri(String path) {
        String baseUri = providerConfig.getBaseUri();
        return ContractUtil.buildUri(baseUri, tableName, path);
    }

    @Override
    public Map<String, ?> getAll() {
        Uri uri = buildUri("getAll");
        try {
            return getValue(uri, FunctionValue.GET_ALL, null, new HashMap<>(), Map.class);
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
            return getValue(uri, FunctionValue.GET_STRING, key, defValue, String.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        Uri uri = buildUri("getStringSet");
        LinkedHashSet<String> finalValue;
        if (defValues instanceof LinkedHashSet) {
            finalValue = (LinkedHashSet<String>) defValues;
        } else {
            finalValue = new LinkedHashSet<>(defValues);
        }
        try {
            return getValue(uri, FunctionValue.GET_STRING_SET, key, finalValue, Set.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return new LinkedHashSet<>();
    }

    @Override
    public int getInt(String key, int defValue) {
        Uri uri = buildUri("getInt");
        try {
            return getValue(uri, FunctionValue.GET_INT, key, defValue, int.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        Uri uri = buildUri("getLong");
        try {
            return getValue(uri, FunctionValue.GET_LONG, key, defValue, long.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        Uri uri = buildUri("getFloat");
        try {
            return getValue(uri, FunctionValue.GET_FLOAT, key, defValue, float.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Uri uri = buildUri("getBoolean");
        try {
            return getValue(uri, FunctionValue.GET_BOOLEAN, key, defValue, boolean.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        Uri uri = buildUri("contains");
        ContractRequest.Action<Object> action = new ContractRequest.Action<>(FunctionValue.CONTAINS, key, null);
        ContractRequest request = new ContractRequest(preferenceId, ModeValue.READ, tableName, GroupValue.CONTAINS, Collections.singletonList(action));
        ContractResponse<Boolean> response = ContractUtil.request(contentResolver, uri, request, Boolean.class);
        return response.data;
    }

    @Override
    public SharedPreferences.Editor edit() {
        return new XEditor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    private <T> T getValue(Uri uri, String function, String key, T defValue, Class<T> rClass) {
        ContractRequest.Action<T> action = new ContractRequest.Action<>(function, key, defValue);
        ContractRequest request = new ContractRequest(preferenceId, ModeValue.READ, tableName, GroupValue.GET, Arrays.asList(action));
        ContractResponse<T> response = ContractUtil.request(contentResolver, uri, request, rClass);
        return response.data == null ? defValue : response.data;
    }

    public class XEditor implements SharedPreferences.Editor {
        private final LinkedHashMap<String, ContractRequest.Action<?>> mActionMap = new LinkedHashMap<>();
        private final AtomicInteger atomicClearMask = new AtomicInteger();

        @Override
        public SharedPreferences.Editor putString(String key, @Nullable String value) {
            mActionMap.put(key, new ContractRequest.Action<>(FunctionValue.PUT_STRING, key, value));
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
            ContractRequest.Action<Set<String>> action = new ContractRequest.Action<>(FunctionValue.PUT_STRING_SET, key, values);
            if (!(values instanceof Serializable || values instanceof Parcelable)) {
                action.value = new LinkedHashSet<>(values);
            }
            mActionMap.put(key, action);
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            mActionMap.put(key, new ContractRequest.Action<>(FunctionValue.PUT_INT, key, value));
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            mActionMap.put(key, new ContractRequest.Action<>(FunctionValue.PUT_LONG, key, value));
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            mActionMap.put(key, new ContractRequest.Action<>(FunctionValue.PUT_FLOAT, key, value));
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            mActionMap.put(key, new ContractRequest.Action<>(FunctionValue.PUT_BOOLEAN, key, value));
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            mActionMap.put(key, new ContractRequest.Action<>(FunctionValue.REMOVE, key, null));
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            //clear没有key，所以构建一个
            String key = "clear_" + hashCode() + "_" + atomicClearMask.incrementAndGet();
            mActionMap.put(key, new ContractRequest.Action<>(FunctionValue.CLEAR, null, null));
            return this;
        }

        @Override
        public boolean commit() {
            Uri uri = buildUri("write");
            ContractRequest request = new ContractRequest(preferenceId, ModeValue.WRITE, tableName, GroupValue.COMMIT, new ArrayList<>(mActionMap.values()));
            ContractResponse<Boolean> response = ContractUtil.request(contentResolver, uri, request, Boolean.class);
            return response.data != null && response.data;
        }

        @Override
        public void apply() {
            Uri uri = buildUri("write");
            ContractRequest request = new ContractRequest(preferenceId, ModeValue.WRITE, tableName, GroupValue.COMMIT, new ArrayList<>(mActionMap.values()));
            ContractUtil.request(contentResolver, uri, request, Object.class);
        }

    }

}
