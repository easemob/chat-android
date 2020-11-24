package com.hyphenate.easeui.modules.conversation.adapter;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseBaseDelegateAdapter;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;

public class EaseConversationListAdapter extends EaseBaseDelegateAdapter<EaseConversationInfo> {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_conversation_data;
    }

}

