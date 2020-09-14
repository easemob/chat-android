package com.hyphenate.chatuidemo.section.chat.views;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

public class ChatRowLive extends EaseChatRow {

    private TextView contentView;

    public ChatRowLive(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.demo_row_sent_conference_invite, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        contentView.setText(txtBody.getMessage());
    }
}
