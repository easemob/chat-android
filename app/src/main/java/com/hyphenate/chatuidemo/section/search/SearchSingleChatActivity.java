package com.hyphenate.chatuidemo.section.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.section.search.adapter.SearchMessageAdapter;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;

import java.util.List;

public class SearchSingleChatActivity extends SearchActivity {
    private List<EMMessage> mData;
    private String toUsername;

    public static void actionStart(Context context, String toUsername) {
        Intent intent = new Intent(context, SearchSingleChatActivity.class);
        intent.putExtra("toUsername", toUsername);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toUsername = getIntent().getStringExtra("toUsername");
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchMessageAdapter();
    }

    @Override
    public void searchMessages(String search) {
        mData = EMClient.getInstance().chatManager().searchMsgFromDB(search, System.currentTimeMillis(), 100, toUsername, EMConversation.EMSearchDirection.UP);
        adapter.setData(mData);
    }

    @Override
    protected void onChildItemClick(View view, int position) {

    }
}
