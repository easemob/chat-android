package com.hyphenate.easeui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hyphenate.easeui.R;

/**
 * 会话列表
 */
public class EaseConversationListLayout extends LinearLayout {

    public EaseConversationListLayout(Context context) {
        this(context, null);
    }

    public EaseConversationListLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseConversationListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_conversation_list, this);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }
}

