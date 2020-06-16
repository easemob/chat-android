package com.hyphenate.chatuidemo.section.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;

public class SearchGroupChatActivity extends SearchActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchGroupChatActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return null;
    }

    @Override
    public void searchMessages(String search) {

    }

    @Override
    protected void onChildItemClick(View view, int position) {

    }
}
