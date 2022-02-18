package com.lu.code.foolish.egg.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lu.code.foolish.egg.util.log.LogUtil;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * @Author: Lu
 * Date: 2022/02/18
 * Description:
 */
public class ViewUtil {

    public static boolean haveText(View rootView, String text) {
        if (rootView instanceof TextView) {
            CharSequence ch = ((TextView) rootView).getText();
            if (ch.toString().contains(text)) {
                return true;
            }
            return false;
        }

        if (rootView instanceof ViewGroup == false) {
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) rootView;
        int count = viewGroup.getChildCount();
        if (count <= 0) {
            return false;
        }
        ArrayList<ViewGroup> parentList = new ArrayList<ViewGroup>();
        parentList.add(viewGroup);
        while (parentList.size() > 0) {
            ViewGroup parent = parentList.get(0);
            parentList.remove(0);

            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                LogUtil.d(view.getClass(), view);
                if (view instanceof TextView) {
                    CharSequence viewText = ((TextView) view).getText();
                    if (viewText != null && viewText.toString().contains(text)) {
                        return true;
                    }
                } else if (view instanceof ViewGroup) {
                    parentList.add((ViewGroup) view);
                }
            }

        }
        return false;
    }

}
