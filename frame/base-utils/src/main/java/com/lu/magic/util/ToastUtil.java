package com.lu.magic.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {
    public static void show(Context context, int textId) {
        if (context == null) {
            return;
        }
        Toast.makeText(AppUtil.getContext(), textId, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, String text) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String text) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int textId) {
        if (context == null) {
            return;
        }
        Toast.makeText(AppUtil.getContext(), textId, Toast.LENGTH_LONG).show();
    }


    public static void show(int textId) {
        show(AppUtil.getContext(), textId);
    }

    public static void show(String text) {
        show(AppUtil.getContext(), text);
    }

    public static void showLong(String text) {
        showLong(AppUtil.getContext(), text);
    }

    public static void showLong(int textId) {
        showLong(AppUtil.getContext(), textId);
    }


}
