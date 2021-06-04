package com.hyphenate.easeim.section.chat.views;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

public class ChatRowConferenceInvite extends EaseChatRow {

    private TextView contentView;

    public ChatRowConferenceInvite(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.demo_row_sent_conference_invite : R.layout.demo_row_received_conference_invite, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String message = txtBody.getMessage();
        if(!TextUtils.isEmpty(message) && message.contains("-")) {
            message = message.substring(0, message.indexOf("-") + 1) + "\n" + message.substring(message.indexOf("-") + 1);
        }
        contentView.setText(message);
    }
}
