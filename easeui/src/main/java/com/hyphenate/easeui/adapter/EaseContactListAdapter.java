package com.hyphenate.easeui.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseImageView;

public class EaseContactListAdapter extends EaseBaseRecyclerViewAdapter<EaseUser> {
    private int emptyLayoutResource;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ease_widget_contact_item, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        if(emptyLayoutResource != 0) {
            return emptyLayoutResource;
        }
        return super.getEmptyLayoutId();
    }

    /**
     * 设置无数据时的布局
     * @param emptyLayoutResource
     */
    public void setEmptyLayoutResource(int emptyLayoutResource) {
        this.emptyLayoutResource = emptyLayoutResource;
    }

    private class ContactViewHolder extends ViewHolder<EaseUser> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName;
        private TextView mSignature;
        private TextView mUnreadMsgNumber;

        public ContactViewHolder(@NonNull View itemView) {
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
            String header = item.getInitialLetter();
            mHeader.setVisibility(View.GONE);
            if(position == 0 || (header != null && !header.equals(getItem(position -1).getInitialLetter()))) {
                if(!TextUtils.isEmpty(header)) {
                    mHeader.setVisibility(View.VISIBLE);
                    mHeader.setText(header);
                }
            }
            mName.setText(item.getUsername());
        }
    }
}
