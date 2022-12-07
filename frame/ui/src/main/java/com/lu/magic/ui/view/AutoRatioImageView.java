package com.lu.magic.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * 适用于view自动按图片比例显示的场景。
 * 当未指定view宽高其中一方时，另一方由图片的宽高比例确定，
 * 一般配合scaleType="fitXY"，以达到按比例缩放view的宽/高的目的。
 */
public class AutoRatioImageView extends androidx.appcompat.widget.AppCompatImageView {
    public AutoRatioImageView(Context context) {
        super(context);
    }

    public AutoRatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoRatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp == null) {
            return;
        }
        if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            Drawable drawable = getDrawable();
            if (drawable != null) {
                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                if (h <= 0 || w <= 0) {
                    return;
                }
                float ratio = w / (float) h;
                int mH = getMeasuredHeight();
                int mW = (int) (mH * ratio);
                setMeasuredDimension(mW, mH);
            }
        } else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            Drawable drawable = getDrawable();
            if (drawable != null) {
                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                if (h <= 0 || w <= 0) {
                    return;
                }
                float ratio = w / (float) h;
                int mW = getMeasuredWidth();
                int mH = (int) (mW / ratio);

                setMeasuredDimension(mW, mH);
            }

        }
    }

}

