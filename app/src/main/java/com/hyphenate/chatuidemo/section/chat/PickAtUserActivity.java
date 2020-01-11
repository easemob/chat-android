package com.hyphenate.chatuidemo.section.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.adapter.PickUserAdapter;
import com.hyphenate.chatuidemo.section.friends.viewmodels.GroupContactViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PickAtUserActivity extends BaseInitActivity implements OnRefreshListener, OnItemClickListener {
    private EaseTitleBar mTitleBarPick;
    private SmartRefreshLayout mSrlRefresh;
    private EaseRecyclerView mRvPickUserList;
    private EaseSidebar mSideBarPickUser;
    private String mGroupId;
    private GroupContactViewModel mViewModel;
    private PickUserAdapter mAdapter;

    public static void actionStartForResult(Fragment fragment, String groupId, int requestCode) {
        Intent starter = new Intent(fragment.getContext(), PickAtUserActivity.class);
        starter.putExtra("groupId", groupId);
        fragment.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_chat_pick_at_user;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mGroupId = getIntent().getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBarPick = findViewById(R.id.title_bar_pick);
        mSrlRefresh = findViewById(R.id.srl_refresh);
        mRvPickUserList = findViewById(R.id.rv_pick_user_list);
        mSideBarPickUser = findViewById(R.id.side_bar_pick_user);

        mRvPickUserList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PickUserAdapter();
        mRvPickUserList.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSrlRefresh.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(GroupContactViewModel.class);
        mViewModel.getGroupMember().observe(this, response -> {
            checkIfAddHeader();
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    mAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });

        mViewModel.getGroupMembers(mGroupId);
    }

    private void checkIfAddHeader() {
        String owner = DemoHelper.getInstance().getGroupManager().getGroup(mGroupId).getOwner();
        if(TextUtils.equals(owner, DemoHelper.getInstance().getCurrentUser())) {
            AddHeader();
        }
    }

    private void AddHeader() {
        View view = LayoutInflater.from(this).inflate(R.layout.em_widget_contact_item, mRvPickUserList, false);
        ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
        TextView textView = (TextView) view.findViewById(R.id.name);
        textView.setText(getString(R.string.all_members));
        avatarView.setImageResource(R.drawable.ease_groups_icon);
        mRvPickUserList.addHeaderView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("username", getString(R.string.all_members)));
            }
        });

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.getGroupMembers(mGroupId);
    }

    private void finishRefresh() {
        if(mSrlRefresh != null) {
            ThreadManager.getInstance().runOnMainThread(() -> {
                mSrlRefresh.finishRefresh();
            });

        }
    }

    @Override
    public void onItemClick(View view, int position) {
        EaseUser user = mAdapter.getData().get(position);
        if(TextUtils.equals(user.getUsername(), DemoHelper.getInstance().getCurrentUser())) {
            return;
        }
        Intent intent = getIntent();
        intent.putExtra("username", user.getUsername());
        setResult(RESULT_OK, intent);
    }
}
