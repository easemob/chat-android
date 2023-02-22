package com.hyphenate.chatdemo.section.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.section.base.BaseActivity;

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
            content = bundle.getString("content");
            inputType = bundle.getInt("inputType", -1);
            contentColor = bundle.getInt("contentColor", 0);
            contentSize = bundle.getInt("contentSize", 0);
            contentHint = bundle.getString("contentHint");
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
        private ConfirmClickListener listener;

        public Builder(BaseActivity context) {
            super(context);
        }

        public Builder setContent(@StringRes int title) {
            bundle.putString("content", context.getString(title));
            return this;
        }

        public Builder setContent(String title) {
            bundle.putString("content", title);
            return this;
        }

        public Builder setContentColor(@ColorRes int color) {
            bundle.putInt("contentColor", ContextCompat.getColor(context, color));
            return this;
        }

        public Builder setContentColorInt(@ColorInt int color) {
            bundle.putInt("contentColor", color);
            return this;
        }

        public Builder setContentSize(float size) {
            bundle.putFloat("contentSize", size);
            return this;
        }

        public Builder setContentInputType(int inputType) {
            bundle.putInt("contentInputType", inputType);
            return this;
        }

        public Builder setContentHint(@StringRes int hint) {
            bundle.putString("contentHint", context.getString(hint));
            return this;
        }

        public Builder setContentHint(String hint) {
            bundle.putString("contentHint", hint);
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
            fragment.setOnConfirmClickListener(listener);
            return fragment;
        }
    }
}
