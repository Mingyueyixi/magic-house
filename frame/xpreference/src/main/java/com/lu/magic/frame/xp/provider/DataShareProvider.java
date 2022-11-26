package com.lu.magic.frame.xp.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.magic.frame.xp.provider.annotation.FunctionValue;
import com.lu.magic.frame.xp.provider.annotation.GroupValue;
import com.lu.magic.frame.xp.provider.annotation.ModeValue;
import com.lu.magic.frame.xp.provider.annotation.ProviderIdValue;
import com.tencent.mmkv.MMKV;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <prefix>://<authority>/<data_type>/<id>
 * content://com.lu.magic/mmkv/
 * content://com.lu.magic/sp/
 *
 * @author Lu
 */
public class DataShareProvider extends BaseCallProvider {
    private static ProviderConfig providerConfig = new ProviderConfig("com.lu.magic");

    public static void initConfig(ProviderConfig config) {
        if (providerConfig == null) {
            return;
        }
        providerConfig = config;
    }

    public static ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        ContractResponse<?> response = null;
        try {
            response = dispatchCallMethod(method, arg, extras);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ContractResponse<>(null, e);
        }
        if (response == null) {
            response = new ContractResponse<>();
        }
        return ContractUtil.toResponseBundle(response);
    }


    private ContractResponse<?> dispatchCallMethod(String mode, @ProviderIdValue String table, Bundle extras) {
        ContractRequest request = ContractUtil.toContractRequest(extras);
        ContractResponse<Object> response = null;

        if (request.actions == null || request.actions.size() == 0) {
            return null;
        }
        switch (request.mode) {
            case ModeValue.READ:
                response = readValue(request);
                break;
            case ModeValue.WRITE:
                response = writeValue(request);
                break;
            default:
                break;
        }
        return response;
    }


    public ContractResponse<Object> readValue(ContractRequest request) {
        //偏好设置读取时，只有一次操作
        ContractRequest.Action<?> bundleAction = request.actions.get(0);
        String group = request.group;
        String function = bundleAction.function;
        String key = bundleAction.key;
        Object resultV = bundleAction.value;
        switch (group) {
            case GroupValue.GET:
                resultV = getValue(request);
                break;
            case GroupValue.CONTAINS:
                resultV = containsKey(function, request.table, key);
                break;
        }
        return new ContractResponse<>(resultV, null);
    }

    private boolean containsKey(@ProviderIdValue String providerId, String table, String key) {
        if (ProviderIdValue.MMKV.equals(providerId)) {
            return mmkvTable(table).contains(key);
        }
        return prefsTable(table).contains(key);
    }


    private Serializable getValue(ContractRequest request) {
        String table = request.table;
        ContractRequest.Action<?> action = request.actions.get(0);
        String function = action.function;
        String key = action.key;
        Object defValue = action.value;

        SharedPreferences sp;
        if (ProviderIdValue.MMKV.equals(request.providerId)) {
            sp = mmkvTable(table);
        } else {
            sp = prefsTable(table);
        }

        Serializable resultV = null;
        switch (function) {
            case FunctionValue.GET_STRING:
                resultV = sp.getString(key, (String) defValue);
                break;
            case FunctionValue.GET_INT:
                resultV = sp.getInt(key, (Integer) defValue);
                break;
            case FunctionValue.GET_LONG:
                resultV = sp.getLong(key, (Long) defValue);
                break;
            case FunctionValue.GET_FLOAT:
                resultV = sp.getFloat(key, (Float) defValue);
                break;
            case FunctionValue.GET_STRING_SET:
                Set<String> setResult = sp.getStringSet(key, (Set<String>) defValue);
                if (!(setResult instanceof Serializable)) {
                    setResult = new LinkedHashSet<>(setResult);
                }
                resultV = (Serializable) setResult;
                break;
            case FunctionValue.GET_ALL:
                Map<String, ?> mapResult = sp.getAll();
                if (!(mapResult instanceof Serializable)) {
                    mapResult = new LinkedHashMap<>(mapResult);
                }
                resultV = (Serializable) mapResult;
                break;
            case FunctionValue.GET_BOOLEAN:
                resultV = sp.getBoolean(key, (Boolean) defValue);
                break;
            default:
                break;
        }
        return resultV;

    }


    private ContractResponse writeValue(ContractRequest request) {
        switch (request.group) {
            case GroupValue.COMMIT:
                return commitValue(request);
            case GroupValue.APPLY:
                return applyValue(request);
            default:
                break;
        }
        return new ContractResponse<Void>(null, null);
    }

    private ContractResponse<Boolean> commitValue(ContractRequest request) {
        SharedPreferences.Editor editor = openEditor(request.providerId, request.table);
        putValue(editor, request.actions);
        boolean result = editor.commit();
        return new ContractResponse<>(result, null);
    }

    private ContractResponse<Void> applyValue(ContractRequest request) {
        SharedPreferences.Editor editor = openEditor(request.providerId, request.table);
        putValue(editor, request.actions);
        editor.apply();
        return new ContractResponse<>(null, null);
    }

    private void putValue(SharedPreferences.Editor editor, List<ContractRequest.Action<?>> bundleActions) {
        for (ContractRequest.Action<?> action : bundleActions) {
            String function = action.function;
            String key = action.key;
            Object value = action.value;
            switch (function) {
                case FunctionValue.CLEAR:
                    editor.clear();
                    break;
                case FunctionValue.REMOVE:
                    editor.remove(key);
                    break;
                default:
                    if (value instanceof String) {
                        editor.putString(key, (String) value);
                    } else if (value instanceof Integer) {
                        editor.putInt(key, (Integer) value);
                    } else if (value instanceof Boolean) {
                        editor.putBoolean(key, (Boolean) value);
                    } else if (value instanceof Float) {
                        editor.putFloat(key, (Float) value);
                    } else if (value instanceof Long) {
                        editor.putLong(key, (Long) value);
                    } else if (value instanceof Set) {
                        Type[] genericType = value.getClass().getGenericInterfaces();
                        editor.putStringSet(key, (Set<String>) value);
                    }
                    break;
            }

        }
    }

    private MMKV mmkvTable(String table) {
        return MMKV.mmkvWithID(table);
    }


    private SharedPreferences prefsTable(String name) {
        /**
         * 偏好设置实现类源码(SharedPreferencesImpl)已经做了内存优化，commit/apply，刷新内存缓存mMap,
         * 只要sp实例不变，就不需要再实现内存缓存
         */
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }


    private SharedPreferences.Editor openEditor(@ProviderIdValue String providerId, String table) {
        if (ProviderIdValue.MMKV.equals(providerId)) {
            return mmkvTable(table);
        }
        return prefsTable(table).edit();
    }
}