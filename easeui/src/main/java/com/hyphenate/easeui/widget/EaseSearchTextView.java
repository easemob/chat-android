package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.hyphenate.easeui.R;

public class EaseSearchTextView extends AppCompatTextView {

    private float mLeftHeight;
    private float mLeftWidth;
    private float mRightHeight;
    private float mRightWidth;

    public EaseSearchTextView(Context context) {
        this(context, null);
    }

    public EaseSearchTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseSearchTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseSearchTextView);
            mLeftHeight = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_left_height, 0);
            mLeftWidth = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_left_width, 0);
            mRightHeight = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_right_height, 0);
            mRightWidth = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_right_width, 0);
            ta.recycle();
        }
        setDrawable();
    }

    private void setDrawable() {
        // If have non-compat relative drawables, then ignore leftCompat/rightCompat
        if (Build.VERSION.SDK_INT >= 17) {
            final Drawable[] existingRel = getCompoundDrawablesRelative();
            if (existingRel[0] != null || existingRel[2] != null) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                        existingRel[0],
                        existingRel[1],
                        existingRel[2],
                        existingRel[3]
                );
                return;
            }
        }
        // No relative drawables, so just set any compat drawables
        Drawable[] existingAbs = getCompoundDrawables();
        Drawable left = existingAbs[0];
        Drawable right = existingAbs[2];
        if(left != null && (mLeftWidth != 0 && mLeftHeight != 0)) {
            left.setBounds(0, 0, (int)mLeftWidth, (int)mLeftHeight);
        }
        if(right != null && (mRightWidth != 0 && mRightHeight != 0)) {
            right.setBounds(0, 0, (int)mRightWidth, (int)mRightHeight);
        }
        setCompoundDrawables(
                left != null ? left : existingAbs[0],
                existingAbs[1],
                right != null ? right : existingAbs[2],
                existingAbs[3]
        );
    }

}
