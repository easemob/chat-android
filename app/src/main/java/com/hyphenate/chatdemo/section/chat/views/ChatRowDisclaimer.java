package com.hyphenate.chatdemo.section.chat.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatdemo.R;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

public class ChatRowDisclaimer extends EaseChatRow {
    private TextView tvDisclaimerLink;

    public ChatRowDisclaimer(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.ease_row_sent_disclaimer, this);
    }

    @Override
    protected void onFindViewById() {
        tvDisclaimerLink = (TextView) findViewById(R.id.tv_disclaimer_link);
    }

    @Override
    protected void onSetUpView() {
        tvDisclaimerLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.easemob.com/protocol/chatbot");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(it);
            }
        });
    }
}

