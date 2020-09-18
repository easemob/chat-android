package com.hyphenate.easeim.section.contact.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.section.base.BaseInitFragment;
import com.hyphenate.easeim.section.group.activity.GroupSimpleDetailActivity;
import com.hyphenate.easeim.section.contact.adapter.PublicGroupContactAdapter;
import com.hyphenate.easeim.section.contact.viewmodels.GroupContactViewModel;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public class GroupPublicContactManageFragment extends BaseInitFragment implements OnRefreshLoadMoreListener, OnItemClickListener {
    public SmartRefreshLayout srlRefresh;
    public RecyclerView rvList;
    public PublicGroupContactAdapter mAdapter;
    private int page_size = 20;
    private String cursor;
    private GroupContactViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_group_public_contact_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(GroupContactViewModel.class);
        viewModel.getPublicGroupObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMCursorResult<EMGroupInfo>>() {
                @Override
                public void onSuccess(EMCursorResult<EMGroupInfo> data) {
                    List<EMGroupInfo> groups = data.getData();
                    cursor = data.getCursor();
                    mAdapter.setData(groups);
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

        viewModel.getMorePublicGroupObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<EMCursorResult<EMGroupInfo>>() {
                @Override
                public void onSuccess(EMCursorResult<EMGroupInfo> data) {
                    cursor = data.getCursor();
                    List<EMGroupInfo> groups = data.getData();
                    mAdapter.addData(groups);
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

    }

    @Override
    protected void initData() {
        super.initData();
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PublicGroupContactAdapter();
        rvList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        getData();
    }

    public void getData() {
        viewModel.getPublicGroups(page_size);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if(cursor != null) {
            viewModel.getMorePublicGroups(page_size, cursor);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getData();
    }

    @Override
    public void onItemClick(View view, int position) {
        EMGroupInfo item = mAdapter.getItem(position);
        GroupSimpleDetailActivity.actionStart(mContext, item.getGroupId());
    }
}
