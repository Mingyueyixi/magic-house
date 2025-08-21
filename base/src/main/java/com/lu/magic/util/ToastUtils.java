package com.lu.magic.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.lu.magic.LibBase;


/**
 * @author Lu
 * @date 2023/10/19 19:27
 * @description toast工具封装
 */
public class ToastUtils {
    private ToastUtils() {

    }

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static Toast toast;
    private static boolean blackStyle = false;

    public static void shotLongBottom(final String message) {
        toastMessage(message, true, Gravity.BOTTOM);
    }

    public static void showBottom(final String message) {
        toastMessage(message, false, Gravity.BOTTOM);
    }

    public static void showLongCenter(final String message) {
        toastMessage(message, true, Gravity.CENTER);
    }

    public static void showCenter(final String message) {
        toastMessage(message, false, Gravity.CENTER);
    }

    public static void show(final String message) {
        showCenter(message);
    }

    public static void show(final String message, boolean isLong, int gravity) {
        toastMessage(message, isLong, gravity);
    }

    public static void shotLongBottom(@StringRes int message) {
        toastMessage(message, true, Gravity.BOTTOM);
    }

    public static void showBottom(@StringRes int message) {
        toastMessage(message, false, Gravity.BOTTOM);
    }

    public static void showLongCenter(@StringRes int message) {
        toastMessage(message, true, Gravity.CENTER);
    }

    public static void showCenter(@StringRes int message) {
        toastMessage(message, false, Gravity.CENTER);
    }

    public static void show(@StringRes int message) {
        showCenter(message);
    }

    public static void show(@StringRes int message, boolean isLong, int gravity) {
        toastMessage(message, isLong, gravity);
    }


    private static void toastMessage(@StringRes int message, boolean isLong, int gravity) {
        toastMessage(LibBase.getApp().getString(message), isLong, gravity);
    }

    private static void toastMessage(final String message, boolean isLong, int gravity) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                toast = Toast.makeText(LibBase.getApp(), message, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                View layout = toast.getView();

                boolean tintStyle = false;
                if (blackStyle) {
                    if (layout != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            layout.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                        } else {
                            layout.setBackgroundColor(Color.BLACK);
                        }
                        tintStyle = true;
                    }
                }
                toast.setGravity(gravity, 0, 0);
                View view = toast.getView();
                if (view != null) {
                    TextView textView = view.findViewById(android.R.id.message);
                    if (textView != null) {
                        textView.setGravity(Gravity.CENTER);
                        if (tintStyle) {
                            textView.setTextColor(Color.WHITE);
                        }
                    }
                }
                toast.show();
            }
        });
    }
}
