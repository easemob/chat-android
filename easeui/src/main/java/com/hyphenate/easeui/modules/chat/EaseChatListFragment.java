package com.hyphenate.easeui.modules.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;

public class EaseChatListFragment extends EaseBaseFragment {
    private EaseChatMessageListLayout layoutMessage;
    private String username;
    private int chatType;

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
        layoutMessage = findViewById(R.id.layout_message);
        layoutMessage.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
    }

    public void initListener() {

    }

    public void initData() {
        layoutMessage.init(EaseChatMessageListLayout.LoadDataType.LOCAL, username, chatType);
        layoutMessage.loadDefaultData(null);
    }
}

