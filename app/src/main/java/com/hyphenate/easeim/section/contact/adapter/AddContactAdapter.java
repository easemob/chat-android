package com.hyphenate.easeim.section.contact.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeim.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.widget.EaseImageView;

import java.util.List;

public class AddContactAdapter extends EaseBaseRecyclerViewAdapter<String> {
    private List<String> mContacts;

    private OnItemAddClickListener mListener;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_item_search_list, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.demo_layout_empty_list_invisible;
    }

    private class MyViewHolder extends ViewHolder<String> {
        private EaseImageView mIvSearchUserIcon;
        private TextView mTvSearchName;
        private TextView mTvSearchUserId;
        private Button mBtnSearchAdd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            mIvSearchUserIcon = itemView.findViewById(R.id.iv_search_user_icon);
            mTvSearchName = itemView.findViewById(R.id.tv_search_name);
            mTvSearchUserId = itemView.findViewById(R.id.tv_search_user_id);
            mBtnSearchAdd = itemView.findViewById(R.id.btn_search_add);
        }

        @Override
        public void setData(String item, int position) {
            mBtnSearchAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v instanceof Button) {
                        v.setBackground(ContextCompat.getDrawable(mContext, R.drawable.demo_button_unenable_shape));
                        ((Button) v).setText(R.string.em_add_contact_item_button_text_added);
                        v.setEnabled(false);
                    }
                    if(mListener != null) {
                        mListener.onItemAddClick(v, position);
                    }
                }
            });
            if(TextUtils.isEmpty(item)) {
                mTvSearchName.setText("");
                return;
            }
            mTvSearchName.setText(item);
            if(mContacts != null && mContacts.contains(item)) {
                mBtnSearchAdd.setBackground(ContextCompat.getDrawable(mContext, R.drawable.demo_button_unenable_shape));
                mBtnSearchAdd.setText(R.string.em_add_contact_item_button_text_added);
                mBtnSearchAdd.setEnabled(false);
            }else {
                mBtnSearchAdd.setBackground(ContextCompat.getDrawable(mContext, R.drawable.demo_add_contact_button_bg));
                mBtnSearchAdd.setText(R.string.em_add_contact_item_button_text);
                mBtnSearchAdd.setEnabled(true);
            }
        }
    }

    /**
     * 设置点击事件
     * @param listener
     */
    public void setOnItemAddClickListener(OnItemAddClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 条目添加事件
     */
    public interface OnItemAddClickListener {
        void onItemAddClick(View view, int position);
    }

    public void addLocalContacts(List<String> contacts) {
        this.mContacts = contacts;
    }
}
