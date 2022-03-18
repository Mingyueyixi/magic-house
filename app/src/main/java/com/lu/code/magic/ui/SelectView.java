package com.lu.code.magic.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.lu.code.magic.magic.R;

/**
 * @Author: Lu
 * Date: 2022/03/18
 * Description:
 */
public class SelectView extends FrameLayout {
    private TextView textView;
    private Paint mPaint;
    private Path path;
    private int tipSize;
    private int tipSpace;
    private int tipColor;

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //TextView保持默认主题色
        textView = new AppCompatTextView(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectView, defStyleAttr, defStyleRes);
        CharSequence text = a.getText(R.styleable.SelectView_android_text);
        int textSize = a.getDimensionPixelSize(R.styleable.SelectView_android_textSize, (int) textView.getTextSize());
        int textStyle = a.getInt(R.styleable.SelectView_android_textStyle, 0);
        int textColor = a.getColor(R.styleable.SelectView_android_textColor, textView.getCurrentTextColor());
        tipSpace = a.getColor(R.styleable.SelectView_tipSpace, (int) dp2px(48));
        tipColor = a.getColor(R.styleable.SelectView_tipColor, 0xFF757575);
        a.recycle();

        tipSize = (int) dp2px(12);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setText(text);
        textView.setTypeface(Typeface.defaultFromStyle(textStyle));
        textView.setTextColor(textColor);
        addView(textView, lp);

        setWillNotDraw(false);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
    }

    private float dp2px(float dp) {
        return getResources().getDisplayMetrics().density * dp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth() + tipSpace + tipSize, getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();

        int w = getWidth();
        int h = getHeight();

        path.moveTo(w, h);
        path.lineTo(w, h - tipSize);
        path.lineTo(w - tipSize, h);
        path.close();

        mPaint.setColor(tipColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, mPaint);
    }

    public int getTipColor() {
        return tipColor;
    }

    public int getTipSpace() {
        return tipSpace;
    }

}
