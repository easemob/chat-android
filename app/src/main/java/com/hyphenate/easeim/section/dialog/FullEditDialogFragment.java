package com.hyphenate.easeim.section.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.base.BaseDialogFragment;
import com.hyphenate.easeui.utils.StatusBarCompat;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class FullEditDialogFragment extends BaseDialogFragment implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private EditText etContent;
    private String content;
    private String hint;
    private OnSaveClickListener listener;
    private String title;
    private float titleSize;
    private int titleColor;
    private String titleRight;
    private int titleRightColor;
    private boolean enableEdit;//是否可以进行编辑

    public static void showDialog(BaseActivity activity, String title, String content, String hint, OnSaveClickListener listener) {
        FullEditDialogFragment fragment = new FullEditDialogFragment();
        fragment.setOnSaveClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("hint", hint);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.show(transaction, null);
    }

    @Override
    public int getLayoutId() {
        return R.layout.demo_fragment_group_edit;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppTheme);
        StatusBarCompat.setLightStatusBar(mContext, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogFullParams();
    }

    @Override
    public void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            title = bundle.getString("title");
            content = bundle.getString("content");
            hint = bundle.getString("hint");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        etContent = findViewById(R.id.et_content);

        if(TextUtils.isEmpty(content)) {
            etContent.setHint(hint);
        }else {
            etContent.setText(content);
        }

        if(!TextUtils.isEmpty(title)) {
            titleBar.setTitle(title);
        }
        if(titleColor != 0) {
            titleBar.getTitle().setTextColor(titleColor);
        }
        if(titleSize != 0) {
            titleBar.getTitle().setTextSize(titleSize);
        }
        if(!TextUtils.isEmpty(titleRight)) {
            titleBar.getRightText().setText(titleRight);
        }
        if(titleRightColor != 0) {
            titleBar.getRightText().setTextColor(titleRightColor);
        }
        if(!enableEdit) {
            titleBar.setRightLayoutVisibility(View.GONE);
            etContent.setEnabled(false);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRightClick(View view) {
        String content = etContent.getText().toString().trim();
        if(listener != null) {
            listener.onSaveClick(view, content);
        }
        dismiss();
    }

    @Override
    public void onBackPress(View view) {
        dismiss();
    }

    public interface OnSaveClickListener {
        void onSaveClick(View view, String content);
    }

    public static class Builder {
        private BaseActivity context;
        private String title;
        private String hint;
        private String content;
        private int titleColor;
        private float titleSize;
        private String confirmText;
        private OnSaveClickListener listener;
        private int confirmColor;
        private boolean enableEdit = true;//默认可以编辑
        private Bundle bundle;

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

        public Builder setTitleSize(float size) {
            this.titleSize = size;
            return this;
        }

        public Builder setOnConfirmClickListener(@StringRes int confirm, OnSaveClickListener listener) {
            this.confirmText = context.getString(confirm);
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(String confirm, OnSaveClickListener listener) {
            this.confirmText = confirm;
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(OnSaveClickListener listener) {
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

        public Builder setHint(@StringRes int hint) {
            this.hint = context.getString(hint);
            return this;
        }

        public Builder setHint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder enableEdit(boolean enableEdit) {
            this.enableEdit = enableEdit;
            return this;
        }

        public Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public FullEditDialogFragment build() {
            FullEditDialogFragment fragment = new FullEditDialogFragment();
            fragment.setTitle(title);
            fragment.setTitleColor(titleColor);
            fragment.setTitleSize(titleSize);
            fragment.setConfirmText(confirmText);
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setConfirmColor(confirmColor);
            fragment.setHint(hint);
            fragment.setContent(content);
            fragment.setEnableEdit(enableEdit);
            fragment.setArguments(bundle);
            return fragment;
        }

        public FullEditDialogFragment show() {
            FullEditDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.show(transaction, null);
            return fragment;
        }
    }

    private void setEnableEdit(boolean enableEdit) {
        this.enableEdit = enableEdit;
    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    private void setConfirmText(String confirmText) {
        this.titleRight = confirmText;
    }

    private void setOnConfirmClickListener(OnSaveClickListener listener) {
        this.listener = listener;
    }

    private void setConfirmColor(int confirmColor) {
        this.titleRightColor = confirmColor;
    }

    private void setHint(String hint) {
        this.hint = hint;
    }

    private void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private void setTitle(String title) {
        this.title = title;
    }

}
