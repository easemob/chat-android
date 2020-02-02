package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ChatRoomDetailActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private String roomId;
    private EaseTitleBar titleBar;
    private ArrowItemView itemChatRoomId;
    private ArrowItemView itemChatRoomName;
    private ArrowItemView itemChatRoomDescription;
    private ArrowItemView itemChatRoomOwner;
    private ArrowItemView itemChatRoomMembers;
    private ArrowItemView itemChatRoomAdmins;
    private TextView tvChatRoomRefund;

    public static void actionStart(Context context, String roomId) {
        Intent intent = new Intent(context, ChatRoomDetailActivity.class);
        intent.putExtra("roomId", roomId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_chat_chat_room_detail;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        roomId = intent.getStringExtra("roomId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemChatRoomId = findViewById(R.id.item_chat_room_id);
        itemChatRoomName = findViewById(R.id.item_chat_room_name);
        itemChatRoomDescription = findViewById(R.id.item_chat_room_description);
        itemChatRoomOwner = findViewById(R.id.item_chat_room_owner);
        itemChatRoomMembers = findViewById(R.id.item_chat_room_members);
        itemChatRoomAdmins = findViewById(R.id.item_chat_room_admins);
        tvChatRoomRefund = findViewById(R.id.tv_chat_room_refund);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemChatRoomMembers.setOnClickListener(this);
        itemChatRoomAdmins.setOnClickListener(this);
        tvChatRoomRefund.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_chat_room_members :
                showToast("群成员");
                break;
            case R.id.item_chat_room_admins :
                showToast("管理员");
                break;
            case R.id.tv_chat_room_refund :
                showToast("退出聊天室");
                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
