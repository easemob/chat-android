package com.hyphenate.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.contact.viewmodels.NewChatRoomViewModel;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;

import androidx.lifecycle.ViewModelProvider;

public class NewChatRoomActivity extends BaseInitActivity implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private EditText etGroupName;
    private EditText etGroupIntroduction;
    private NewChatRoomViewModel viewModel;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, NewChatRoomActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_new_chat_room;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        etGroupName = findViewById(R.id.et_group_name);
        etGroupIntroduction = findViewById(R.id.et_group_introduction);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(NewChatRoomViewModel.class);
        viewModel.chatRoomObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(EMChatRoom data) {
                    LiveDataBus.get().with(DemoConstant.CHAT_ROOM_CHANGE).postValue(EaseEvent.create(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.TYPE.CHAT_ROOM));
                    finish();
                }
            });
        });
    }

    @Override
    public void onRightClick(View view) {
        createChatRoom();
    }

    private void createChatRoom() {
        String name = etGroupName.getText().toString();
        if(TextUtils.isEmpty(name)) {
            new SimpleDialogFragment.Builder(mContext)
                    .setTitle(R.string.em_chat_room_new_name_cannot_be_empty)
                    .show();
            return;
        }
        String desc = etGroupIntroduction.getText().toString();
        viewModel.createChatRoom(name, desc, "welcome join chat", 500, null);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
