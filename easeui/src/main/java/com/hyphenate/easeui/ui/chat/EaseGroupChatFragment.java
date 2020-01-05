package com.hyphenate.easeui.ui.chat;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;

public class EaseGroupChatFragment extends EaseChatFragment {

    @Override
    protected void initChildArguments() {
        super.initChildArguments();
        chatType = EaseConstant.CHATTYPE_GROUP;
        emMsgChatType = EMMessage.ChatType.GroupChat;
    }
}
