package com.hyphenate.chatuidemo.section.chat.fragment;

import com.hyphenate.easeui.interfaces.IChatAdapterProvider;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.ui.EaseViewHolderProvider;
import com.hyphenate.easeui.ui.chat.EaseChatFragment;

public class ChatFragment extends EaseChatFragment {


    @Override
    public IViewHolderProvider setViewHolderProvider() {
        return new EaseViewHolderProvider();
    }

    @Override
    protected IChatAdapterProvider setChatAdapterProvider() {
        return super.setChatAdapterProvider();
    }
}
