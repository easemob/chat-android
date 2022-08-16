package com.hyphenate.easeim.section.me;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.dialog.FullEditDialogFragment;
import com.hyphenate.easeim.section.dialog.LabelEditDialogFragment;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeim.common.widget.ArrowItemView;
import com.hyphenate.easeim.section.base.BaseInitFragment;
import com.hyphenate.easeim.section.dialog.DemoDialogFragment;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.login.activity.LoginActivity;
import com.hyphenate.easeim.section.me.activity.AboutHxActivity;
import com.hyphenate.easeim.section.me.activity.DeveloperSetActivity;
import com.hyphenate.easeim.section.me.activity.FeedbackActivity;
import com.hyphenate.easeim.section.me.activity.SetIndexActivity;
import com.hyphenate.easeim.section.me.activity.UserDetailActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.util.EMLog;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AboutMeFragment extends BaseInitFragment implements View.OnClickListener {
    static private String TAG =  "AboutMeFragment";
    private ConstraintLayout clUser;
    private ImageView avatar;
    private ArrowItemView itemCommonSet;
    private ArrowItemView itemFeedback;
    private ArrowItemView itemAboutHx;
    private ArrowItemView itemDeveloperSet;
    private ArrowItemView itemEmail;
    private Button mBtnLogout;
    private TextView nickName_view;
    private TextView userId_view;
    private EMUserInfo userInfo;
    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        clUser = findViewById(R.id.cl_user);
        nickName_view = findViewById(R.id.tv_nickName);
        userId_view = findViewById(R.id.tv_userId);
        avatar = findViewById(R.id.avatar);
        itemCommonSet = findViewById(R.id.item_common_set);
        itemFeedback = findViewById(R.id.item_feedback);
        itemAboutHx = findViewById(R.id.item_about_hx);
        itemDeveloperSet = findViewById(R.id.item_developer_set);
        mBtnLogout = findViewById(R.id.btn_logout);
        itemEmail = findViewById(R.id.item_email);
        nickName_view.setText(mContext.getString(R.string.account) + DemoHelper.getInstance().getCurrentUser());
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnLogout.setOnClickListener(this);
        clUser.setOnClickListener(this);
        itemCommonSet.setOnClickListener(this);
        itemFeedback.setOnClickListener(this);
        itemAboutHx.setOnClickListener(this);
        itemDeveloperSet.setOnClickListener(this);
        itemEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout :
                logout();
                break;
            case R.id.cl_user:
                if(userInfo != null){
                    UserDetailActivity.actionStart(mContext,userInfo.getNickName(),userInfo.getAvatarUrl());
                }else{
                    UserDetailActivity.actionStart(mContext,null,null);
                }
                break;
            case R.id.item_common_set:
                SetIndexActivity.actionStart(mContext);
                break;
            case R.id.item_feedback:
                FeedbackActivity.actionStart(mContext);
                break;
            case R.id.item_about_hx:
                AboutHxActivity.actionStart(mContext);
                break;
            case R.id.item_developer_set:
                DeveloperSetActivity.actionStart(mContext);
                break;
            case R.id.item_email:
                showDialog();
                break;
        }
    }

    private void logout() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_login_out_hint)
                .showCancelButton(true)
                .setOnConfirmClickListener(R.string.em_dialog_btn_confirm, new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        DemoHelper.getInstance().logout(true, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                DemoHelper.getInstance().getModel().setPhoneNumber("");
                                LoginActivity.startAction(mContext);
                                mContext.finish();
                            }

                            @Override
                            public void onError(int code, String error) {
                                EaseThreadManager.getInstance().runOnMainThread(()-> showToast(error));
                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });
                    }
                })
                .show();
    }


    @Override
    public void initData(){
        super.initData();
        addLiveDataObserver();
    }


    protected void addLiveDataObserver() {
        LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                Glide.with(mContext).load(event.message).placeholder(R.drawable.em_login_logo).into(avatar);
                if(userInfo != null){
                    userInfo.setAvatarUrl(event.message);
                }
            }
        });
        LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                nickName_view.setText(mContext.getString(R.string.push_nick) + ": " + event.message);
                userId_view.setText(mContext.getString(R.string.account) + EMClient.getInstance().getCurrentUser());
                if(userInfo != null){
                    userInfo.setNickName(event.message);
                }
            }
        });
    }

    private void showDialog(){
        new FullEditDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.em_about_email)
                .setOnConfirmClickListener(R.string.em_chat_group_read_ack_send, new FullEditDialogFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String reason) {
                        sendEmail(reason);
                    }
                })
                .setConfirmColor(R.color.em_color_brand)
                .show();
    }

    private void sendEmail(String content) {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        //邮件发送类型：无附件，纯文本
        email.setType("plain/text");
        //邮件接收者（数组，可以是多位接收者）
        String[] emailReciver = new String[]{"yunying@easemob.com"};

        String  emailTitle = "投诉建议";
        //设置邮件地址
        email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
        //设置邮件标题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
        //设置发送的内容
        email.putExtra(android.content.Intent.EXTRA_TEXT, content);
        //调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
    }
}
