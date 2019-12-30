package com.hyphenate.chatuidemo.section.friends.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseImageView;

public class GroupContactAdapter extends EaseBaseRecyclerViewAdapter<EMGroup> {
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
        public void setData(EMGroup item, int position) {
            String header = EaseCommonUtils.getLetter(item.getGroupName());
            Log.e("TAG", "GroupContactAdapter header = "+header);
            mHeader.setVisibility(View.GONE);
            // 是否显示字母
            if(position == 0 || (header != null && !header.equals(EaseCommonUtils.getLetter(getItem(position - 1).getGroupName())))) {
                if(!TextUtils.isEmpty(header)) {
                    mHeader.setVisibility(View.VISIBLE);
                    mHeader.setText(header);
                }
            }
            mName.setText(item.getGroupName());
        }
    }
}
