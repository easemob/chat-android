package com.hyphenate.easeui.modules.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.interfaces.OnChatLayoutListener;
import com.hyphenate.easeui.modules.chat.presenter.EaseChatMessagePresenterImpl;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;

public class EaseChatListFragment extends EaseBaseFragment {
    public EaseChatLayout layoutMessage;
    private String username;
    private int chatType;
    private OnChatLayoutListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return LayoutInflater.from(container.getContext()).inflate(getLayoutId(), null);
    }

    private int getLayoutId() {
        return R.layout.ease_fragment_chat_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initArguments() {
        username = getArguments().getString("username");
        chatType = getArguments().getInt("chatType", 1);
    }

    public void initView() {
        layoutMessage = findViewById(R.id.layout_chat);
        layoutMessage.getChatMessageListLayout().setItemShowType(EaseChatMessageListLayout.ShowType.NORMAL);
        layoutMessage.getChatMessageListLayout().setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
    }

    public void initListener() {
        layoutMessage.setOnChatLayoutListener(listener);
    }

    public void initData() {
        layoutMessage.init(username, chatType);
        layoutMessage.loadDefaultData();
    }

    public void setOnChatLayoutListener(OnChatLayoutListener listener) {
        this.listener = listener;
    }
}

