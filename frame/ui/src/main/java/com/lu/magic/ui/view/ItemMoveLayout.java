package com.lu.magic.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lu.magic.frame.ui.R;


public class ItemMoveLayout extends FrameLayout {
    /**
     * 进行平移的view
     */
    private View moveView;
    private OnFlyListener mOnFlyListener;
    private OnSmoothTranslateXListener mOnSmoothTranslateXListener;
    private OnTouchTranslateXListener mOnTouchTranslateXListener;
    /**
     * 左边距飞动阈值，大于时进行飞速滑动
     */
    private int flyMarginLeft;
    /**
     * 右边距飞动阈值，大于时进行飞速滑动
     */
    private int flyMarginRight;
    /**
     * 左边距停靠值
     */
    private int dockOnMarginLeft;
    /**
     * 右边距停靠值
     */
    private int dockOnMarginRight;
    /**
     * 左边距停靠临界值，大于此值，靠到左边距 dockOnMarginLeft
     */
    private int leftStartDock;
    /**
     * 右边距停靠临界值，大于此值，靠到右边距 dockOnMarginRight
     */
    private int rightDockRight;

    private static final int MODE_FLY = 0;
    private static final int MODE_DOCK = 1;
    /**
     * 移动模式
     */
    private int moveMode;

    private float oldX;
    private float downX;

    public ItemMoveLayout(@NonNull Context context) {
        this(context, null);
    }

