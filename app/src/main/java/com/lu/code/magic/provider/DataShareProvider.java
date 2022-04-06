package com.lu.code.magic.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.code.magic.provider.annotation.FunctionValue;
import com.lu.code.magic.provider.annotation.GroupValue;
import com.lu.code.magic.provider.annotation.ModeValue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <prefix>://<authority>/<data_type>/<id>
 * content://com.lu.code.magic/mmkv/
 * content://com.lu.code.magic/sp/
 *
 * @author Lu
 */
public class DataShareProvider extends BaseCallProvider {
    public static final String AUTOHORITY = "com.lu.code.magic";
    public static final String baseUri = "content://com.lu.code.magic";

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        return dispatchCallMethod(method, arg, extras);
    }


    private Bundle dispatchCallMethod(String mode, String table, Bundle extras) {
        ContractRequest request = ContractUtil.toContractRequest(extras);
        ContractResponse<Serializable> response = new ContractResponse<>(null, null);
        switch (request.mode) {
            case ModeValue.READ:
                if (request.actions != null && request.actions.size() > 0) {
                    response = readValue(request.group, table, request.actions.get(0));
                }
                break;
            case ModeValue.WRITE:
                if (request.actions != null && request.actions.size() > 0) {
                    response = writeValue(request.group, table, request.actions);
                }
                break;
            default:
                break;
        }
        return ContractUtil.toBundleResponse(response);
    }


    private SharedPreferences getSharePreferences(String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public ContractResponse<Serializable> readValue(@GroupValue String group, String table, ContractRequest.Action<?> bundleAction) {
        String function = bundleAction.function;
        String key = bundleAction.key;
        Object defValue = bundleAction.value;
        Serializable resultV = null;
        switch (group) {
            case GroupValue.GET:
                resultV = getValueSp(function, table, key, defValue);
                break;
            case GroupValue.CONTAINS:
                resultV = containsKeySp(function, table, key);
                break;
        }
        return new ContractResponse<>(resultV, null);
    }

    private boolean containsKeySp(@FunctionValue String function, String table, String key) {
        return getSharePreferences(table).contains(key);
    }

    private Serializable getValueSp(@FunctionValue String function, String table, String key, Object defValue) {
        SharedPreferences sp = getSharePreferences(table);
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


    private ContractResponse writeValue(@GroupValue String group, String table, List<ContractRequest.Action<?>> bundleActions) {
        switch (group) {
            case GroupValue.COMMIT:
                return commitValue(table, bundleActions);
            case GroupValue.APPLY:
                return applyValue(table, bundleActions);
            default:
                break;
        }
        return new ContractResponse<Void>(null, null);
    }

    private ContractResponse<Boolean> commitValue(String table, List<ContractRequest.Action<?>> bundleActions) {
        SharedPreferences.Editor editor = getSharePreferences(table).edit();
        putValue(editor, bundleActions);
        boolean result = editor.commit();
        return new ContractResponse<>(result, null);
    }

    private ContractResponse<Void> applyValue(String table, List<ContractRequest.Action<?>> bundleActions) {
        SharedPreferences.Editor editor = getSharePreferences(table).edit();
        putValue(editor, bundleActions);
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


}