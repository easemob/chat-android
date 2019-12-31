package com.hyphenate.chatuidemo.section.friends.fragment;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.friends.adapter.ChatRoomContactAdapter;
import com.hyphenate.chatuidemo.section.friends.viewmodels.ChatRoomContactViewModel;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public class ChatRoomContactManageFragment extends BaseInitFragment implements OnRefreshLoadMoreListener {
    private int pageNum = 1;
    private static final int PAGE_SIZE = 50;
    private SmartRefreshLayout mSrlCommonRefresh;
    private EaseRecyclerView mRvCommonList;
    private ChatRoomContactAdapter mAdapter;
    private ChatRoomContactViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_common_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mSrlCommonRefresh = findViewById(R.id.srl_common_refresh);
        mRvCommonList = findViewById(R.id.rv_common_list);

        mRvCommonList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ChatRoomContactAdapter();
        mRvCommonList.setAdapter(mAdapter);
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
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishLoadMore();
                }
            });
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
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        pageNum = 1;
        mViewModel.loadChatRooms(pageNum, PAGE_SIZE);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        pageNum++;
        mViewModel.setLoadMoreChatRooms(pageNum, PAGE_SIZE);
    }
}
