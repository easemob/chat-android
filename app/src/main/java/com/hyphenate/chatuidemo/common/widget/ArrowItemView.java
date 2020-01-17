package com.hyphenate.chatuidemo.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class ArrowItemView extends ConstraintLayout {
    private TextView tvTitle;
    private TextView tvContent;
    private ImageView ivArrow;
    private View viewDivider;
    private String title;
    private String content;
    private int titleColor;
    private int contentColor;
    private float titleSize;
    private float contentSize;
    private View root;

    public ArrowItemView(Context context) {
        this(context, null);
    }

    public ArrowItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        root = LayoutInflater.from(context).inflate(R.layout.em_layout_item_arrow, this);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        ivArrow = findViewById(R.id.iv_arrow);
        viewDivider = findViewById(R.id.view_divider);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowItemView);
        int titleResourceId = a.getResourceId(R.styleable.ArrowItemView_arrowItemTitle, -1);
        title = a.getString(R.styleable.ArrowItemView_arrowItemTitle);
        if(titleResourceId != -1) {
            title = getContext().getString(titleResourceId);
        }
        tvTitle.setText(title);

        int titleColorId = a.getResourceId(R.styleable.ArrowItemView_arrowItemTitleColor, -1);
        titleColor = a.getColor(R.styleable.ArrowItemView_arrowItemTitleColor, ContextCompat.getColor(getContext(), R.color.em_color_common_text_black));
        if(titleColorId != -1) {
            titleColor = ContextCompat.getColor(getContext(), titleColorId);
        }
        tvTitle.setTextColor(titleColor);

        int titleSizeId = a.getResourceId(R.styleable.ArrowItemView_arrowItemTitleSize, -1);
        titleSize = a.getDimension(R.styleable.ArrowItemView_arrowItemTitleSize, 16);
        if(titleSizeId != -1) {
            titleSize = getResources().getDimension(titleSizeId);
        }
        tvTitle.setTextSize(titleSize);

        int contentResourceId = a.getResourceId(R.styleable.ArrowItemView_arrowItemContent, -1);
        content = a.getString(R.styleable.ArrowItemView_arrowItemContent);
        if(contentResourceId != -1) {
            content = getContext().getString(contentResourceId);
        }
        tvContent.setText(content);

        int contentColorId = a.getResourceId(R.styleable.ArrowItemView_arrowItemContentColor, -1);
        contentColor = a.getColor(R.styleable.ArrowItemView_arrowItemContentColor, ContextCompat.getColor(getContext(), R.color.em_color_common_text_gray));
        if(contentColorId != -1) {
            contentColor = ContextCompat.getColor(getContext(), contentColorId);
        }
        tvContent.setTextColor(contentColor);

        int contentSizeId = a.getResourceId(R.styleable.ArrowItemView_arrowItemContentSize, -1);
        contentSize = a.getDimension(R.styleable.ArrowItemView_arrowItemContentSize, 14);
        if(contentSizeId != -1) {
            contentSize = getResources().getDimension(contentSizeId);
        }
        tvContent.setTextSize(contentSize);

        boolean showDivider = a.getBoolean(R.styleable.ArrowItemView_arrowItemShowDivider, true);
        viewDivider.setVisibility(showDivider ? VISIBLE : GONE);

        boolean showArrow = a.getBoolean(R.styleable.ArrowItemView_arrowItemShowArrow, true);
        ivArrow.setVisibility(showArrow ? VISIBLE : GONE);
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }
}
