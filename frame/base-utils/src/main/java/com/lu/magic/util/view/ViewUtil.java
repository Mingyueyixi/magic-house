package com.lu.magic.util.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description:
 */
public class ViewUtil {
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public static TextCheck textCheck() {
        return new TextCheck();
    }

    public static boolean haveChild(View rootView, View child) {
        if (!(rootView instanceof ViewGroup)) {
            return false;
        }
        return new ChildDeepCheck().eachCheck(rootView, view -> child == view);
    }

    public static void setVisibility(@Visibility int flag, View... views) {
        if (views == null) {
            return;
        }
        if (flag != View.VISIBLE && flag != View.GONE && flag != View.INVISIBLE) {
            return;
        }
        for (View view : views) {
            if (view == null) {
                continue;
            }
            view.setVisibility(flag);
        }
    }


}
