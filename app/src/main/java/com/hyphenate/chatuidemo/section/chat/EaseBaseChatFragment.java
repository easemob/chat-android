package com.hyphenate.chatuidemo.section.chat;

import android.os.Bundle;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class EaseBaseChatFragment extends BaseInitFragment implements SwipeRefreshLayout.OnRefreshListener {
    private EaseTitleBar mTitleBarMessage;
    private SwipeRefreshLayout mChatSwipeLayout;
    private RecyclerView mMessageList;

    @Override
    protected int getLayoutId() {
        return R.layout.ease_fragment_chat;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBarMessage = findViewById(R.id.title_bar_message);
        mChatSwipeLayout = findViewById(R.id.chat_swipe_layout);
        mMessageList = findViewById(R.id.message_list);


    }

    @Override
    protected void initListener() {
        super.initListener();
        mChatSwipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {

    }

    private void finishRefresh() {
        if(mChatSwipeLayout != null) {
            mChatSwipeLayout.setRefreshing(false);
        }
    }
}
