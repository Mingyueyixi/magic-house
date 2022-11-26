package com.lu.magic.util;

import android.content.res.Resources;
import android.util.TypedValue;

public class SizeUtil {

    public static float dp2px(Resources resources, float dp) {
        return getPxSize(resources, TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    public static float getPxSize(Resources resources, int unit, float size) {
        return TypedValue.applyDimension(unit, size, resources.getDisplayMetrics());
    }

    public static float sp2px(Resources resources, float sp) {
        return getPxSize(resources, TypedValue.COMPLEX_UNIT_SP, sp);
    }

}
