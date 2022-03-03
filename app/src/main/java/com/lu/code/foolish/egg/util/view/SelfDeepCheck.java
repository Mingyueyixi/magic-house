package com.lu.code.foolish.egg.util.view;

import android.view.View;

import androidx.core.util.Consumer;
import androidx.core.util.Predicate;

/**
 * @Author: Lu
 * Date: 2022/03/03
 * Description:
 */
public class SelfDeepCheck {

    public void each(View rootView, Consumer<View> function) {
        //each 自身
        function.accept(rootView);
        //each 所有子孙view
        new ChildDeepCheck().each(rootView, function);
    }

    public boolean eachCheck(View rootView, Predicate<View> function) {
        //check 自身
        boolean flag = function.test(rootView);
        if (flag) {
            return true;
        }
        //check 所有子孙view
        return new ChildDeepCheck().eachCheck(rootView, function);
    }

}
