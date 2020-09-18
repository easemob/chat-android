package com.hyphenate.easeim.section.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeim.section.chat.activity.ChatActivity;
import com.hyphenate.easeim.section.contact.adapter.ChatRoomContactAdapter;
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
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_search_chat_room));
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchChatRoomContactAdapter();
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

        EaseThreadManager.getInstance().runOnIOThread(()-> {
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

    private class SearchChatRoomContactAdapter extends ChatRoomContactAdapter {
        @Override
        public int getEmptyLayoutId() {
            return R.layout.demo_layout_no_data_show_nothing;
        }
    }
}
