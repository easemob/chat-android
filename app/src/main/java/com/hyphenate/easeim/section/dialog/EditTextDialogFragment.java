package com.hyphenate.easeim.section.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseActivity;

public class EditTextDialogFragment extends DemoDialogFragment {

    private EditText etInput;
    private String content;
    private int contentColor;
    private float contentSize;
    private int inputType = -1;
    private ConfirmClickListener listener;
    private String contentHint;

    @Override
    public int getMiddleLayoutId() {
        return R.layout.demo_fragment_dialog_edit;
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            title = bundle.getString("title");
            content = bundle.getString("content");
            inputType = bundle.getInt("inputType", 0);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        etInput = findViewById(R.id.et_input);
        if(!TextUtils.isEmpty(contentHint)) {
            etInput.setHint(contentHint);
        }
        if(!TextUtils.isEmpty(title)) {
            mTvDialogTitle.setText(title);
        }
        if(!TextUtils.isEmpty(content)) {
            etInput.setText(content);
        }
        if(contentColor != 0) {
            etInput.setTextColor(contentColor);
        }
        if(contentSize != 0) {
            etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
        }
        if(inputType != -1) {
            etInput.setInputType(inputType);
        }
    }

    @Override
    public void onConfirmClick(View v) {
        dismiss();
        String content = etInput.getText().toString().trim();
        if(listener != null) {
            listener.onConfirmClick(v, content);
        }
    }

    public void setOnConfirmClickListener(ConfirmClickListener listener) {
        this.listener = listener;
    }

    public interface ConfirmClickListener {
         void onConfirmClick(View view, String content);
    }

    public static class Builder extends DemoDialogFragment.Builder {
        private String content;
        private int contentColor;
        private float contentSize;
        private int inputType = -1;
        private String hint;
        private ConfirmClickListener listener;

        public Builder(BaseActivity context) {
            super(context);
        }

        public Builder setContent(@StringRes int title) {
            this.content = context.getString(title);
            return this;
        }

        public Builder setContent(String title) {
            this.content = title;
            return this;
        }

        public Builder setContentColor(@ColorRes int color) {
            this.contentColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setContentColorInt(@ColorInt int color) {
            this.contentColor = color;
            return this;
        }

        public Builder setContentSize(float size) {
            this.contentSize = size;
            return this;
        }

        public Builder setContentInputType(int inputType) {
            this.inputType = inputType;
            return this;
        }

        public Builder setContentHint(@StringRes int hint) {
            this.hint = context.getString(hint);
            return this;
        }

        public Builder setContentHint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setConfirmClickListener(ConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        protected DemoDialogFragment getFragment() {
            return new EditTextDialogFragment();
        }

        @Override
        public DemoDialogFragment build() {
            EditTextDialogFragment fragment = (EditTextDialogFragment) super.build();
            fragment.setContent(content);
            fragment.setContentColor(contentColor);
            fragment.setContentSize(contentSize);
            fragment.setInputType(inputType);
            fragment.setContentHint(hint);
            fragment.setOnConfirmClickListener(listener);
            return fragment;
        }

    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    private void setContentSize(float contentSize) {
        this.contentSize = contentSize;
    }

    private void setInputType(int inputType) {
        this.inputType = inputType;
    }

    private void setContentHint(String hint) {
        this.contentHint = hint;
    }
}
