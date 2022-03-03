package com.lu.code.foolish.egg.util.view;

import android.view.View;
import android.view.ViewGroup;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description:
 */
public class ViewUtil {

    public static TextCheck textCheck() {
        return new TextCheck();
    }

    public static boolean haveChild(View rootView, View child) {
        if (!(rootView instanceof ViewGroup)) {
            return false;
        }
        return new ChildDeepCheck().eachCheck(rootView, view -> child == view);
    }
}
