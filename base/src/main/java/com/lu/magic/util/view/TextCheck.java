package com.lu.magic.util.view;

import android.view.View;
import android.widget.TextView;

import com.lu.magic.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lu
 * Date: 2022/03/03
 * Description:
 */
public class TextCheck {

    public List<String> getAllText(View rootView) {
        List<String> stringList = new ArrayList<>();
        new SelfDeepCheck().each(rootView, view -> {
            if (view instanceof TextView) {
                stringList.add(((TextView) view).getText() + "");
            }
        });
        return stringList;
    }

    public boolean matchesText(View rootView, String regex) {
        return new SelfDeepCheck().eachCheck(rootView, view -> {
            if (view instanceof TextView) {
                String input = ((TextView) view).getText() + "";
                return TextUtil.matches(regex, input);
            }
            return false;
        });
    }


    public boolean findText(View rootView, String regex) {
        return new SelfDeepCheck().eachCheck(rootView, view -> {
            if (view instanceof TextView) {
                String input = ((TextView) view).getText() + "";
                return TextUtil.find(regex, input);
            }
            return false;
        });
    }

    public boolean findText(View rootView, String regex,int flag) {
        return new SelfDeepCheck().eachCheck(rootView, view -> {
            if (view instanceof TextView) {
                String input = ((TextView) view).getText() + "";
                return TextUtil.find(regex, input,flag);
            }
            return false;
        });
    }

    public boolean haveText(View rootView, String text) {

        return new SelfDeepCheck().eachCheck(rootView, view -> {
            if (view instanceof TextView) {
                String input = ((TextView) view).getText() + "";
                return input.contains(text);
            }
            return false;
        });
    }

}
