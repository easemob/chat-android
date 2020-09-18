package com.hyphenate.easeim.common.widget;

import android.app.ProgressDialog;
import android.content.Context;

import com.hyphenate.easeim.R;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;


public class EaseProgressDialog extends ProgressDialog {
    protected EaseProgressDialog(@NonNull Context context) {
        this(context, 0);
    }

    protected EaseProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;
        private String message;
        private boolean cancelable;
        private boolean canceledOnTouchOutside;
        private OnCancelListener cancelListener;

        public Builder(@NonNull Context context) {
            mContext = context;
        }


        public Builder setLoadingMessage(@StringRes int message) {
            this.message = mContext.getString(message);
            return this;
        }

        public Builder setLoadingMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancel) {
            this.canceledOnTouchOutside = cancel;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public EaseProgressDialog build() {
            EaseProgressDialog dialog = new EaseProgressDialog(mContext, R.style.Dialog_Light);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            dialog.setMessage(message);
            dialog.setOnCancelListener(cancelListener);
            return dialog;
        }

        public EaseProgressDialog show() {
            EaseProgressDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
