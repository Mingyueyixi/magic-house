package com.lu.magic.util.ripple;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;

import androidx.annotation.ColorInt;

public class RectangleRippleBuilder implements RippleBuilder {
    private int contentColor;
    private int rippleColor;

    private int radius;

    public RectangleRippleBuilder(@ColorInt int contentColor, @ColorInt int rippleColor) {
        this(contentColor, rippleColor, 0);
    }

    public RectangleRippleBuilder(@ColorInt int contentColor, @ColorInt int rippleColor, int radius) {
        this.contentColor = contentColor;
        this.rippleColor = rippleColor;
        this.radius = radius;
    }

    @Override
    public RippleDrawable create() {
        GradientDrawable content = new GradientDrawable();
        content.setCornerRadius(radius);
        content.setShape(GradientDrawable.RECTANGLE);
        content.setColor(contentColor);

        //波纹能到达的位置
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(ColorStateList.valueOf(0xFFFFFFFF));
        mask.setShape(GradientDrawable.RECTANGLE);
        mask.setCornerRadius(radius);

        return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, mask);
    }
}
