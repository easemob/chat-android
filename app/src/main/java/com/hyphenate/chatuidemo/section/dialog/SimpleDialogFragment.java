package com.hyphenate.chatuidemo.section.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatuidemo.section.base.BaseActivity;

public class SimpleDialogFragment extends DemoDialogFragment {
    public static final String MESSAGE_KEY = "message";
    private String title;
    private int confirmColor;
    private String confirm;
    private boolean showCancel;
    private int titleColor;
    private String cancel;

    @Override
    public void initArgument() {
        if(getArguments() != null) {
            title = getArguments().getString(MESSAGE_KEY);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if(!TextUtils.isEmpty(title)) {
            mTvDialogTitle.setText(title);
        }
        if(titleColor != 0) {
            mTvDialogTitle.setTextColor(titleColor);
        }
        if(!TextUtils.isEmpty(confirm)) {
            mBtnDialogConfirm.setText(confirm);
        }
        if(confirmColor != 0) {
            mBtnDialogConfirm.setTextColor(confirmColor);
        }
        if(!TextUtils.isEmpty(cancel)) {
            mBtnDialogCancel.setText(cancel);
        }
        if(showCancel) {
            mGroupMiddle.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onConfirmClick(View v) {
        super.onConfirmClick(v);
        dismiss();
        if(mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick(v);
        }
    }

    @Override
    public void onCancelClick(View v) {
        super.onCancelClick(v);
        if(mOnCancelClickListener != null) {
            mOnCancelClickListener.onCancelClick(v);
        }
    }

    /**
     * 设置确定按钮的点击事件
     * @param listener
     */
    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    public static class Builder {
        private BaseActivity context;
        private String title;
        private int titleColor;
        private boolean showCancel;
        private String confirmText;
        private OnConfirmClickListener listener;
        private onCancelClickListener cancelClickListener;
        private int confirmColor;
        private Bundle bundle;
        private String cancel;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitleColor(@ColorRes int color) {
            this.titleColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setTitleColorInt(@ColorInt int color) {
            this.titleColor = color;
            return this;
        }

        public Builder showCancelButton(boolean showCancel) {
            this.showCancel = showCancel;
            return this;
        }

        public Builder setOnConfirmClickListener(@StringRes int confirm, OnConfirmClickListener listener) {
            this.confirmText = context.getString(confirm);
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(String confirm,  OnConfirmClickListener listener) {
            this.confirmText = confirm;
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setConfirmColor(@ColorRes int color) {
            this.confirmColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setConfirmColorInt(@ColorInt int color) {
            this.confirmColor = color;
            return this;
        }

        public Builder setOnCancelClickListener(@StringRes int cancel,  onCancelClickListener listener) {
            this.cancel = context.getString(cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(String cancel,  onCancelClickListener listener) {
            this.cancel = cancel;
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(onCancelClickListener listener) {
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public SimpleDialogFragment build() {
            SimpleDialogFragment fragment = new SimpleDialogFragment();
            fragment.setTitle(title);
            fragment.setTitleColor(titleColor);
            fragment.showCancelButton(showCancel);
            fragment.setConfirmText(confirmText);
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setConfirmColor(confirmColor);
            fragment.setCancelText(cancel);
            fragment.setOnCancelClickListener(cancelClickListener);
            fragment.setArguments(bundle);
            return fragment;
        }

        public SimpleDialogFragment show() {
            SimpleDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.show(transaction, null);
            return fragment;
        }
    }

    private void setCancelText(String cancel) {
        this.cancel = cancel;
    }

    private void setOnCancelClickListener(onCancelClickListener cancelClickListener) {
        this.mOnCancelClickListener = cancelClickListener;
    }

    private void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private void showCancelButton(boolean showCancel) {
        this.showCancel = showCancel;
    }

    private void setConfirmText(String confirmText) {
        this.confirm = confirmText;
    }

    private void setConfirmColor(int confirmColor) {
        this.confirmColor = confirmColor;
    }

    private void setTitle(String title) {
        this.title = title;
    }

}
