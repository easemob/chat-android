package com.hyphenate.easeim.section.group.adapter;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.contact.adapter.ContactListAdapter;

public class GroupMemberAuthorityAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
