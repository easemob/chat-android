package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseTitleBar);
            mLeftHeight = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_left_height, 0);
            mLeftWidth = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_left_width, 0);
            mRightHeight = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_right_height, 0);
            mRightWidth = ta.getDimension(R.styleable.EaseSearchTextView_search_drawable_right_width, 0);
            ta.recycle();
        }

        Drawable[] drawables = getCompoundDrawables();
        Drawable left = drawables[0];
        Drawable right = drawables[2];
        if(left != null) {
            left.setBounds(0, 0, (int)mLeftWidth, (int)mLeftHeight);
        }
        if(right != null) {
            right.setBounds(0, 0, (int)mRightWidth, (int)mRightHeight);
        }
        setCompoundDrawables(left, drawables[1], right, drawables[3]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
