package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.manager.SidebarPresenter;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.friends.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.group.adapter.GroupMemberAuthorityAdapter;
import com.hyphenate.chatuidemo.section.group.viewmodels.GroupMemberAuthorityViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class GroupMemberAuthorityActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, OnItemClickListener, OnRefreshListener {
    private EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private EaseSidebar sidebar;
    private TextView floatingHeader;
    private SidebarPresenter presenter;
    protected GroupMemberAuthorityAdapter adapter;
    protected GroupMemberAuthorityViewModel viewModel;
    protected String groupId;

    public static void start(Context context, String groupId) {
        Intent starter = new Intent(context, GroupMemberAuthorityActivity.class);
        starter.putExtra("groupId", groupId);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_group_member_authority;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);
        sidebar = findViewById(R.id.sidebar);
        floatingHeader = findViewById(R.id.floating_header);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new GroupMemberAuthorityAdapter();
        rvList.setAdapter(adapter);

        presenter = new SidebarPresenter();
        presenter.setupWithRecyclerView(rvList, floatingHeader);

        registerForContextMenu(rvList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.em_group_member_authority_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        return super.onContextItemSelected(item);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        adapter.setOnItemClickListener(this);
        sidebar.setOnTouchEventListener(presenter);
        srlRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(GroupMemberAuthorityViewModel.class);
        getData();
    }

    public void getData() {
        viewModel.getMemberObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    adapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });
        viewModel.getMembers(groupId);
    }

    public void finishRefresh() {
        if(srlRefresh != null) {
            runOnUiThread(()-> srlRefresh.finishRefresh());
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        viewModel.getMembers(groupId);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {
        //跳转到
        ContactDetailActivity.actionStart(mContext, adapter.getItem(position));
    }
}
