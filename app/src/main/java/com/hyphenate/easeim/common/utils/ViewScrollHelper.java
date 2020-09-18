package com.hyphenate.easeim.common.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

/**
 * 用来帮助view控件，实现滑动
 */
public class ViewScrollHelper {
    private static final String TAG = ViewScrollHelper.class.getSimpleName();
    private static ViewScrollHelper instance;

    private int screenWidth;
    private int viewWidth;
    private int viewLeft;
    private int viewTop;
    private int viewRight;
    private int viewBottom;
    private int lastLeft, lastTop, lastRight, lastBottom;

    private ViewScrollHelper(Context context) {
        screenWidth = (int) EaseCommonUtils.getScreenInfo(context)[0];
    }

    public static ViewScrollHelper getInstance(Context context) {
        if(instance == null) {
            synchronized (ViewScrollHelper.class) {
                if(instance == null) {
                    instance = new ViewScrollHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void makeViewCanScroll(View view) {
        view.post(()-> {
            viewWidth = view.getWidth();
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            boolean result = false;

            float left;
            float top;
            float startX = 0;
            float startY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        result = false;
                        startX = event.getRawX();
                        startY = event.getRawY();

                        viewLeft = view.getLeft();
                        viewTop = view.getTop();
                        viewRight = view.getRight();
                        viewBottom = view.getBottom();

                        EMLog.i(TAG, "startX: " + startX + ", startY: " + startY + ", left: " + viewLeft + ", top: " + viewTop);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getRawX() - startX) > 20 || Math.abs(event.getRawY() - startY) > 20) {
                            result = true;
                        }

                        int deltaX = (int) (event.getRawX() - startX);
                        int deltaY = (int) (event.getRawY() - startY);

                        lastLeft = viewLeft + deltaX;
                        lastTop = viewTop + deltaY;
                        lastRight = viewRight + deltaX;
                        lastBottom = viewBottom + deltaY;
                        EMLog.i("TAG", "action move dx = "+deltaX + " dy = "+ deltaY);
                        view.layout(lastLeft, lastTop, lastRight, lastBottom);
                        break;
                    case MotionEvent.ACTION_UP:
                        smoothScrollToBorder(view);
                        break;
                }
                return result;
            }
        });
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(viewRight != 0) {
                    view.layout(lastLeft, lastTop, lastRight, lastBottom);
                }
            }
        });
    }

    private void smoothScrollToBorder(View view) {
        EMLog.i(TAG, "screenWidth: " + screenWidth + ", viewWidth: " + viewWidth);
        int splitLine = screenWidth / 2 - viewWidth / 2;
        final int left = view.getLeft();
        int top = view.getTop();
        int bottom = view.getBottom();
        int targetX;

        if (left < splitLine) {
            // 滑动到最左边
            targetX = 0;
        } else {
            // 滑动到最右边
            targetX = screenWidth - viewWidth;
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if(top < 0) {
            lastBottom = lastBottom - lastTop;
            lastTop = 0;
        }
        if(bottom > parent.getHeight()) {
            lastTop = parent.getHeight() - (lastBottom - lastTop);
            lastBottom = parent.getHeight();
        }


        ValueAnimator animator = ValueAnimator.ofInt(left, targetX);
        animator.setDuration(100)
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (view == null) return;

                        int value = (int) animation.getAnimatedValue();
                        EMLog.i("TAG", "onAnimationUpdate, value: " + value);
                        view.offsetLeftAndRight(value - view.getLeft());
                    }
                });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lastRight = lastRight - lastLeft + targetX;
                lastLeft = targetX;
                view.requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
}

