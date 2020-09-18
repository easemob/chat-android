package com.hyphenate.easeim.section.contact.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.section.base.BaseInitFragment;
import com.hyphenate.easeim.section.chat.activity.ChatActivity;
import com.hyphenate.easeim.section.group.activity.NewChatRoomActivity;
import com.hyphenate.easeim.section.contact.adapter.ChatRoomContactAdapter;
import com.hyphenate.easeim.section.contact.viewmodels.ChatRoomContactViewModel;
import com.hyphenate.easeui.interfaces.EaseChatRoomListener;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public class ChatRoomContactManageFragment extends BaseInitFragment implements OnRefreshLoadMoreListener, OnItemClickListener {
    private int pageNum = 1;
    private static final int PAGE_SIZE = 50;
    private SmartRefreshLayout mSrlCommonRefresh;
    private EaseRecyclerView mRvCommonList;
    private ChatRoomContactAdapter mAdapter;
    private ChatRoomContactViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_common_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mSrlCommonRefresh = findViewById(R.id.srl_common_refresh);
        mRvCommonList = findViewById(R.id.rv_common_list);

        mRvCommonList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ChatRoomContactAdapter();
        mRvCommonList.setAdapter(mAdapter);

        //addHeaderView();
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(mContext).get(ChatRoomContactViewModel.class);
        mViewModel.getLoadObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EMChatRoom>>() {
                @Override
                public void onSuccess(List<EMChatRoom> data) {
                    mAdapter.setData(data);
                    DemoHelper.getInstance().getModel().chatRooms = mAdapter.getData();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });
        mViewModel.getLoadMoreObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EMChatRoom>>() {
                @Override
                public void onSuccess(List<EMChatRoom> data) {
                    mAdapter.addData(data);
                    DemoHelper.getInstance().getModel().chatRooms = mAdapter.getData();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishLoadMore();
                }
            });
        });
        mViewModel.getMessageChangeObservable().with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isChatRoomLeave() || event.type == EaseEvent.TYPE.CHAT_ROOM) {
                pageNum = 1;
                mViewModel.loadChatRooms(pageNum, PAGE_SIZE);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel.loadChatRooms(pageNum, PAGE_SIZE);
    }

    private void finishRefresh() {
        if(mSrlCommonRefresh != null) {
            mSrlCommonRefresh.finishRefresh();
        }
    }

    private void finishLoadMore() {
        if(mSrlCommonRefresh != null) {
            mSrlCommonRefresh.finishLoadMore();
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSrlCommonRefresh.setOnRefreshLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
        DemoHelper.getInstance().getChatroomManager().addChatRoomChangeListener(new ChatRoomChangeListener());
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        pageNum = 1;
        mViewModel.loadChatRooms(pageNum, PAGE_SIZE);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        pageNum += 1;
        mViewModel.setLoadMoreChatRooms(pageNum, PAGE_SIZE);
    }

    @Override
    public void onItemClick(View view, int position) {
        EMChatRoom item = mAdapter.getItem(position);
        ChatActivity.actionStart(mContext, item.getId(), DemoConstant.CHATTYPE_CHATROOM);
    }

    private void addHeaderView() {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.demo_widget_contact_item, mRvCommonList, false);
        ImageView avatar = headerView.findViewById(R.id.avatar);
        TextView name = headerView.findViewById(R.id.name);
        avatar.setImageResource(R.drawable.em_create_group);
        name.setText(R.string.em_friends_chat_room_create);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewChatRoomActivity.actionStart(mContext);
            }
        });
        mRvCommonList.addHeaderView(headerView);
    }

    private class ChatRoomChangeListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            pageNum = 1;
            mViewModel.loadChatRooms(pageNum, PAGE_SIZE);
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {

        }

        @Override
        public void onMemberJoined(String roomId, String participant) {

        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {

        }
    }
}
