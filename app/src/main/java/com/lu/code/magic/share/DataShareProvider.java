package com.lu.code.magic.share;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.code.magic.bean.Query;
import com.lu.code.magic.util.TextUtil;
import com.lu.code.magic.util.log.LogUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

    private void doActionCommitApply(SharedPreferences.Editor editor, ContentValues contentValues, String action) {
        Set<Map.Entry<String, Object>> valueSet = contentValues.valueSet();

        for (Map.Entry<String, Object> ele : valueSet) {
            String k = ele.getKey();
            Object q = ele.getValue();
            if (q instanceof Query == false) {
                continue;
            }
            Query qBean = (Query) q;
            Object v = qBean.getValue();
            String func = qBean.getFunction();
            switch (func) {
                case "remove":
                    editor.remove(k);
                    break;
                case "put":
                    putFunction(editor, k, v);
                    break;
                case "clear":
                    editor.clear();
                    break;
                default:
                    break;
            }
        }
        if (action.equals("commit")) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    private void putFunction(SharedPreferences.Editor editor, String k, Object v) {
        if (v instanceof String) {
            editor.putString(k, (String) v);
        } else if (v instanceof Integer) {
            editor.putInt(k, (Integer) v);
        } else if (v instanceof Boolean) {
            editor.putBoolean(k, (Boolean) v);
        } else if (v instanceof Float) {
            editor.putFloat(k, (Float) v);
        } else if (v instanceof Long) {
            editor.putLong(k, (Long) v);
        } else if (v instanceof Set) {
            editor.putStringSet(k, (Set<String>) v);
        }
    }

    private SharedPreferences getSharePreferences(String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public Bundle getValue(Bundle extras, String method, String table) {
        String key = extras.getString("k");
        Object defValue = extras.get("v");
        Bundle res = new Bundle();

        Serializable resultV = null;
        try {
            resultV = getValueFromSp(method, table, key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
            res.putSerializable("throw", e);
        }
        res.putSerializable(key, resultV);
        return res;
    }

    private Serializable getValueFromSp(String method, String table, String key, Object defValue) {
        SharedPreferences sp = getSharePreferences(table);
        Serializable resultV = null;
        switch (method) {
            case "getString":
//                res.putString(key, sp.getString(key, (String) defValue));
                resultV = sp.getString(key, (String) defValue);
                break;
            case "getInt":
//                res.putInt(key, sp.getInt(key, (Integer) defValue));
                resultV = sp.getInt(key, (Integer) defValue);
                break;
            case "getLong":
//                res.putLong(key, sp.getLong(key, (Long) defValue));
                resultV = sp.getLong(key, (Long) defValue);
                break;
            case "getFloat":
//                res.putFloat(key, sp.getFloat(key, (Float) defValue));
                resultV = sp.getFloat(key, (Float) defValue);
                break;
            case "getStringSet":
                Set<String> setResult = sp.getStringSet(key, (Set<String>) defValue);
//                res.putStringArray(key, setResult.toArray(new String[setResult.size()]));
                if (setResult instanceof Serializable == false) {
                    setResult = new LinkedHashSet<>(setResult);
                }
                resultV = (Serializable) setResult;
                break;
            case "getAll":
                Map<String, ?> mapResult = sp.getAll();
                if (mapResult instanceof Serializable == false) {
                    mapResult = new LinkedHashMap<>(mapResult);
                }
//                res.putSerializable(key, (Serializable) mapResult);
                resultV = (Serializable) mapResult;
                break;
            case "getBoolean":
//                res.putBoolean(key, sp.getBoolean(key, (Boolean) defValue));
                resultV = sp.getBoolean(key, (Boolean) defValue);
                break;
            default:
                break;
        }
        return resultV;

    }
//
//    @Nullable
//    @Override
//    public Bundle call(@NonNull String authority, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
//        if (!AUTOHORITY.equals(authority)) {
//            LogUtil.e(">>>", "鉴权不通过");
//            return null;
//        }
//        return getValue(extras, method);
//    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        switch (method) {
            case "commit":

                break;
            case "apply":
                break;
        }
        return getValue(extras, method, arg);
    }


}
