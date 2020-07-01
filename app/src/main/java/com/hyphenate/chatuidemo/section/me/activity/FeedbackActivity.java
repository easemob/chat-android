package com.hyphenate.chatuidemo.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.EditTextDialogFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class FeedbackActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemQuestionType;
    private EditText etContent;
    private ArrowItemView itemEmail;
    private ArrowItemView itemQq;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, FeedbackActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_feedback;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemQuestionType = findViewById(R.id.item_question_type);
        etContent = findViewById(R.id.et_content);
        itemEmail = findViewById(R.id.item_email);
        itemQq = findViewById(R.id.item_qq);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
        itemQuestionType.setOnClickListener(this);
        itemEmail.setOnClickListener(this);
        itemQq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_question_type ://反馈问题类型

                break;
            case R.id.item_email ://邮箱
                showAddEmailDialog();
                break;
            case R.id.item_qq ://qq
                showAddQQDialog();
                break;
        }
    }

    private void showAddEmailDialog() {
        new EditTextDialogFragment.Builder(mContext)
                .setContentHint(R.string.em_feedback_email_hint)
                .setContent(getContent(itemEmail, R.string.em_feedback_email_hint))
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        itemEmail.getTvContent().setText(content);
                    }
                })
                .setTitle(R.string.em_feedback_email_title)
                .show();

    }

    private void showAddQQDialog() {
        new EditTextDialogFragment.Builder(mContext)
                .setContentHint(R.string.em_feedback_qq_hint)
                .setContent(getContent(itemQq, R.string.em_feedback_qq_hint))
                .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view, String content) {
                        itemQq.getTvContent().setText(content);
                    }
                })
                .setTitle(R.string.em_feedback_qq_title)
                .show();
    }

    private String getContent(ArrowItemView item, int stringRes) {
        String content = item.getTvContent().getText().toString().trim();
        if(TextUtils.equals(content, getString(stringRes))) {
            return "";
        }
        return content;
    }

    @Override
    public void onRightClick(View view) {

    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
