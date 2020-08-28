package com.hyphenate.chatuidemo.common.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

public class ViewScrollHelper {
    private static final String TAG = ViewScrollHelper.class.getSimpleName();
    private static ViewScrollHelper instance;

    private int screenWidth;
    private int viewWidth;

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

                        this.left = view.getLeft();
                        top = startY - view.getTop();

                        EMLog.i(TAG, "startX: " + startX + ", startY: " + startY + ", left: " + this.left + ", top: " + top);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getRawX() - startX) > 20 || Math.abs(event.getRawY() - startY) > 20) {
                            result = true;
                        }

                        int deltaX = (int) (event.getRawX() - startX);
                        int deltaY = (int) (event.getRawY() - startY);

                        EMLog.i("TAG", "action move dx = "+deltaX + " dy = "+ deltaY);
                        view.offsetLeftAndRight(deltaX);
                        checkTopAndBottom(deltaY, view);
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        smoothScrollToBorder(view);
                        break;
                }
                return true;
            }
        });
    }

    private void checkTopAndBottom(int deltaY, View view) {
        int top = view.getTop();
        int bottom = view.getBottom();
        EMLog.i(TAG, "top = "+top + " bottom = "+bottom);
        ViewGroup parent = (ViewGroup) view.getParent();
        if(top  + deltaY < 0 || bottom  + deltaY > parent.getHeight()) {
            if(top + deltaY < 0) {
                view.offsetTopAndBottom(-top);
            }
            if(bottom + deltaY> parent.getHeight()) {
                view.offsetTopAndBottom(parent.getHeight() - bottom);
            }
        }else {
            view.offsetTopAndBottom(deltaY);
        }
    }

    private void smoothScrollToBorder(View view) {
        EMLog.i(TAG, "screenWidth: " + screenWidth + ", viewWidth: " + viewWidth);
        int splitLine = screenWidth / 2 - viewWidth / 2;
        final int left = view.getLeft();
        final int top = view.getTop();
        int targetX;

        if (left < splitLine) {
            // 滑动到最左边
            targetX = 0;
        } else {
            // 滑动到最右边
            targetX = screenWidth - viewWidth;
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
        animator.start();
    }
}

