package com.hyphenate.easeui.ui.chat;

import android.text.Editable;
import android.text.TextWatcher;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.EaseGroupListener;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.EMLog;

public class EaseGroupChatFragment extends EaseChatFragment implements TextWatcher {
    private static final String TAG = EaseGroupChatFragment.class.getSimpleName();
    private GroupListener listener;

    @Override
    protected void initChildArguments() {
        super.initChildArguments();
        chatType = EaseConstant.CHATTYPE_GROUP;
        emMsgChatType = EMMessage.ChatType.GroupChat;
    }



}
