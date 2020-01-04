package com.hyphenate.easeui.ui.chat;

import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.constants.EaseConstant;

import androidx.recyclerview.widget.RecyclerView;

public class EaseSingleChatFragment extends EaseChatFragment {

    @Override
    protected void initChildArguments() {
        super.initChildArguments();
        chatType = EaseConstant.CHATTYPE_SINGLE;
    }

    @Override
    protected void initChildData() {
        super.initChildData();
        ((EaseMessageAdapter)messageAdapter).showUserNick(false);
    }
}
