package com.hyphenate.easeim.section.conversation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.chat.activity.ChatActivity;
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeim.section.conversation.viewmodel.ConversationListViewModel;
import com.hyphenate.easeim.section.dialog.DemoDialogFragment;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.message.SystemMsgsActivity;
import com.hyphenate.easeim.section.search.SearchConversationActivity;
import com.hyphenate.easeui.manager.EaseProviderManager;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;
import com.hyphenate.easeui.modules.conversation.EaseConversationListLayout;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.provider.EaseConversationInfoProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseSearchTextView;



public class ConversationListFragment extends EaseConversationListFragment implements View.OnClickListener {
    private EaseSearchTextView tvSearch;

    private ConversationListViewModel mViewModel;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        //添加搜索会话布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_layout_search, null);
        llRoot.addView(view, 0);
        tvSearch = view.findViewById(R.id.tv_search);

        initViewModel();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EaseConversationInfo info = conversationListLayout.getItem(position);
        Object object = info.getInfo();

        if(object instanceof EMConversation) {
            switch (item.getItemId()) {
                case EaseConversationListLayout.MENU_MAKE_TOP :
                    conversationListLayout.makeConversationTop(position, info);
                    return true;
                case EaseConversationListLayout.MENU_MAKE_CANCEL_TOP :
                    conversationListLayout.cancelConversationTop(position, info);
                    return true;
                case EaseConversationListLayout.MENU_DELETE :
                    showDeleteDialog(position, info);
                    return true;
            }
        }
        return super.onMenuItemClick(item, position);
    }

    private void showDeleteDialog(int position, EaseConversationInfo info) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.delete_conversation)
                .setOnConfirmClickListener(R.string.delete, new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        conversationListLayout.deleteConversation(position, info);
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void initListener() {
        super.initListener();
        tvSearch.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        EaseProviderManager.getInstance().setConversationInfoProvider(new EaseConversationInfoProvider() {
            @Override
            public String getDefaultTypeAvatar(String type) {
                if(TextUtils.equals(type, EMConversation.EMConversationType.Chat.name())) {
                    return "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605085753048&di=154d47eff01205a62cb779f8588eeb43&imgtype=0&src=http%3A%2F%2Fb.hiphotos.baidu.com%2Fzhidao%2Fwh%253D450%252C600%2Fsign%3Da587b23df11f3a295a9dddcaac159007%2F500fd9f9d72a60590cfef2f92934349b023bba62.jpg";
                }
                return null;
            }

            @Override
            public int getDefaultTypeAvatarResource(String type) {
                if(TextUtils.equals(type, EMConversation.EMConversationType.GroupChat.name())) {
                    return com.hyphenate.easeui.R.drawable.ease_default_image;
                }
                return 0;
            }

            @Override
            public String getConversationName(String username) {
                if(TextUtils.equals(username, "ljn")) {
                    return "威武";
                }
                return null;
            }
        });
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);

        mViewModel.getDeleteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                    mViewModel.loadConversationList();
                }
            });
        });

        mViewModel.getReadObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                    conversationListLayout.loadDefaultData();
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
            conversationListLayout.loadDefaultData();
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
            conversationListLayout.loadDefaultData();
        }
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if(mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).parseResource(response, callback);
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
        Object item = conversationListLayout.getItem(position).getInfo();
        if(item instanceof EMConversation) {
            if(EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item)) {
                SystemMsgsActivity.actionStart(mContext);
            }else {
                ChatActivity.actionStart(mContext, ((EMConversation)item).conversationId(), EaseCommonUtils.getChatType((EMConversation) item));
            }
        }
    }
}
