package com.hyphenate.chatuidemo.section.conversation;

import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.conversation.adapter.HomeAdapter;
import com.hyphenate.chatuidemo.section.conversation.viewmodel.HomeViewModel;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class HomeFragment extends BaseInitFragment implements OnRefreshListener, View.OnClickListener {
    private EaseSearchTextView mTvSearch;
    private RecyclerView mRvHomeList;
    private SmartRefreshLayout mRefreshLayout;
    private HomeViewModel mViewModel;
    private HomeAdapter mHomeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTvSearch = findViewById(R.id.tv_search);
        mRvHomeList = findViewById(R.id.rv_home_list);
        mRefreshLayout = findViewById(R.id.srl_refresh);

        mRvHomeList.setLayoutManager(new LinearLayoutManager(mContext));
        mHomeAdapter = new HomeAdapter();
        mRvHomeList.setAdapter(mHomeAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.em_home_divider_list));
        mRvHomeList.addItemDecoration(decoration);

    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.getConversationObservable().observe(this, response -> {
            if(response == null) {
                return;
            }
            if(response.status == Status.SUCCESS) {
                finishRefresh();
                mHomeAdapter.setData(response.data);
            } else if(response.status == Status.ERROR) {
                finishRefresh();
                showToast(response.getMessage());
            }else if(response.status == Status.LOADING) {
                // do nothing
            }

        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTvSearch.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel.loadConversationList();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.loadConversationList();
    }

    private void finishRefresh() {
        if(mRefreshLayout != null) {
            ThreadManager.getInstance().runOnMainThread(()-> {
                mRefreshLayout.finishRefresh();
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search :
                showToast("跳转到搜索页面");
                break;
        }
    }
}
