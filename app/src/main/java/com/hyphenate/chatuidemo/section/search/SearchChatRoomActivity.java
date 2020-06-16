package com.hyphenate.chatuidemo.section.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.chat.ChatActivity;
import com.hyphenate.chatuidemo.section.friends.adapter.ChatRoomContactAdapter;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchChatRoomActivity extends SearchActivity {
    private List<EMChatRoom> mData;
    private List<EMChatRoom> result;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchChatRoomActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new ChatRoomContactAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        result = new ArrayList<>();
        mData = DemoHelper.getInstance().getModel().chatRooms;
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
            for (EMChatRoom room : mData) {
                if(room.getName().contains(search) || room.getId().contains(search)) {
                    result.add(room);
                }
            }
            runOnUiThread(()-> adapter.setData(result));
        });
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        EMChatRoom item = ((ChatRoomContactAdapter) adapter).getItem(position);
        ChatActivity.actionStart(mContext, item.getId(), DemoConstant.CHATTYPE_CHATROOM);
    }
}
