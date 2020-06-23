package com.hyphenate.chatuidemo.section.conversation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.chat.ChatActivity;
import com.hyphenate.chatuidemo.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.chatuidemo.section.conversation.adapter.HomeAdapter;
import com.hyphenate.chatuidemo.section.conversation.viewmodel.HomeViewModel;
import com.hyphenate.chatuidemo.section.message.NewFriendsMsgActivity;
import com.hyphenate.chatuidemo.section.search.SearchActivity;
import com.hyphenate.chatuidemo.section.search.SearchConversationActivity;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSearchTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class ConversationListFragment extends BaseInitFragment implements OnRefreshListener, View.OnClickListener, OnItemClickListener {
    private EaseSearchTextView mTvSearch;
    private EaseRecyclerView mRvHomeList;
    private SmartRefreshLayout mRefreshLayout;
    private HomeViewModel mViewModel;
    private HomeAdapter mHomeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_home;
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
        decoration.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.demo_home_divider_list));
        mRvHomeList.addItemDecoration(decoration);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.demo_conversation_list_menu, menu);
        if(menuInfo instanceof EaseRecyclerView.RecyclerViewContextMenuInfo) {
            int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) menuInfo).position;
            Object item = mHomeAdapter.getItem(position);
            if(item instanceof EMConversation) {
                String extField = ((EMConversation)item).getExtField();
                if(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                    // 含有时间戳
                    menu.findItem(R.id.action_cancel_top).setVisible(true);
                    menu.findItem(R.id.action_make_top).setVisible(false);
                }
            }else if(item instanceof MsgTypeManageEntity) {
                String ext = ((MsgTypeManageEntity) item).getExtField();
                if(!TextUtils.isEmpty(ext) && EaseCommonUtils.isTimestamp(ext)) {
                    // 含有时间戳
                    menu.findItem(R.id.action_cancel_top).setVisible(true);
                    menu.findItem(R.id.action_make_top).setVisible(false);
                }
            }

        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        EaseRecyclerView.RecyclerViewContextMenuInfo info = (EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Object object = null;
        if(position >= 0) {
            object = mHomeAdapter.getItem(position);
        }
        if(object != null) {
            if(object instanceof EMConversation) {
                EMConversation conversation = (EMConversation) object;
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
            }else if(object instanceof MsgTypeManageEntity) {
                MsgTypeManageEntity msg = (MsgTypeManageEntity) object;
                switch (item.getItemId()) {
                    case R.id.action_make_top :
                        msg.setExtField(System.currentTimeMillis()+"");
                        DemoHelper.getInstance().update(msg);
                        mViewModel.loadConversationList();
                        break;
                    case R.id.action_cancel_top:
                        msg.setExtField("");
                        DemoHelper.getInstance().update(msg);
                        mViewModel.loadConversationList();
                        break;
                    case R.id.action_delete:
                        mViewModel.deleteSystemMsg(msg);
                        break;
                }
            }

        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.getConversationObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<Object>>() {
                @Override
                public void onSuccess(List<Object> data) {
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

        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.getMessageChange().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::loadList);
        messageViewModel.getMessageChange().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::loadList);
        messageViewModel.getMessageChange().with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, this::loadList);
        messageViewModel.getMessageChange().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(this, this::loadList);
    }

    private void loadList(EaseEvent change) {
        if(change == null) {
            return;
        }
        if(change.isMessageChange() || change.isNotifyChange()
                || change.isGroupLeave() || change.isChatRoomLeave()
                || change.type == EaseEvent.TYPE.CHAT_ROOM || change.isGroupChange()) {
            mViewModel.loadConversationList();
        }
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
                SearchConversationActivity.actionStart(mContext);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Object item = mHomeAdapter.getItem(position);
        if(item instanceof EMConversation) {
            ChatActivity.actionStart(mContext, ((EMConversation)item).conversationId(), EaseCommonUtils.getChatType((EMConversation) item));
        }else if(item instanceof MsgTypeManageEntity) {
            NewFriendsMsgActivity.actionStart(mContext);
        }
    }
}
