package com.hyphenate.chatdemo.section.contact.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatdemo.R;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.widget.EaseImageView;

public class ContactListAdapter extends EaseBaseRecyclerViewAdapter<EaseUser> {

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_widget_contact_item, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.demo_layout_friends_empty_list;
    }

    private class MyViewHolder extends ViewHolder<EaseUser> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName;
        private TextView mSignature;
        private TextView mUnreadMsgNumber;

        public MyViewHolder(@NonNull View itemView) {
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
        public void setData(EaseUser item, int position) {
            Log.e("TAG", "item = "+item.toString());
            String header = item.getInitialLetter();
            Log.e("TAG", "position = "+position + " header = "+header);
            mHeader.setVisibility(View.GONE);
            if(position == 0 || (header != null && !header.equals(getItem(position -1).getInitialLetter()))) {
                if(!TextUtils.isEmpty(header)) {
                    mHeader.setVisibility(View.VISIBLE);
                    mHeader.setText(header);
                }
            }
            //判断是否为自己账号多端登录
            String username = item.getUsername();
            String nickname = item.getNickname();
            if(username.contains("/") && username.contains(EMClient.getInstance().getCurrentUser())) {
                username = EMClient.getInstance().getCurrentUser();
            }
            Glide.with(mAvatar)
                    .load(item.getAvatar())
                    .placeholder(R.drawable.ease_default_avatar)
                    .error(R.drawable.ease_default_avatar)
                    .into(mAvatar);

            String postfix = "";
            if(username.contains("/") && username.contains(EMClient.getInstance().getCurrentUser())) {
                postfix = "/"+username.split("/")[1];
                nickname = nickname+postfix;
            }
            mName.setText(nickname);
        }
    }
}
