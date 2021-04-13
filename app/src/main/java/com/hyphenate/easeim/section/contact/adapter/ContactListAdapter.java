package com.hyphenate.easeim.section.contact.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.group.viewmodels.GroupMemberAuthorityViewModel;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.modules.contact.model.EaseContactSetStyle;
import com.hyphenate.easeui.widget.EaseImageView;

public class ContactListAdapter extends EaseBaseRecyclerViewAdapter<EaseUser> {

    private GroupMemberAuthorityViewModel  viewModel;
    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_widget_contact_item, parent, false));
    }

    public void setSettingModel(GroupMemberAuthorityViewModel settingModel) {
        this.viewModel = settingModel;
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
            if(!item.getUsername().contains(EMClient.getInstance().getCurrentUser())){
                if(item.getNickname() != null && item.getNickname().length() > 0){
                    mName.setText(item.getNickname());
                }
                Glide.with(mContext).load(item.getAvatar()).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(mAvatar);
            }else{
                EaseUser user = DemoHelper.getInstance().getUserInfo(EMClient.getInstance().getCurrentUser());
                if(user.getNickname() != null && user.getNickname().length() > 0){
                    mName.setText(user.getNickname());
                }
                Glide.with(mContext).load(user.getAvatar()).placeholder(R.drawable.em_login_logo).error(R.drawable.em_login_logo).into(mAvatar);
            }
        }
    }
}
