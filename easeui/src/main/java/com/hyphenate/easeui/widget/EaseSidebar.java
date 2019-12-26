/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeui.R;
import com.hyphenate.util.DensityUtil;

/**
 * side bar
 */
public class EaseSidebar extends View{
	private Paint paint;
	private float height;
	private Context context;
	private OnTouchEventListener mListener;
	private String[] sections;
	private String topText;
	private int mTextColor;
	private static final String DEFAULT_COLOR = "#8C8C8C";
	private static final float DEFAULT_TEXT_SIZE = 10;
	private float mTextSize;
	private int mBgColor;

	public EaseSidebar(Context context) {
		this(context, null);
	}

	public EaseSidebar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EaseSidebar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		initAttrs(attrs);
		init();
	}

	private void initAttrs(AttributeSet attrs) {
		if(attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EaseSidebar);
			int topTextId = a.getResourceId(R.styleable.EaseSidebar_ease_side_bar_top_text, -1);
			if(topTextId != -1) {
				topText = context.getResources().getString(topTextId);
			}else {
				topText = a.getString(R.styleable.EaseSidebar_ease_side_bar_top_text);
			}
			int textColorId = a.getResourceId(R.styleable.EaseSidebar_ease_side_bar_text_color, -1);
			if(textColorId != -1) {
				mTextColor = ContextCompat.getColor(context, textColorId);
			}else {
				mTextColor = a.getColor(R.styleable.EaseSidebar_ease_side_bar_text_color, Color.parseColor(DEFAULT_COLOR));
			}
			mTextSize = a.getDimension(R.styleable.EaseSidebar_ease_side_bar_text_size, DEFAULT_TEXT_SIZE);
			int bgId = a.getResourceId(R.styleable.EaseSidebar_ease_side_bar_background, -1);
			if(bgId != -1) {
				mBgColor = ContextCompat.getColor(context, textColorId);
			}else {
				mBgColor = a.getColor(R.styleable.EaseSidebar_ease_side_bar_background, Color.TRANSPARENT);
			}
		}
	}

	private void init(){
	    if(TextUtils.isEmpty(topText)) {
	        topText = context.getString(R.string.search_new);
	    }
        sections= new String[]{topText,"A","B","C","D","E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","#"};
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(mTextColor);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(mTextSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mBgColor != Color.TRANSPARENT) {
			canvas.drawColor(mBgColor);
		}
		float center = getWidth() / 2;
		height = getHeight() / sections.length;
		for (int i = sections.length - 1; i > -1; i--) {
			canvas.drawText(sections[i], center, height * (i+1), paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:{
			// 提供对外的接口，进行操作
			if(mListener != null) {
			    mListener.onActionDown(event);
			}
			return true;
		}
		case MotionEvent.ACTION_MOVE:{
			// 提供对外的接口，便于开发者操作
			if(mListener != null) {
			    mListener.onActionMove(event);
			}
			return true;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if(mListener != null) {
			    mListener.onActionUp(event);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 绘制背景色
	 * @param color
	 */
	public void drawBackground(@ColorRes int color) {
		mBgColor = ContextCompat.getColor(context, color);
		postInvalidate();
	}

	public void drawBackgroundDrawable(@DrawableRes int drawableId) {
		setBackground(ContextCompat.getDrawable(context, drawableId));
	}

	public void drawBackgroundDrawable( Drawable drawable) {
		setBackground(drawable);
	}

	public void setOnTouchEventListener(OnTouchEventListener listener) {
		this.mListener = listener;
	}

	public interface OnTouchEventListener {
		/**
		 * 按下的监听
		 * @param event
		 */
		void onActionDown(MotionEvent event);

		/**
		 * 移动的监听
		 * @param event
		 */
		void onActionMove(MotionEvent event);

		/**
		 * 抬起的监听
		 * @param event
		 */
		void onActionUp(MotionEvent event);
	}

}
