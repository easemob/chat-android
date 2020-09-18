package com.hyphenate.easeim.section.contact.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.widget.EaseImageView;

public class PublicGroupContactAdapter extends EaseBaseRecyclerViewAdapter<EMGroupInfo> {
    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_widget_contact_item, parent, false));
    }

    private class GroupViewHolder extends ViewHolder<EMGroupInfo> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName;
        private TextView mSignature;
        private TextView mUnreadMsgNumber;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            mHeader = findViewById(R.id.header);
            mAvatar = findViewById(R.id.avatar);
            mName = findViewById(R.id.name);
            mSignature = findViewById(R.id.signature);
            mUnreadMsgNumber = findViewById(R.id.unread_msg_number);
            mHeader.setVisibility(View.GONE);
        }

        @Override
        public void setData(EMGroupInfo item, int position) {
            mAvatar.setImageResource(R.drawable.ease_group_icon);
            mName.setText(item.getGroupName());
            mSignature.setVisibility(View.VISIBLE);
            mSignature.setText(item.getGroupId()+"");
        }
    }
}
