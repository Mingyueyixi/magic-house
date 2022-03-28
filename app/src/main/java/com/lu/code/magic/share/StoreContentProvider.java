package com.lu.code.magic.share;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentValuesKt;

import com.google.gson.JsonElement;
import com.lu.code.magic.bean.Query;
import com.lu.code.magic.magic.BuildConfig;
import com.lu.code.magic.util.GsonUtil;
import com.lu.code.magic.util.TextUtil;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * <prefix>://<authority>/<data_type>/<id>
 * content://com.lu.code.magic/mmkv/
 * content://com.lu.code.magic/sp/
 */
public class StoreContentProvider extends ContentProvider {

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
        return true;
    }


    //        Uri uri = Uri.parse("content://www.baidu.com/mmkv/getString?table=123&key=abc&defValue=a");
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int id = sMatcher.match(uri);
        String action = uri.getLastPathSegment();
        String table = uri.getQueryParameter("table");

        String key = selectionArgs[0];
        String defValue = selectionArgs[1];
        if (TextUtil.isEmpty(table)) {
            return null;
        }
        Bundle bundle = new Bundle();
        if (id == PATH_SP_CODE) {
            SharedPreferences sp = getSharePreferences(table);
            Serializable v = null;
            switch (action) {
                case "getString":
                    v = sp.getString(key, defValue);
                    break;
                case "getInt":
                    v = sp.getInt(key, Integer.parseInt(defValue));
                    break;
                case "getBoolean":
                    v = Boolean.parseBoolean(defValue);
                    break;
                case "getFloat":
                    v = sp.getFloat(key, Float.parseFloat(defValue));
                    break;
                case "getLong":
                    v = sp.getLong(key, Long.parseLong(defValue));
                    break;
                case "getStringSet":
                    Set<String> defValueSet = new LinkedHashSet<>();
                    if (projection != null) {
                        defValueSet = new LinkedHashSet<>(Arrays.asList(projection));
                    }
                    v = (Serializable) sp.getStringSet(key, defValueSet);
                    break;
                case "getAll":
                    Map<String, ?> allV = sp.getAll();
                    if (allV instanceof HashMap == false) {
                        allV = new HashMap<>(allV);
                    }
                    v = (Serializable) allV;
                    break;
            }
            bundle.putSerializable("VALUE_KEY", v);
        }
        return new BundleCursor(bundle);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable Bundle extras) {
        return super.update(uri, values, extras);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int id = sMatcher.match(uri);
        if (values == null) {
            return 0;
        }
        String table = uri.getQueryParameter("table");
        String action = uri.getLastPathSegment();

        if (id == PATH_SP_CODE) {
            SharedPreferences sp = getSharePreferences(table);
            SharedPreferences.Editor editor = sp.edit();
            switch (action) {
                case "commit":
                case "apply":
                    doActionCommitApply(editor, values, action);
                    break;
                default:
                    break;
            }
        }
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

    private static final class BundleCursor extends MatrixCursor {
        private Bundle mBundle;

        public BundleCursor(Bundle extras) {
            super(new String[]{}, 0);
            mBundle = extras;
        }

        @Override
        public Bundle getExtras() {
            return mBundle;
        }

        @Override
        public Bundle respond(Bundle extras) {
            mBundle = extras;
            return mBundle;
        }
    }


}
