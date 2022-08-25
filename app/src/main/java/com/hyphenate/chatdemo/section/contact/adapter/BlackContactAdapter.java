package com.hyphenate.chatdemo.section.contact.adapter;

import com.hyphenate.chatdemo.R;

public class BlackContactAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