    public ItemMoveLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemMoveLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemMoveLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        int dp72 = (int) dp2px(72);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemMoveLayout, defStyleAttr, defStyleRes);
        moveMode = a.getInt(R.styleable.ItemMoveLayout_moveMode, MODE_FLY);
        flyMarginLeft = a.getDimensionPixelSize(R.styleable.ItemMoveLayout_flyMarginLeft, dp72);
        flyMarginRight = a.getDimensionPixelSize(R.styleable.ItemMoveLayout_flyMarginRight, dp72);
        dockOnMarginLeft = a.getDimensionPixelSize(R.styleable.ItemMoveLayout_dockOnMarginLeft, dp72);
        dockOnMarginRight = a.getDimensionPixelSize(R.styleable.ItemMoveLayout_dockOnMarginRight, dp72);
        leftStartDock = a.getDimensionPixelSize(R.styleable.ItemMoveLayout_leftStartDock, (int) (dockOnMarginLeft / 2f));
        rightDockRight = a.getDimensionPixelSize(R.styleable.ItemMoveLayout_rightStartDock, (int) (dockOnMarginRight / 2f));

        a.recycle();
    }

    private float dp2px(int v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    public void setMoveView(View moveView) {
        this.moveView = moveView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (moveView == null) {
            return super.onTouchEvent(event);
        }
        //是否消费掉事件
        boolean hasConsume = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                oldX = downX;
                break;
            case MotionEvent.ACTION_MOVE:
                //请求父布局，不要拦截掉事件，让事件传递下来，以便进行处理。
                getParent().requestDisallowInterceptTouchEvent(true);
                hasConsume = handleMoveAction(event);
                break;
            case MotionEvent.ACTION_UP:
                hasConsume = handleUpAction(event);
                break;
        }
        if (hasConsume) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean handleUpAction(MotionEvent event) {
        float tranX = moveView.getTranslationX();
        float absTranX = Math.abs(tranX);
        boolean hasConsume = false;
        if (absTranX > 0) {
            if (moveMode == MODE_FLY) {
                handleFlyActionUp();
            } else if (moveMode == MODE_DOCK) {
                if (tranX >= 0) {
                    if (absTranX > leftStartDock) {
                        smoothTranslationX(dockOnMarginLeft);
                    } else {
                        //恢复到原位
                        smoothTranslationX(0);
                    }
                } else if (tranX < 0) {
                    if (absTranX > rightDockRight) {
                        smoothTranslationX(-dockOnMarginRight);
                    } else {
                        //恢复到原位
                        smoothTranslationX(0);
                    }
                }
            }
            //消费掉事件，防止和点击事件冲突
            hasConsume = true;
        }
        return hasConsume;
    }

    private boolean handleMoveAction(MotionEvent event) {
        float x = event.getX();
        float offsetX = x - oldX;
        if (Math.abs(offsetX) > 0) {
            float tranX = moveView.getTranslationX();
            tranX = tranX + offsetX;
            if (moveMode == MODE_FLY) {
                moveView.setTranslationX(tranX);
                if (mOnTouchTranslateXListener != null) {
                    mOnTouchTranslateXListener.onTranslate(moveView, tranX);
                }
            } else if (moveMode == MODE_DOCK) {
                if ((tranX >= 0 && tranX <= dockOnMarginLeft)
                        || (tranX < 0 && (-tranX) <= dockOnMarginRight)) {
                    //向左、右偏移不能超过最大值
                    moveView.setTranslationX(tranX);
                    if (mOnTouchTranslateXListener != null) {
                        mOnTouchTranslateXListener.onTranslate(moveView, tranX);
                    }
                }
            }
            oldX = x;
        }
        return false;
    }

    /**
     * 动画横向平移到目的地
     *
     * @param endTranX x平移目的
     */
    private void smoothTranslationX(float endTranX) {
        float startX = moveView.getTranslationX();
        ValueAnimator ani = ValueAnimator.ofFloat(startX, endTranX);
        ani.setInterpolator(new AnticipateInterpolator());
        ani.setDuration(200);
        ani.addUpdateListener(animation -> {
            float v = (float) animation.getAnimatedValue();
            moveView.setTranslationX(v);
            if (mOnSmoothTranslateXListener != null) {
                mOnSmoothTranslateXListener.onTranslate(moveView, v);
            }
        });

        ani.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                moveView.setTranslationX(endTranX);
                if (mOnSmoothTranslateXListener != null) {
                    mOnSmoothTranslateXListener.onComplete(moveView);
                }
            }
        });
        ani.start();
        if (mOnSmoothTranslateXListener != null) {
            mOnSmoothTranslateXListener.onStart(moveView);
        }
    }

    private void handleFlyActionUp() {
        //向右飞
        int width = getWidth();
        int faceWidth = moveView.getWidth();

        float tranX = moveView.getTranslationX();
        if (tranX > 0) {
            //当前向右移动了
            if (tranX > flyMarginLeft) {
                //大于左边起始滑动宽度，向右飞，到当前布局最右边
                smoothFlyAni(width);
            } else {
                //复位
                smoothTranslationX(0);
            }
        } else if (tranX < 0) {
            //当前向左移动了
            float distance = Math.abs(tranX);
            if (distance > flyMarginRight) {
                //大于右边起始滑动宽度，向左飞，到当前布局最左边，直到看不见
                smoothFlyAni(-faceWidth);
            } else {
                //复位
                smoothTranslationX(0);
            }
        }
    }

    private void smoothFlyAni(float endTranX) {
        float start = moveView.getTranslationX();
        ValueAnimator ani = ValueAnimator.ofFloat(start, endTranX);
        ani.setDuration(200);
        //速度越来越快
        ani.setInterpolator(new AccelerateInterpolator());

        ani.addUpdateListener(animation -> {
            float v = (float) animation.getAnimatedValue();
            moveView.setTranslationX(v);
            if (mOnFlyListener != null) {
                mOnFlyListener.onFlying(moveView, v);
            }
        });
        ani.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //动画结束
                //fly则，隐藏
                moveView.setVisibility(View.GONE);
                if (mOnFlyListener != null) {
                    mOnFlyListener.onComplete(moveView);
                }
                //恢复位置
                moveView.setTranslationX(0);
            }
        });
        ani.start();
        if (mOnFlyListener != null) {
            mOnFlyListener.onStart(moveView);
        }
    }

    public void setOnFlyListener(OnFlyListener listener) {
        this.mOnFlyListener = listener;
    }

    public void setOnSmoothTranslateXListener(OnSmoothTranslateXListener listener) {
        this.mOnSmoothTranslateXListener = listener;
    }

    public void setOnTouchTranslateListener(OnTouchTranslateXListener listener) {
        this.mOnTouchTranslateXListener = listener;
    }

    /**
     * 飞速移除动画监听器
     */
    public interface OnFlyListener {

        void onStart(View view);

        void onFlying(View view, float offsetX);

        void onComplete(View view);
    }

    /**
     * 平移动画监听器
     */
    public interface OnSmoothTranslateXListener {
        void onStart(View view);

        void onTranslate(View view, float tranX);

        void onComplete(View view);
    }

    /**
     * 手指触摸滑动监听器
     */
    public interface OnTouchTranslateXListener {
        void onTranslate(View view, float tranX);
    }
}
