package com.lu.code.magic.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.code.magic.provider.annotation.FunctionValue;
import com.lu.code.magic.provider.annotation.GroupValue;
import com.lu.code.magic.provider.annotation.ModeValue;

import java.io.InvalidClassException;
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
public class DataShareProvider extends ContentProvider {

    // UriMatcher类使用:在ContentProvider 中注册URI
    private static final UriMatcher sMatcher;
    public static final String AUTOHORITY = "com.lu.code.magic";
    private static final String PATH_SP = "sp";
    private static final String PATH_MKV = "mmkv";

    private static final int PATH_SP_CODE = 1;
    private static final int PATH_MMKV_CODE = 2;

    public static final String baseUri = "content://com.lu.code.magic";

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 初始化
        // sp/gutString/table?=tv&key?=v&def?=0
        sMatcher.addURI(AUTOHORITY, "sp/*", PATH_SP_CODE);
        sMatcher.addURI(AUTOHORITY, "mmkv/*", PATH_MMKV_CODE);
    }

    public static final String KEY_RESULT = "result";
    public static final String KEY_THROW = "throw";

    @Override
    public boolean onCreate() {
        SharedPreferences sp = getSharePreferences("nima");
        sp.edit().putString("hh", "旺旺~~~")
                .putInt("int", 1)
                .putBoolean("bool", true)
                .commit();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private SharedPreferences getSharePreferences(String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public ContractResponse<Serializable> readValue(String group, String table, ContractRequest.Action<?> bundleAction) {
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
        return new ContractResponse<Serializable>(resultV, null);
    }

    private boolean containsKeySp(String function, String table, String key) {
        SharedPreferences sp = getSharePreferences(table);
        return sp.contains(key);
    }

    private Serializable getValueSp(String function, String table, String key, Object defValue) {
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

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        return dispatchCallMethod(method, arg, extras);
    }

    private Bundle dispatchCallMethod(String mode, String table, Bundle extras) {
        ContractRequest request = ContractUtil.toRequest(extras);
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


    private ContractResponse writeValue(String group, String table, List<ContractRequest.Action<?>> bundleActions) {
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