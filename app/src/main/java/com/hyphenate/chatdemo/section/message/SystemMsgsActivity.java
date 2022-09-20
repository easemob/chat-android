package com.hyphenate.chatdemo.section.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.db.entity.InviteMessageStatus;
import com.hyphenate.chatdemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatdemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.chatdemo.section.message.delegates.AgreeMsgDelegate;
import com.hyphenate.chatdemo.section.message.delegates.InviteMsgDelegate;
import com.hyphenate.chatdemo.section.message.delegates.OtherMsgDelegate;
import com.hyphenate.chatdemo.section.message.viewmodels.NewFriendsViewModel;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class SystemMsgsActivity extends BaseInitActivity implements OnRefreshLoadMoreListener, InviteMsgDelegate.OnInviteListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private static final int limit = 10;
    private int offset;
    private NewFriendsMsgAdapter adapter;
    private NewFriendsViewModel viewModel;
    private InviteMsgDelegate msgDelegate;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, SystemMsgsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_system_msgs;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new NewFriendsMsgAdapter();
        msgDelegate = new InviteMsgDelegate();
        adapter.addDelegate(new AgreeMsgDelegate())
                .addDelegate(msgDelegate)
                .addDelegate(new OtherMsgDelegate());
        rvList.setAdapter(adapter);

        registerForContextMenu(rvList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.demo_invite_list_menu, menu);
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) menuInfo).position;
        EMMessage item = adapter.getItem(position);
        String statusParams = null;
        try {
            statusParams = item.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        if(statusParams == null) {
            return;
        }
        InviteMessageStatus statusEnum = InviteMessageStatus.valueOf(statusParams);
        if(statusEnum == InviteMessageStatus.BEINVITEED ||
                statusEnum == InviteMessageStatus.BEAPPLYED ||
                statusEnum == InviteMessageStatus.GROUPINVITATION) {
            menu.findItem(R.id.action_invite_agree).setVisible(true);
            menu.findItem(R.id.action_invite_refuse).setVisible(true);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo()).position;
        EMMessage message = adapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.action_invite_agree :
                viewModel.agreeInvite(message);
                break;
            case R.id.action_invite_refuse :
                viewModel.refuseInvite(message);
                break;
            case R.id.action_invite_delete :
                viewModel.deleteMsg(message);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshLoadMoreListener(this);
        msgDelegate.setOnInviteListener(this);
        titleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(NewFriendsViewModel.class);
        viewModel.inviteMsgObservable().observe(this, response -> {
            finishRefresh();
            if(response == null) {
                return;
            }
            adapter.setData(response);
        });
        viewModel.moreInviteMsgObservable().observe(this, response -> {
            finishLoadMore();
            if(response == null) {
                return;
            }
            adapter.addData(response);
        });

        viewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    viewModel.loadMessages(limit);
                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(event);
                }
            });
        });
        viewModel.agreeObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String message) {
                    viewModel.loadMessages(limit);
                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(event);
                }
            });
        });
        viewModel.refuseObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String message) {
                    viewModel.loadMessages(limit);
                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(event);
                }
            });
        });
        LiveDataBus bus = LiveDataBus.get();
        bus.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, this::loadData);
        viewModel.makeAllMsgRead();
        viewModel.loadMessages(limit);
    }

    private void loadData(EaseEvent easeEvent) {
        viewModel.loadMessages(limit);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        offset += limit;
        EMMessage message = adapter.getData().get(adapter.getData().size() - 1);
        viewModel.loadMoreMessages(message.getMsgId(), limit);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        viewModel.loadMessages(limit);
    }

    private void finishRefresh() {
        if(srlRefresh != null) {
            srlRefresh.finishRefresh();
        }
    }

    private void finishLoadMore() {
        if(srlRefresh != null) {
            srlRefresh.finishLoadMore();
        }
    }

    @Override
    public void onInviteAgree(View view, EMMessage msg) {
        viewModel.agreeInvite(msg);
    }

    @Override
    public void onInviteRefuse(View view, EMMessage msg) {
        viewModel.refuseInvite(msg);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
