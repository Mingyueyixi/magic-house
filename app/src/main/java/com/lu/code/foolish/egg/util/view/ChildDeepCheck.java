package com.lu.code.foolish.egg.util.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Consumer;
import androidx.core.util.Predicate;

import java.util.ArrayList;

/**
 * @Author: Lu
 * Date: 2022/03/03
 * Description:
 */

public class ChildDeepCheck {

    public void each(View rootView, Consumer<View> function) {
        if (rootView == null || !(rootView instanceof ViewGroup)) {
            return;
        }
        function.accept(rootView);
        ViewGroup viewGroup = (ViewGroup) rootView;
        ArrayList<ViewGroup> parentList = new ArrayList<>();
        parentList.add(viewGroup);

        while (parentList.size() > 0) {
            ViewGroup parent = parentList.get(0);
            parentList.remove(0);
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);

                function.accept(view);
                if (view instanceof ViewGroup) {
                    parentList.add((ViewGroup) view);
                }
            }
        }
    }

    public boolean eachCheck(View rootView, Predicate<View> function) {
        if (rootView == null || !(rootView instanceof ViewGroup)) {
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) rootView;
        ArrayList<ViewGroup> parentList = new ArrayList<>();
        parentList.add(viewGroup);

        while (parentList.size() > 0) {
            ViewGroup parent = parentList.get(0);
            parentList.remove(0);
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                //只要不返回true，每个子view都检测
                if (function.test(view)) {
                    return true;
                }
                if (view instanceof ViewGroup) {
                    parentList.add((ViewGroup) view);
                }
            }
        }
        return false;
    }

}
