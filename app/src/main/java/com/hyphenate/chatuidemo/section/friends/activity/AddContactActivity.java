package com.hyphenate.chatuidemo.section.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.enums.SearchType;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.friends.adapter.AddContactAdapter;
import com.hyphenate.chatuidemo.section.friends.viewmodels.AddContactViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class AddContactActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, SearchView.OnQueryTextListener, OnRefreshListener, AddContactAdapter.OnItemAddClickListener, OnItemClickListener {
    private EaseTitleBar mTitleBar;
    private SearchView mSearchView;
    private SmartRefreshLayout mSrlSearch;
    private RecyclerView mRvSearchList;
    private AddContactAdapter mAdapter;
    private AddContactViewModel mViewModel;
    private SearchType mType;

    public static void startAction(Context context, SearchType type) {
        Intent intent = new Intent(context, AddContactActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mType = (SearchType) getIntent().getSerializableExtra("type");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_friends_add_contact;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.em_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        // 搜索框直接展开显示，输入内容后有清空图标
        mSearchView.onActionViewExpanded();
        mSearchView.setMaxWidth((int) EaseCommonUtils.getScreenInfo(mContext)[0]);
        // 去除默认的下面横线
        mSearchView.findViewById(R.id.search_plate).setBackground(null);
        mSearchView.findViewById(R.id.submit_area).setBackground(null);
        mSearchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = findViewById(R.id.title_bar_search);
        mSrlSearch = findViewById(R.id.srl_search);
        mRvSearchList = findViewById(R.id.rv_search_list);

        mRvSearchList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AddContactAdapter();
        mRvSearchList.setAdapter(mAdapter);
        mRvSearchList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTitleBar.setOnBackPressListener(this);
        mSrlSearch.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemAddClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(mContext).get(AddContactViewModel.class);
        mViewModel.getAddContact().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(getResources().getString(R.string.em_add_contact_send_successful));
                }
            });

        });
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        // you can search the user from your app server here.
        if(!TextUtils.isEmpty(query)) {
            if(mAdapter.getData() == null || mAdapter.getData().isEmpty()) {
                mAdapter.addData(query);
            }else {
                mAdapter.clearData();
                mAdapter.addData(query);
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText)) {
            mAdapter.clearData();
        }
        return false;
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
    }

    @Override
    public void onItemAddClick(View view, int position) {
        // 添加好友
        mViewModel.addContact(mAdapter.getItem(position), getResources().getString(R.string.em_add_contact_add_a_friend));
    }

    @Override
    public void onItemClick(View view, int position) {
        // 跳转到好友页面
        String item = mAdapter.getItem(position);
        EaseUser user = new EaseUser(item);
        ContactDetailActivity.actionStart(mContext, user, false);
    }
}
