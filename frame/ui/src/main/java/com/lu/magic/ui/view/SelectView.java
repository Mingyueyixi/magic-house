package com.lu.magic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import com.lu.magic.frame.ui.R;


/**
 * @Author: Lu
 * Date: 2022/03/18
 * Description: 带有list菜单的view
 */
public class SelectView extends FrameLayout {
    private Paint mPaint;
    private Path mPath;
    private int tipSize;
    private int tipSpace;
    private int tipColor;
    private PopSelectMenu mPopMenu;
    private int mMenuResId;
    private TextView textView;
    private ViewGroup.LayoutParams sourceLayoutParams;

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
        textView = new TextView(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectView, defStyleAttr, defStyleRes);
        CharSequence text = a.getText(R.styleable.SelectView_android_text);
        int textSize = a.getDimensionPixelSize(R.styleable.SelectView_android_textSize, sp2px(14));
        int textStyle = a.getInt(R.styleable.SelectView_android_textStyle, 0);
        int textColor = a.getColor(R.styleable.SelectView_android_textColor, 0xFF757575);
        tipSpace = a.getColor(R.styleable.SelectView_tipSpace, dp2px(36));
        tipColor = a.getColor(R.styleable.SelectView_tipColor, 0xFF757575);
        int menuRes = a.getResourceId(R.styleable.SelectView_menuSrc, -1);

        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();

        tipSize = dp2px(12);
        mPaint.setTextSize(textSize);
        textView.setText(text);
        textView.setTypeface(Typeface.defaultFromStyle(textStyle));
        textView.setTextColor(textColor);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        addView(textView, lp);
        //高撑满，文字垂直居中
        textView.setGravity(Gravity.CENTER_VERTICAL);

        setWillNotDraw(false);

        mPopMenu = new PopSelectMenu(context, this);
        setMenuRes(menuRes);
        setClickable(true);
        setFocusable(true);
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showMenu();
                performClick();
                return true;
            }
            return false;
        });

    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (sourceLayoutParams == null) {
            sourceLayoutParams = new LayoutParams(params);
        }
    }


    public void setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener listener) {
        mPopMenu.setOnMenuItemClickListener(listener);
    }

    public void setOnMenuDismissListener(PopupMenu.OnDismissListener listener) {
        mPopMenu.setOnDismissListener(listener);
    }

    public void setOnMenuShowListener(PopSelectMenu.OnShowListener listener) {
        mPopMenu.setOnShowListener(listener);
    }

    public void showMenu() {
        if (mPopMenu.getMenu().size() > 0) {
            mPopMenu.show();
        }
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

    public void setMenuRes(int menuRes) {
        if (mMenuResId != menuRes) {
            mMenuResId = menuRes;
            mPopMenu.getMenuInflater().inflate(menuRes, mPopMenu.getMenu());
        }
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
        int preWidth = getMeasuredWidth() + tipSpace + tipSize;
        if (sourceLayoutParams != null
                && sourceLayoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT) {
            setMeasuredDimension(preWidth, getMeasuredHeight());
        }
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

    public static class PopSelectMenu extends PopupMenu {
        private OnShowListener mOnShowListener;

        public PopSelectMenu(@NonNull Context context, @NonNull View anchor) {
            super(context, anchor);
        }

        @Override
        public void show() {
            super.show();
            if (mOnShowListener != null) {
                mOnShowListener.onShow(this);
            }
        }

        public void setOnShowListener(@Nullable OnShowListener listener) {
            this.mOnShowListener = listener;
        }

        public interface OnShowListener {
            void onShow(PopupMenu popupMenu);
        }
    }
}
