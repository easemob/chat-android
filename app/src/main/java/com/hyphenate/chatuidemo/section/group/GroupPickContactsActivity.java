package com.hyphenate.chatuidemo.section.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.manager.SidebarPresenter;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.group.adapter.GroupPickContactsAdapter;
import com.hyphenate.chatuidemo.section.group.viewmodels.GroupPickContactsViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupPickContactsActivity extends BaseInitActivity implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener, OnRefreshListener {
    private EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private RecyclerView rvList;
    private EaseSidebar sidebar;
    private TextView floatingHeader;
    private SidebarPresenter presenter;
    private GroupPickContactsAdapter adapter;
    private GroupPickContactsViewModel viewModel;
    private String groupId;
    private boolean isOwner;

    public static void actionStartForResult(Activity context, int requestCode) {
        Intent starter = new Intent(context, GroupPickContactsActivity.class);
        context.startActivityForResult(starter, requestCode);
    }

    public static void actionStartForResult(Activity context, String groupId, boolean owner, int requestCode) {
        Intent starter = new Intent(context, GroupPickContactsActivity.class);
        starter.putExtra("groupId", groupId);
        starter.putExtra("isOwner", owner);
        context.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat_group_pick_contacts;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
        isOwner = intent.getBooleanExtra("isOwner", false);
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
        adapter = new GroupPickContactsAdapter();
        rvList.setAdapter(adapter);

        presenter = new SidebarPresenter();
        presenter.setupWithRecyclerView(rvList, floatingHeader);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
        srlRefresh.setOnRefreshListener(this);
        sidebar.setOnTouchEventListener(presenter);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(GroupPickContactsViewModel.class);
        viewModel.getContacts().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    adapter.setData(data);
                    if(!TextUtils.isEmpty(groupId)) {
                        viewModel.getGroupMembers(groupId);
                    }
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });
        viewModel.getGroupMembersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    adapter.setExistMember(data);
                }
            });
        });
        viewModel.getAddMembersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        });
        viewModel.getAllContacts();
    }

    private void finishRefresh() {
        if(srlRefresh != null) {
            runOnUiThread(() -> srlRefresh.finishRefresh());
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        viewModel.getAllContacts();
    }

    @Override
    public void onRightClick(View view) {
        List<String> selectedMembers = adapter.getSelectedMembers();
        if(selectedMembers == null || selectedMembers.isEmpty()) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        String[] newMembers = selectedMembers.toArray(new String[0]);
        if(TextUtils.isEmpty(groupId)) {
            Intent intent = getIntent().putExtra("newmembers", newMembers);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        viewModel.addGroupMembers(isOwner, groupId, newMembers);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
