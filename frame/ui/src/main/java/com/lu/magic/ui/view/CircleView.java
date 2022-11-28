package com.lu.magic.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Author: Lu
 * Date: 2022/03/02
 * Description:
 */
public class CircleView extends View {
    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        int w = getWidth();
        int h = getHeight();
        int min = w > h ? h : w;
        path.addCircle(getWidth() / 2f, getHeight() / 2f, min / 2f, Path.Direction.CW);
        canvas.clipPath(path);
        super.draw(canvas);
        path.reset();
        path.addCircle(getWidth() / 2f, getHeight() / 2f, min / 2f - 2.5f, Path.Direction.CW);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawPath(path, paint);

    }

    @Override
    public void setClipBounds(Rect clipBounds) {
        super.setClipBounds(clipBounds);
    }
}
