package com.hyphenate.chatuidemo.section.group.adapter;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.friends.adapter.ContactListAdapter;

public class GroupMemberAuthorityAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
