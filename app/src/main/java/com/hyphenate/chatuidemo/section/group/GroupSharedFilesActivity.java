package com.hyphenate.chatuidemo.section.group;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.group.adapter.SharedFilesAdapter;
import com.hyphenate.chatuidemo.section.group.viewmodels.SharedFilesViewModel;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

public class GroupSharedFilesActivity extends BaseInitActivity implements OnRefreshListener, OnItemClickListener, OnRefreshLoadMoreListener {
    private EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private SharedFilesAdapter adapter;
    private SharedFilesViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_chat_group_shared_files;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new SharedFilesAdapter();
        rvList.setAdapter(adapter);
        rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        registerForContextMenu(rvList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.em_group_shared_files_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        EaseRecyclerView.RecyclerViewContextMenuInfo info = (EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case R.id.action_shared_delete :
                deleteFile(adapter.getItem(position));
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshLoadMoreListener(this);
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(SharedFilesViewModel.class);
        viewModel.getFilesObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EMMucSharedFile>>() {
                @Override
                public void onSuccess(List<EMMucSharedFile> data) {
                    adapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                    finishLoadMore();
                }
            });
        });
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {

    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

    }

    @Override
    public void onItemClick(View view, int position) {
        showFile(adapter.getItem(position));
    }

    private void deleteFile(EMMucSharedFile file) {

    }

    private void showFile(EMMucSharedFile item) {

    }

    private void finishRefresh() {
        if(srlRefresh != null) {
            runOnUiThread(() -> srlRefresh.finishRefresh());
        }
    }

    private void finishLoadMore() {
        if(srlRefresh != null) {
            runOnUiThread(()-> srlRefresh.finishLoadMore());
        }
    }
}
