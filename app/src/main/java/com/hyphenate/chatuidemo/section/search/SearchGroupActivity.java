package com.hyphenate.chatuidemo.section.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.chat.ChatActivity;
import com.hyphenate.chatuidemo.section.friends.adapter.GroupContactAdapter;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupActivity extends SearchActivity {
    private List<EMGroup> mData;
    private List<EMGroup> result;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchGroupActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new GroupContactAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        result = new ArrayList<>();
        mData = EMClient.getInstance().groupManager().getAllGroups();
    }

    @Override
    public void searchMessages(String search) {
        searchResult(search);
    }


    private void searchResult(String search) {
        if(mData == null || mData.isEmpty()) {
            return;
        }

        ThreadManager.getInstance().runOnIOThread(()-> {
            result.clear();
            for (EMGroup group : mData) {
                if(group.getGroupName().contains(search) || group.getGroupId().contains(search)) {
                    result.add(group);
                }
            }
            runOnUiThread(()-> adapter.setData(result));
        });
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        //跳转到群聊页面
        EMGroup group = ((GroupContactAdapter)adapter).getItem(position);
        ChatActivity.actionStart(mContext, group.getGroupId(), DemoConstant.CHATTYPE_GROUP);
    }
}
