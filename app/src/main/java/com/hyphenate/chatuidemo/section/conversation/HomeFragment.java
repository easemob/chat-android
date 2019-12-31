package com.hyphenate.chatuidemo.section.conversation;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.conversation.adapter.HomeAdapter;
import com.hyphenate.chatuidemo.section.conversation.viewmodel.HomeViewModel;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class HomeFragment extends BaseInitFragment implements OnRefreshListener, View.OnClickListener, OnItemClickListener {
    private EaseSearchTextView mTvSearch;
    private EaseRecyclerView mRvHomeList;
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

        // 注册快捷菜单
        registerForContextMenu(mRvHomeList);

        mRvHomeList.setLayoutManager(new LinearLayoutManager(mContext));
        mHomeAdapter = new HomeAdapter();
        mRvHomeList.setAdapter(mHomeAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.em_home_divider_list));
        mRvHomeList.addItemDecoration(decoration);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.em_conversation_list_menu, menu);
        if(menuInfo instanceof EaseRecyclerView.RecyclerViewContextMenuInfo) {
            int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) menuInfo).position;
            EMConversation item = mHomeAdapter.getItem(position);
            String extField = item.getExtField();
            if(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                // 含有时间戳
                menu.findItem(R.id.action_cancel_top).setVisible(true);
                menu.findItem(R.id.action_make_top).setVisible(false);
            }
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        EaseRecyclerView.RecyclerViewContextMenuInfo info = (EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        EMConversation conversation = null;
        if(position >= 0) {
            conversation = mHomeAdapter.getItem(position);
        }
        if(conversation != null) {
            switch (item.getItemId()) {
                case R.id.action_make_top :
                    conversation.setExtField(System.currentTimeMillis()+"");
                    mViewModel.loadConversationList();
                    break;
                case R.id.action_cancel_top:
                    conversation.setExtField("");
                    mViewModel.loadConversationList();
                    break;
                case R.id.action_delete:
                    mViewModel.deleteConversationById(conversation.conversationId());
                    break;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.getConversationObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EMConversation>>() {
                @Override
                public void onSuccess(List<EMConversation> data) {
                    mHomeAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });

        });

        mViewModel.getDeleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadConversationList();
                }
            });
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTvSearch.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mHomeAdapter.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(View view, int position) {
        EMConversation item = mHomeAdapter.getItem(position);
    }
}
