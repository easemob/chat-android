package com.hyphenate.chatdemo.section.group.adapter;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.section.contact.adapter.ContactListAdapter;

public class GroupMemberAuthorityAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
