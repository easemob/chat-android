package com.hyphenate.chatuidemo.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hyphenate.chatuidemo.R;

public class SwitchItemView extends ConstraintLayout {
    private TextView tvTitle;
    private View viewDivider;
    private String title;
    private String content;
    private int titleColor;
    private int contentColor;
    private float titleSize;
    private float contentSize;
    private View root;
    private Switch switchItem;

    public SwitchItemView(Context context) {
        this(context, null);
    }

    public SwitchItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        root = LayoutInflater.from(context).inflate(R.layout.em_layout_item_switch, this);
        tvTitle = findViewById(R.id.tv_title);
        viewDivider = findViewById(R.id.view_divider);
        switchItem = findViewById(R.id.switch_item);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchItemView);
        int titleResourceId = a.getResourceId(R.styleable.SwitchItemView_switchItemTitle, -1);
        title = a.getString(R.styleable.SwitchItemView_switchItemTitle);
        if(titleResourceId != -1) {
            title = getContext().getString(titleResourceId);
        }
        tvTitle.setText(title);

        int titleColorId = a.getResourceId(R.styleable.SwitchItemView_switchItemTitleColor, -1);
        titleColor = a.getColor(R.styleable.SwitchItemView_switchItemTitleColor, ContextCompat.getColor(getContext(), R.color.em_color_common_text_black));
        if(titleColorId != -1) {
            titleColor = ContextCompat.getColor(getContext(), titleColorId);
        }
        tvTitle.setTextColor(titleColor);

        int titleSizeId = a.getResourceId(R.styleable.SwitchItemView_switchItemTitleSize, -1);
        titleSize = a.getDimension(R.styleable.SwitchItemView_switchItemTitleSize, 16);
        if(titleSizeId != -1) {
            titleSize = getResources().getDimension(titleSizeId);
        }
        tvTitle.setTextSize(titleSize);

        boolean showDivider = a.getBoolean(R.styleable.SwitchItemView_switchItemShowDivider, true);
        viewDivider.setVisibility(showDivider ? VISIBLE : GONE);

        a.recycle();
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public Switch getSwitch() {
        return switchItem;
    }
}
