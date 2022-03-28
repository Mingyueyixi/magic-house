package com.lu.code.magic.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.lu.code.magic.bean.FuckDialogConfig;
import com.lu.code.magic.bean.Query;
import com.lu.code.magic.util.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {


    public static Map<String, JsonObject> getAll(SharedPreferences sp) {
//        String text = sp.getString("config", "{}");
//        return GsonUtil.fromJson(text, GsonUtil.getMapType(String.class, JsonObject.class));
        //
        return new HashMap<>();
    }

    public static FuckDialogConfig getFuckDialogConfig(String packageName) {
//        Map<String, JsonObject> map = getAll(sp);
//        JsonObject json = map.get(packageName);
//        if (json == null) {
//            json = new JsonObject();
//        }
//        FuckDialogConfig fuckDialogConfig = GsonUtil.fromJson(json, FuckDialogConfig.class);
        ContentResolver contentResolver = AppUtil.getInstance().getAppContext().getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://com.lu.code.magic/sp/getString?table=config"),
                null,
                null,
                new String[]{"fuckDialog", "{}"},
                null);
        cursor.moveToNext();
        Bundle bundle = cursor.getExtras();
        LogUtil.d(">>>", bundle);
        cursor.close();

        return new FuckDialogConfig();
    }

    public static void setFuckDialogConfig(String packageName, FuckDialogConfig fuckDialogConfig) {
//        SharedPreferences.Editor editor = sp().edit();
//        editor.putString(packageName, GsonUtil.toJson(fuckDialogConfig));
//        editor.commit();
        ContentResolver contentResolver = AppUtil.getInstance().getAppContext().getContentResolver();

        String json = GsonUtil.toJson(fuckDialogConfig);
        Query<String> query = new Query<String>();
        query.setFunction("putString");
        query.setKey(packageName);
        query.setValue(json);

        String queryJson = GsonUtil.toJson(query);
        ContentValues values = new ContentValues();
        values.put("queryJson", queryJson);

        int result = contentResolver.update(
                Uri.parse("content://com.lu.code.magic/sp/commit?table=config"),
                values,
                null,
                null
        );

    }
}
