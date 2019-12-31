package com.hyphenate.chatuidemo.section.friends.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseImageView;

public class ChatRoomContactAdapter extends EaseBaseRecyclerViewAdapter<EMChatRoom> {
    @Override
    public ViewHolder getViewHolder(ViewGroup parent) {
        return new GroupViewHolder(LayoutInflater.from(mContext).inflate(R.layout.em_widget_contact_item, parent, false));
    }

    private class GroupViewHolder extends ViewHolder {
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
        }

        @Override
        public void setData(EMChatRoom item, int position) {
            String header = EaseCommonUtils.getLetter(item.getName());
            Log.e("TAG", "chat room's name = "+header);
            mName.setText(TextUtils.isEmpty(header) ? "" : header);
        }
    }
}
