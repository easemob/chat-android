package com.hyphenate.chatuidemo.section.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatuidemo.section.base.BaseActivity;

public class SimpleDialogFragment extends DemoDialogFragment {
    public static final String MESSAGE_KEY = "message";

    @Override
    public void initArgument() {
        if(getArguments() != null) {
            title = getArguments().getString(MESSAGE_KEY);
        }
    }

    public static class Builder extends DemoDialogFragment.Builder {

        public Builder(BaseActivity context) {
            super(context);
        }

        @Override
        protected DemoDialogFragment getFragment() {
            return new SimpleDialogFragment();
        }

    }


}
