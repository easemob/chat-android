package com.hyphenate.chatuidemo.section.friends.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseImageView;

public class FriendsAdapter extends EaseBaseRecyclerViewAdapter<EaseUser> {
    private TextView mHeader;
    private EaseImageView mAvatar;
    private TextView mName;
    private TextView mSignature;
    private TextView mUnreadMsgNumber;

    @Override
    public int getItemLayoutId() {
        return R.layout.em_widget_contact_item;
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.em_layout_friends_empty_list;
    }

    @Override
    public void initView(View itemView) {
        mHeader = itemView.findViewById(R.id.header);
        mAvatar = itemView.findViewById(R.id.avatar);
        mName = itemView.findViewById(R.id.name);
        mSignature = itemView.findViewById(R.id.signature);
        mUnreadMsgNumber = itemView.findViewById(R.id.unread_msg_number);
    }

    @Override
    public void setData(EaseUser item, int position) {
        String header = item.getInitialLetter();
        mHeader.setVisibility(View.GONE);
        if(position == 0 || (header != null && header.equals(getItem(position -1).getInitialLetter()))) {
            if(!TextUtils.isEmpty(header)) {
                mHeader.setVisibility(View.VISIBLE);
            }
        }
        mName.setText(item.getUsername());
    }
}
