package com.hyphenate.chatuidemo.section.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.widget.ContactItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.friends.adapter.FriendsAdapter;
import com.hyphenate.easeui.widget.EaseRecyclerView;

public class FriendsFragment extends BaseInitFragment implements View.OnClickListener {
    private EaseRecyclerView mRvFriendsList;
    private FriendsAdapter mAdapter;
    private ContactItemView mCivNewChat;
    private ContactItemView mCivGroupChat;
    private ContactItemView mCivLabel;
    private ContactItemView mCivChatRoom;
    private ContactItemView mCivOfficialAccount;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_friends;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRvFriendsList = findViewById(R.id.rv_friends_list);

        View header = LayoutInflater.from(mContext).inflate(R.layout.em_header_friends_list, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(layoutParams);
        mRvFriendsList.addHeaderView(header);

        mCivNewChat = header.findViewById(R.id.civ_new_chat);
        mCivGroupChat = header.findViewById(R.id.civ_group_chat);
        mCivLabel = header.findViewById(R.id.civ_label);
        mCivChatRoom = header.findViewById(R.id.civ_chat_room);
        mCivOfficialAccount = header.findViewById(R.id.civ_official_account);

        mRvFriendsList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new FriendsAdapter();
        mRvFriendsList.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mCivNewChat.setOnClickListener(this);
        mCivGroupChat.setOnClickListener(this);
        mCivLabel.setOnClickListener(this);
        mCivChatRoom.setOnClickListener(this);
        mCivOfficialAccount.setOnClickListener(this);
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
}
