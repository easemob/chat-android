package com.hyphenate.easeim.section.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseActivity;

public class CompleteDialogFragment extends DemoDialogFragment {
    private TextView tvContent;

    @Override
    public int getMiddleLayoutId() {
        return R.layout.demo_layout_dialog_fragment_middle;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvContent = findViewById(R.id.tv_content);

        if(!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
    }

    public static class Builder extends DemoDialogFragment.Builder {

        public Builder(BaseActivity context) {
            super(context);
        }

        @Override
        protected DemoDialogFragment getFragment() {
            return new CompleteDialogFragment();
        }
    }

}

