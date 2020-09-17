package com.hyphenate.chatuidemo.section.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.adapter.PickUserAdapter;
import com.hyphenate.chatuidemo.section.contact.viewmodels.GroupContactViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Iterator;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PickAtUserActivity extends BaseInitActivity implements OnRefreshListener, OnItemClickListener, EaseSidebar.OnTouchEventListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar mTitleBarPick;
    private SmartRefreshLayout mSrlRefresh;
    private EaseRecyclerView mRvPickUserList;
    private EaseSidebar mSideBarPickUser;
    private TextView mFloatingHeader;
    private String mGroupId;
    private GroupContactViewModel mViewModel;
    protected PickUserAdapter mAdapter;

    public static void actionStartForResult(Fragment fragment, String groupId, int requestCode) {
        Intent starter = new Intent(fragment.getContext(), PickAtUserActivity.class);
        starter.putExtra("groupId", groupId);
        fragment.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat_pick_at_user;
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
        mFloatingHeader = findViewById(R.id.floating_header);

        mRvPickUserList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PickUserAdapter();
        mRvPickUserList.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSrlRefresh.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mSideBarPickUser.setOnTouchEventListener(this);
        mTitleBarPick.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(GroupContactViewModel.class);
        mViewModel.getGroupMember().observe(this, response -> {
            if(response != null) {
                checkIfAddHeader();
            }
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    removeSelf(data);
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

    private void removeSelf(List<EaseUser> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        Iterator<EaseUser> iterator = data.iterator();
        while (iterator.hasNext()) {
            EaseUser user = iterator.next();
            if(TextUtils.equals(user.getUsername(), DemoHelper.getInstance().getCurrentUser())) {
                iterator.remove();
            }
        }
    }

    private void checkIfAddHeader() {
        EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(mGroupId);
        if(group != null) {
            String owner = group.getOwner();
            if(TextUtils.equals(owner, DemoHelper.getInstance().getCurrentUser())) {
                AddHeader();
            }
        }

    }

    private void AddHeader() {
        View view = LayoutInflater.from(this).inflate(R.layout.demo_widget_contact_item, mRvPickUserList, false);
        ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
        TextView textView = (TextView) view.findViewById(R.id.name);
        textView.setText(getString(R.string.all_members));
        avatarView.setImageResource(R.drawable.ease_groups_icon);
        mRvPickUserList.removeHeaderViews();
        mRvPickUserList.addHeaderView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("username", getString(R.string.all_members)));
                finish();
            }
        });

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.getGroupMembers(mGroupId);
    }

    private void finishRefresh() {
        if(mSrlRefresh != null) {
            EaseThreadManager.getInstance().runOnMainThread(() -> {
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
        finish();
    }

    @Override
    public void onActionDown(MotionEvent event, String pointer) {
        showFloatingHeader(pointer);
        moveToRecyclerItem(pointer);
    }

    @Override
    public void onActionMove(MotionEvent event, String pointer) {
        showFloatingHeader(pointer);
        moveToRecyclerItem(pointer);
    }

    @Override
    public void onActionUp(MotionEvent event) {
        hideFloatingHeader();
    }

    private void moveToRecyclerItem(String pointer) {
        List<EaseUser> data = mAdapter.getData();
        if(data == null || data.isEmpty()) {
            return;
        }
        for(int i = 0; i < data.size(); i++) {
            if(TextUtils.equals(EaseCommonUtils.getLetter(data.get(i).getNickname()), pointer)) {
                LinearLayoutManager manager = (LinearLayoutManager) mRvPickUserList.getLayoutManager();
                if(manager != null) {
                    manager.scrollToPositionWithOffset(i, 0);
                }
            }
        }
    }

    /**
     * 展示滑动的字符
     * @param pointer
     */
    private void showFloatingHeader(String pointer) {
        if(TextUtils.isEmpty(pointer)) {
            hideFloatingHeader();
            return;
        }
        mFloatingHeader.setText(pointer);
        mFloatingHeader.setVisibility(View.VISIBLE);
    }

    private void hideFloatingHeader() {
        mFloatingHeader.setVisibility(View.GONE);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
