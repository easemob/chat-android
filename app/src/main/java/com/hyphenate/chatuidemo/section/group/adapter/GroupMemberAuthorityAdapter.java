package com.hyphenate.chatuidemo.section.group.adapter;

import android.util.Log;
import android.view.ViewGroup;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.friends.adapter.FriendsAdapter;

public class GroupMemberAuthorityAdapter extends FriendsAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
