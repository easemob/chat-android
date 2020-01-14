package com.hyphenate.chatuidemo.section.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.ChatActivity;
import com.hyphenate.chatuidemo.section.chat.ChatVideoCallActivity;
import com.hyphenate.chatuidemo.section.chat.ChatVoiceCallActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ContactDetailActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar mEaseTitleBar;
    private EaseImageView mAvatarUser;
    private TextView mTvName;
    private TextView mTvNote;
    private TextView mBtnChat;
    private TextView mBtnVoice;
    private TextView mBtnVideo;
    private TextView mBtnAddContact;
    private Group mGroupFriend;

    private EaseUser mUser;
    private boolean mIsFriend;

    public static void actionStart(Context context, EaseUser user) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, EaseUser user, boolean isFriend) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("isFriend", isFriend);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_friends_contact_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.em_friends_contact_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_black :
                showToast("添加到黑名单");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mUser = (EaseUser) getIntent().getSerializableExtra("user");
        mIsFriend = getIntent().getBooleanExtra("isFriend", true);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mEaseTitleBar = findViewById(R.id.title_bar_contact_detail);
        mAvatarUser = findViewById(R.id.avatar_user);
        mTvName = findViewById(R.id.tv_name);
        mTvNote = findViewById(R.id.tv_note);
        mBtnChat = findViewById(R.id.btn_chat);
        mBtnVoice = findViewById(R.id.btn_voice);
        mBtnVideo = findViewById(R.id.btn_video);
        mBtnAddContact = findViewById(R.id.btn_add_contact);
        mGroupFriend = findViewById(R.id.group_friend);

        if(mIsFriend) {
            mGroupFriend.setVisibility(View.VISIBLE);
            mBtnAddContact.setVisibility(View.GONE);
        }else {
            mGroupFriend.setVisibility(View.GONE);
            mBtnAddContact.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        mEaseTitleBar.setOnBackPressListener(this);
        mTvNote.setOnClickListener(this);
        mBtnChat.setOnClickListener(this);
        mBtnVoice.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
        mBtnAddContact.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        if(mUser != null) {
            mTvName.setText(mUser.getNickname());
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_note :
                showToast("跳转到备注设置");
                break;
            case R.id.btn_chat :
                ChatActivity.actionStart(mContext, mUser.getUsername(), EaseConstant.CHATTYPE_SINGLE);
                break;
            case R.id.btn_voice :
                ChatVoiceCallActivity.actionStart(mContext, mUser.getUsername());
                break;
            case R.id.btn_video :
                ChatVideoCallActivity.actionStart(mContext, mUser.getUsername());
                break;
            case R.id.btn_add_contact :
                showToast("添加为好友");
                break;
        }
    }
}
