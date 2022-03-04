package com.lu.code.magic.util.view;

import android.view.View;
import android.widget.TextView;

import com.lu.code.magic.util.TextUtil;

/**
 * @Author: Lu
 * Date: 2022/03/03
 * Description:
 */
public class TextCheck {

    public static String getAllText(View rootView) {
        StringBuffer sb = new StringBuffer();
        new SelfDeepCheck().each(rootView, view -> {
            if (view instanceof TextView) {
                sb.append(((TextView) view).getText() + "");
            }
        });
        return sb.toString();
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
