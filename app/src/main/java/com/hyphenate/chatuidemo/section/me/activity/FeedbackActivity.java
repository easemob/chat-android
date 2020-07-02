package com.hyphenate.chatuidemo.section.me.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.EditTextDialogFragment;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.Arrays;

public class FeedbackActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemQuestionType;
    private EditText etContent;
    private ArrowItemView itemEmail;
    private ArrowItemView itemQq;
    private static final String[] questions = {"BUG反馈", "优化建议", "其它"};
    private AlertDialog dialog;
    private int selectedPosition;

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
                showPopuWindowDialog();
                break;
            case R.id.item_email ://邮箱
                showAddEmailDialog();
                break;
            case R.id.item_qq ://qq
                showAddQQDialog();
                break;
        }
    }

    private void showPopuWindowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View root = LayoutInflater.from(mContext).inflate(R.layout.demo_layout_feedback_question_type, null);
        RecyclerView rvFeedbackList = root.findViewById(R.id.rv_feedback_list);
        QuestionTypeAdapter adapter = new QuestionTypeAdapter();
        rvFeedbackList.setLayoutManager(new LinearLayoutManager(mContext));
        rvFeedbackList.setAdapter(adapter);
        adapter.setData(Arrays.asList(questions));
        builder.setView(root);
        dialog = builder.create();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String question = questions[selectedPosition];
                itemQuestionType.getTvContent().setText(question);
            }
        });
        dialog.show();
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

    private class QuestionTypeAdapter extends EaseBaseRecyclerViewAdapter<String> {

        @Override
        public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.demo_item_feedback_question_type, parent, false);
            return new MyViewHolder(view);
        }

        private class MyViewHolder extends ViewHolder<String> {
            private TextView tvQuestionTitle;
            private CheckBox cbQuestion;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                tvQuestionTitle = findViewById(R.id.tv_question_title);
                cbQuestion = findViewById(R.id.cb_question);
            }

            @Override
            public void setData(String item, int position) {
                tvQuestionTitle.setText(item);
                if(selectedPosition == position) {
                    cbQuestion.setChecked(true);
                }else {
                    cbQuestion.setChecked(false);
                }
            }


        }

        @Override
        public void itemClickAction(View v, int position) {
            super.itemClickAction(v, position);
            selectedPosition = position;
            notifyDataSetChanged();
            if(dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
