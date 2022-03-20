package com.lu.code.magic.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.lu.code.magic.magic.R;

/**
 * @Author: Lu
 * Date: 2022/03/18
 * Description: 带有list菜单的view
 */
public class SelectView extends LinearLayout {
    private Paint mPaint;
    private Path mPath;
    private int tipSize;
    private int tipSpace;
    private int tipColor;
    private MenuInflater menuInflater;
    private PopupMenu mPopMenu;
    private int menuRes;
    private TextView textView;

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        //使用TextView样式
        this(context, attrs, defStyleAttr, android.R.attr.textViewStyle);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        textView = new TextView(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectView, defStyleAttr, defStyleRes);
        CharSequence text = a.getText(R.styleable.SelectView_android_text);
        int textSize = a.getDimensionPixelSize(R.styleable.SelectView_android_textSize, sp2px(14));
        int textStyle = a.getInt(R.styleable.SelectView_android_textStyle, 0);
        int textColor = a.getColor(R.styleable.SelectView_android_textColor, 0xFF757575);
        tipSpace = a.getColor(R.styleable.SelectView_tipSpace, (int) dp2px(36));
        tipColor = a.getColor(R.styleable.SelectView_tipColor, 0xFF757575);
        menuRes = a.getResourceId(R.styleable.SelectView_menu, 0);

        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();

        tipSize = (int) dp2px(12);
        mPaint.setTextSize(textSize);
        textView.setText(text);
        textView.setTypeface(Typeface.defaultFromStyle(textStyle));
        textView.setTextColor(textColor);

        addView(textView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setWillNotDraw(false);

        mPopMenu = new PopupMenu(context, this);
        if (menuRes != 0) {
            inflateMenu(mPopMenu, mPopMenu.getMenu(), menuRes);
        }
        setClickable(true);
        setFocusable(true);
        setOnClickListener(v -> {
            mPopMenu.show();
        });
    }

    public void setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener listener) {
        mPopMenu.setOnMenuItemClickListener(listener);
    }

    public void setOnDismissListener(PopupMenu.OnDismissListener listener) {
        mPopMenu.setOnDismissListener(listener);
    }

    public void showMenu() {
        mPopMenu.show();
    }

    public void dismissMenu() {
        mPopMenu.dismiss();
    }

    public PopupMenu getPopMenu() {
        return mPopMenu;
    }

    public void setTipText(CharSequence text) {
        textView.setText(text);
    }

    public void inflateMenu(PopupMenu popupMenu, Menu menu, int menuRes) {
        popupMenu.getMenuInflater().inflate(menuRes, menu);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        textView.layout(0, 0, getMeasuredWidth(), getBottom());
    }


    private int dp2px(float dp) {
        return (int) getPxSize(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    private int sp2px(float sp) {
        return (int) getPxSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    public float getPxSize(int unit, float size) {
        return TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth() + tipSpace + tipSize, getMeasuredHeight());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();

        int w = getWidth();
        int h = getHeight();

        mPath.moveTo(w, h);
        mPath.lineTo(w, h - tipSize);
        mPath.lineTo(w - tipSize, h);
        mPath.close();

        mPaint.setColor(tipColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath, mPaint);
    }

    public int getTipColor() {
        return tipColor;
    }

    public int getTipSpace() {
        return tipSpace;
    }

}
