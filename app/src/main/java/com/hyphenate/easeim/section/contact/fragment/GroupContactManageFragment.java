package com.hyphenate.easeim.section.contact.fragment;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.section.chat.activity.ChatActivity;
import com.hyphenate.easeim.section.contact.adapter.GroupContactAdapter;
import com.hyphenate.easeim.section.contact.viewmodels.GroupContactViewModel;
import com.hyphenate.easeui.model.EaseEvent;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;


public class GroupContactManageFragment extends GroupPublicContactManageFragment {
    private GroupContactViewModel mViewModel;
    private GroupContactAdapter mAdapter;
    private int pageIndex;
    private static final int PAGE_SIZE = 20;

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(mContext).get(GroupContactViewModel.class);
        mViewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EMGroup>>() {
                @Override
                public void onSuccess(List<EMGroup> data) {
                    mAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    if(srlRefresh != null) {
                        srlRefresh.finishRefresh();
                    }
                }
            });
        });
        mViewModel.getMoreGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EMGroup>>() {
                @Override
                public void onSuccess(List<EMGroup> data) {
                    if(data != null) {
                        mAdapter.addData(data);
                    }
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    if(srlRefresh != null) {
                        srlRefresh.finishLoadMore();
                    }
                }
            });
        });
        mViewModel.getMessageObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isGroupChange() || event.isGroupLeave()) {
                getData();
            }
        });
    }

    @Override
    protected void initData() {
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new GroupContactAdapter();
        rvList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        getData();
    }

    @Override
    public void getData() {
        pageIndex = 0;
        mViewModel.loadGroupListFromServer(pageIndex, PAGE_SIZE);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        pageIndex += PAGE_SIZE;
        mViewModel.loadMoreGroupListFromServer(pageIndex, PAGE_SIZE);
    }

    @Override
    public void onItemClick(View view, int position) {
        //跳转到群聊页面
        EMGroup group = mAdapter.getItem(position);
        ChatActivity.actionStart(mContext, group.getGroupId(), DemoConstant.CHATTYPE_GROUP);
    }
}
