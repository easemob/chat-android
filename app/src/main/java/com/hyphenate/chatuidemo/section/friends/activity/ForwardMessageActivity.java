package com.hyphenate.chatuidemo.section.friends.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.DemoApp;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.manager.PushAndMessageHelper;
import com.hyphenate.chatuidemo.section.base.BaseDialogFragment;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.ChatActivity;
import com.hyphenate.chatuidemo.section.chat.adapter.PickUserAdapter;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.chatuidemo.section.friends.viewmodels.ContactListViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ForwardMessageActivity extends BaseInitActivity implements OnRefreshListener, OnItemClickListener, EaseSidebar.OnTouchEventListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar mEaseTitleBar;
    private SmartRefreshLayout mSrlRefresh;
    private RecyclerView mRvContactList;
    private EaseSidebar mEaseSidebar;
    private TextView mFloatingHeader;
    private PickUserAdapter mAdapter;
    private ContactListViewModel mViewModel;
    private String mForwardMsgId;

    public static void actionStart(Context context, String forward_msg_id) {
        Intent starter = new Intent(context, ForwardMessageActivity.class);
        starter.putExtra("forward_msg_id", forward_msg_id);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_contact_list;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mForwardMsgId = getIntent().getStringExtra("forward_msg_id");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mSrlRefresh = findViewById(R.id.srl_refresh);
        mRvContactList = findViewById(R.id.rv_contact_list);
        mEaseSidebar = findViewById(R.id.side_bar_pick_user);
        mFloatingHeader = findViewById(R.id.floating_header);
        mEaseTitleBar = findViewById(R.id.title_bar_contact_list);

        mRvContactList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PickUserAdapter();
        mRvContactList.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSrlRefresh.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mEaseSidebar.setOnTouchEventListener(this);
        mEaseTitleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(ContactListViewModel.class);
        mViewModel.getContactListObservable().observe(this, response -> {
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
        mViewModel.getContactList();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.getContactList();
    }

    @Override
    public void onItemClick(View view, int position) {
        EaseUser user = mAdapter.getData().get(position);
        SimpleDialogFragment.showDialog(mContext, getString(R.string.confirm_forward_to, user.getNickname()), new DemoDialogFragment.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(View view) {
                PushAndMessageHelper.sendForwardMessage(user.getUsername(), mForwardMsgId);
                finish();
            }
        });
    }

    private void finishChatActivity() {
        DemoApp.getInstance().getActivityLifecycle().finishTarget(ChatActivity.class);
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

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    private void moveToRecyclerItem(String pointer) {
        List<EaseUser> data = mAdapter.getData();
        if(data == null || data.isEmpty()) {
            return;
        }
        for(int i = 0; i < data.size(); i++) {
            if(TextUtils.equals(EaseCommonUtils.getLetter(data.get(i).getNickname()), pointer)) {
                LinearLayoutManager manager = (LinearLayoutManager) mRvContactList.getLayoutManager();
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

    private void finishRefresh() {
        if(mSrlRefresh != null) {
            mContext.runOnUiThread(() -> {
                mSrlRefresh.finishRefresh();
            });
        }
    }
}
