package com.hyphenate.chatuidemo.section.friends;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.widget.ContactItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.friends.adapter.FriendsAdapter;
import com.hyphenate.chatuidemo.section.friends.viewmodels.FriendsViewModel;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


public class FriendsFragment extends BaseInitFragment implements View.OnClickListener, OnRefreshListener, EaseSidebar.OnTouchEventListener {
    private SmartRefreshLayout mSrlFriendRefresh;
    private EaseRecyclerView mRvFriendsList;
    private EaseSidebar mSideBarFriend;
    private FriendsAdapter mAdapter;
    private ContactItemView mCivNewChat;
    private ContactItemView mCivGroupChat;
    private ContactItemView mCivLabel;
    private ContactItemView mCivChatRoom;
    private ContactItemView mCivOfficialAccount;
    private FriendsViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_friends;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRvFriendsList = findViewById(R.id.rv_friends_list);
        mSrlFriendRefresh = findViewById(R.id.srl_friend_refresh);
        mSideBarFriend = findViewById(R.id.side_bar_friend);

        mRvFriendsList.setHasFixedSize(true);
        mRvFriendsList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new FriendsAdapter();
        mRvFriendsList.setAdapter(mAdapter);
        addHeader();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mCivNewChat.setOnClickListener(this);
        mCivGroupChat.setOnClickListener(this);
        mCivLabel.setOnClickListener(this);
        mCivChatRoom.setOnClickListener(this);
        mCivOfficialAccount.setOnClickListener(this);
        mSrlFriendRefresh.setOnRefreshListener(this);
        mSideBarFriend.setOnTouchEventListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_new_chat :
                showToast("new chat");
                break;
            case R.id.civ_group_chat :
                showToast("group chat");
                break;
            case R.id.civ_label :
                showToast("lable");
                break;
            case R.id.civ_chat_room :
                showToast("chat room");
                break;
            case R.id.civ_official_account :
                showToast("official account");
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        mViewModel.getContactObservable().observe(this, response -> {
            if(response.status == Status.SUCCESS) {
                finishRefresh();
                mAdapter.setData(response.data);
            }else if(response.status == Status.ERROR) {
                finishRefresh();
                showToast(response.getMessage());
            }else if(response.status == Status.LOADING) {

            }
        });
        mViewModel.loadContactList();
    }

    /**
     * 添加头布局
     */
    private void addHeader() {
        // 获取头布局，应该放在RecyclerView的setLayoutManager之后
        View header = getLayoutInflater().inflate(R.layout.em_header_friends_list, mRvFriendsList, false);
        mRvFriendsList.addHeaderView(header);

        mCivNewChat = header.findViewById(R.id.civ_new_chat);
        mCivGroupChat = header.findViewById(R.id.civ_group_chat);
        mCivLabel = header.findViewById(R.id.civ_label);
        mCivChatRoom = header.findViewById(R.id.civ_chat_room);
        mCivOfficialAccount = header.findViewById(R.id.civ_official_account);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.loadContactList();
    }

    private void finishRefresh() {
        if(mSrlFriendRefresh != null) {
            mSrlFriendRefresh.finishRefresh();
        }
    }

    @Override
    public void onActionDown(MotionEvent event) {

    }

    @Override
    public void onActionMove(MotionEvent event) {

    }

    @Override
    public void onActionUp(MotionEvent event) {

    }
}
