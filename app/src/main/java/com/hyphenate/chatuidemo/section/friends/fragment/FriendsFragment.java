package com.hyphenate.chatuidemo.section.friends.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.widget.ContactItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.chat.ConferenceActivity;
import com.hyphenate.chatuidemo.section.friends.activity.ChatRoomContactManageActivity;
import com.hyphenate.chatuidemo.section.friends.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.friends.activity.GroupContactManageActivity;
import com.hyphenate.chatuidemo.section.friends.adapter.FriendsAdapter;
import com.hyphenate.chatuidemo.section.friends.viewmodels.FriendsViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FriendsFragment extends BaseInitFragment implements View.OnClickListener, OnRefreshListener, EaseSidebar.OnTouchEventListener, OnItemClickListener {
    private SmartRefreshLayout mSrlFriendRefresh;
    private EaseRecyclerView mRvFriendsList;
    private EaseSidebar mSideBarFriend;
    private TextView mFloatingHeader;

    private FriendsAdapter mAdapter;
    private ContactItemView mCivNewChat;
    private ContactItemView mCivGroupChat;
    private ContactItemView mCivLabel;
    private ContactItemView mCivChatRoom;
    private ContactItemView mCivOfficialAccount;
    private ContactItemView mCivAvConference;
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
        mFloatingHeader = findViewById(R.id.floating_header);

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
        mCivAvConference.setOnClickListener(this);
        mSrlFriendRefresh.setOnRefreshListener(this);
        mSideBarFriend.setOnTouchEventListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_new_chat :
                showToast("new chat");
                break;
            case R.id.civ_group_chat :
                GroupContactManageActivity.actionStart(mContext);
                break;
            case R.id.civ_label :
                showToast("lable");
                break;
            case R.id.civ_chat_room :
                ChatRoomContactManageActivity.actionStart(mContext);
                break;
            case R.id.civ_official_account :
                showToast("official account");
                break;
            case R.id.civ_av_conference:
                ConferenceActivity.startConferenceCall(getActivity(), null);
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        mViewModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    // 先进行排序
                    sortData(data);
                    mAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });

        });
        mViewModel.loadContactList();
    }

    private void sortData(List<EaseUser> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        Collections.sort(data, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNickname().compareTo(rhs.getNickname());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
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
        mCivAvConference = header.findViewById(R.id.civ_av_conference);
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
    public void onActionDown(MotionEvent event, String pointer) {
        showFloatingHeader(pointer);
        moveToRecyclerItem(pointer);
    }

    @Override
    public void onActionMove(MotionEvent event, String pointer) {
        showFloatingHeader(pointer);
        moveToRecyclerItem(pointer);
    }

    @Override
    public void onActionUp(MotionEvent event) {
        hideFloatingHeader();
    }

    private void moveToRecyclerItem(String pointer) {
        List<EaseUser> data = mAdapter.getData();
        if(data == null || data.isEmpty()) {
            return;
        }
        for(int i = 0; i < data.size(); i++) {
            if(TextUtils.equals(data.get(i).getInitialLetter(), pointer)) {
                LinearLayoutManager manager = (LinearLayoutManager) mRvFriendsList.getLayoutManager();
                if(manager != null) {
                    manager.scrollToPositionWithOffset(i, 0);
                }
            }
        }
    }

    /**
     * 展示滑动的字符
     * @param pointer
     */
    private void showFloatingHeader(String pointer) {
        if(TextUtils.isEmpty(pointer)) {
            hideFloatingHeader();
            return;
        }
        mFloatingHeader.setText(pointer);
        mFloatingHeader.setVisibility(View.VISIBLE);
    }

    private void hideFloatingHeader() {
        mFloatingHeader.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        EaseUser item = mAdapter.getItem(position);
        ContactDetailActivity.actionStart(mContext, item);
    }
}
