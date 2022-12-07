package com.lu.magic.util.ripple;

import android.graphics.drawable.RippleDrawable;
import android.view.View;

public class RippleApplyUtil {
    public static void apply(View view, RippleDrawable rippleDrawable) {
        view.setClickable(true);
        view.setBackground(rippleDrawable);
    }

    public static void apply(View view, RippleBuilder builder) {
        RippleDrawable drawable = builder.create();
        view.setClickable(true);
        view.setBackground(drawable);
    }
}
