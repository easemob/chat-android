package com.hyphenate.easeim.section.chat.activity;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.easeui.constants.EaseConstant;

public class ChatHistoryActivity extends ChatActivity {

    public static void actionStart(Context context, String userId, int chatType, String historyMsgId) {
        Intent intent = new Intent(context, ChatHistoryActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, userId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        intent.putExtra(EaseConstant.HISTORY_MSG_ID, historyMsgId);
        context.startActivity(intent);
    }

}
