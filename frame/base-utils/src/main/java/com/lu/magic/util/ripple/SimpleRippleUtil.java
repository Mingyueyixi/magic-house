package com.lu.magic.util.ripple;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.view.View;

import androidx.annotation.ColorInt;

public class SimpleRippleUtil {

    public static ColorStateList getRippleColorStateList(@ColorInt int pressedColor, @ColorInt int normalColor) {
        int[][] stateList = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_activated},
                new int[]{}
        };
        int[] stateColorList = new int[]{
                pressedColor,
                pressedColor,
                pressedColor,
                normalColor
        };
        ColorStateList colorStateList = new ColorStateList(stateList, stateColorList);
        return colorStateList;
    }

    /**
     * 根据contentColor计算推荐的波纹颜色
     *
     * @param contentColor 内容颜色
     * @return 推荐的颜色
     */
    public static int computeRippleColor(@ColorInt int contentColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(contentColor, hsv);
        if (contentColor == Color.TRANSPARENT) {
            //hsv数组（h=0,s=0.v=0）
            //h=0 红色。这是不对的，s设置为0，丢掉饱和度
            hsv[1] = 0f;
            hsv[2] = 0.95f;
        } else {
            //饱和度
            hsv[1] = 0.16f;
            hsv[2] = 0.90f;
        }
        return Color.HSVToColor(hsv);
    }


    public static RippleDrawable getRectangleRipple(View view, @ColorInt int contentColor, @ColorInt int rippleColor) {
        return new RectangleRippleBuilder(contentColor, rippleColor).create();
    }

    public static RippleDrawable getRadiusRectangleRipple(View view, @ColorInt int contentColor, @ColorInt int rippleColor, int radius) {
        return new RectangleRippleBuilder(contentColor, rippleColor, radius).create();
    }


}
