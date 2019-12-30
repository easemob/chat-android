package com.hyphenate.chatuidemo.section.friends.activity;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;

public class ChatRoomContactManageActivity extends BaseInitActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ChatRoomContactManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_friends_chat_room_contact_manage;
    }
}
