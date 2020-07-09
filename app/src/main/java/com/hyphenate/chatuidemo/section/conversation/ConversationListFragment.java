package com.hyphenate.chatuidemo.section.conversation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.chatuidemo.section.chat.ChatActivity;
import com.hyphenate.chatuidemo.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.chatuidemo.section.conversation.delegate.SystemMessageDelegate;
import com.hyphenate.chatuidemo.section.conversation.viewmodel.ConversationListViewModel;
import com.hyphenate.chatuidemo.section.message.NewFriendsMsgActivity;
import com.hyphenate.chatuidemo.section.search.SearchConversationActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseSearchTextView;

import java.util.List;


public class ConversationListFragment extends EaseConversationListFragment implements View.OnClickListener {
    private EaseSearchTextView tvSearch;

    private ConversationListViewModel mViewModel;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        //添加搜索会话布局
        viewStub.setLayoutResource(R.layout.demo_layout_search);
        View view = viewStub.inflate();
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        tvSearch = view.findViewById(R.id.tv_search);

        initViewModel();
    }

    @Override
    public void addDelegate() {
        super.addDelegate();
        listAdapter.addDelegate(new SystemMessageDelegate());
    }

    @Override
    public void onChildCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, Object item) {
        super.onChildCreateContextMenu(menu, v, menuInfo, item);
        if(item instanceof MsgTypeManageEntity) {
            String ext = ((MsgTypeManageEntity) item).getExtField();
            if(!TextUtils.isEmpty(ext) && EaseCommonUtils.isTimestamp(ext)) {
                // 含有时间戳
                menu.findItem(R.id.action_cancel_top).setVisible(true);
                menu.findItem(R.id.action_make_top).setVisible(false);
            }
        }
    }

    @Override
    public void onChildContextItemSelected(MenuItem menuItem, Object object) {
        super.onChildContextItemSelected(menuItem, object);
        if(object instanceof MsgTypeManageEntity) {
            MsgTypeManageEntity msg = (MsgTypeManageEntity) object;
            switch (menuItem.getItemId()) {
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

    @Override
    public void initListener() {
        super.initListener();
        tvSearch.setOnClickListener(this);
    }

    @Override
    public void refreshList() {
        mViewModel.loadConversationList();
    }

    @Override
    public void makeConversationRead(EMConversation conversation) {
        mViewModel.makeConversationRead(conversation.conversationId());
    }

    @Override
    public void deleteConversation(String conversationId) {
        mViewModel.deleteConversationById(conversationId);
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
        mViewModel.getConversationObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<Object>>() {
                @Override
                public void onSuccess(List<Object> data) {
                    listAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });

        });

        mViewModel.getDeleteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadConversationList();
                }
            });
        });

        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        LiveDataBus messageChange = messageViewModel.getMessageChange();
        messageChange.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), this::refreshList);
        messageChange.with(DemoConstant.MESSAGE_NOT_SEND, Boolean.class).observe(getViewLifecycleOwner(), this::refreshList);
    }

    private void refreshList(Boolean event) {
        if(event == null) {
            return;
        }
        if(event) {
            mViewModel.loadConversationList();
        }
    }

    private void loadList(EaseEvent change) {
        if(change == null) {
            return;
        }
        if(change.isMessageChange() || change.isNotifyChange()
                || change.isGroupLeave() || change.isChatRoomLeave()
                || change.isContactChange()
                || change.type == EaseEvent.TYPE.CHAT_ROOM || change.isGroupChange()) {
            mViewModel.loadConversationList();
        }
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if(response == null) {
            return;
        }
        if(response.status == Status.SUCCESS) {
            callback.hideLoading();
            callback.onSuccess(response.data);
        }else if(response.status == Status.ERROR) {
            callback.hideLoading();
            if(!callback.hideErrorMsg) {
                showToast(response.getMessage());
            }
            callback.onError(response.errorCode, response.getMessage());
        }else if(response.status == Status.LOADING) {
            callback.onLoading();
        }
    }

    /**
     * toast by string
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showToast(message);
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
        super.onItemClick(view, position);
        Object item = listAdapter.getItem(position);
        if(item instanceof EMConversation) {
            ChatActivity.actionStart(mContext, ((EMConversation)item).conversationId(), EaseCommonUtils.getChatType((EMConversation) item));
        }else if(item instanceof MsgTypeManageEntity) {
            NewFriendsMsgActivity.actionStart(mContext);
        }
    }
}
