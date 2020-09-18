package com.hyphenate.easeim.section.contact.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.model.ContactHeaderBean;
import com.hyphenate.easeim.common.widget.ContactItemView;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactHeaderAdapter extends EaseBaseRecyclerViewAdapter<ContactHeaderBean> {
    private static final int[] names = {R.string.em_friends_new_chat, R.string.em_friends_group_chat,
            /*R.string.em_friends_label, */R.string.em_friends_chat_room/*, R.string.em_friends_official_account, R.string.em_friends_av_conference*/};
    private static final int[] icons = {R.drawable.em_friends_new_chat, R.drawable.em_friends_group_chat,
            /*R.drawable.em_friends_label, */R.drawable.em_friends_chat_room/*, R.drawable.em_friends_official_account, R.drawable.em_chat_video_call_normal*/};

    public ContactHeaderAdapter() {
        setHeaderData();
    }

    private void setHeaderData() {
        ContactHeaderBean bean;
        List<ContactHeaderBean> headers = new ArrayList<>();
        for(int i = 0; i < names.length; i++) {
            int nameRes = names[i];
            int iconRes = icons[i];
            bean = new ContactHeaderBean();
            bean.setImage(iconRes);
            bean.setName(nameRes);
            headers.add(bean);
        }
        setData(headers);
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        ContactItemView itemView = new ContactItemView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext, 58));
        itemView.setLayoutParams(params);
        return new MyViewHolder(itemView);
    }

    private static class MyViewHolder extends ViewHolder<ContactHeaderBean> {
        private ContactItemView contactItemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            contactItemView = (ContactItemView) itemView;
        }

        @Override
        public void setData(ContactHeaderBean item, int position) {
            contactItemView.setName(itemView.getContext().getString(item.getName()));
            contactItemView.setImage(item.getImage());
        }
    }
}

