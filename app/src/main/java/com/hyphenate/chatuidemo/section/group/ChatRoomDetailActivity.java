package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;

public class ChatRoomDetailActivity extends BaseInitActivity {
    private String roomId;

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
}
