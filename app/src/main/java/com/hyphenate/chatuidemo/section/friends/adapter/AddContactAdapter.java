package com.hyphenate.chatuidemo.section.friends.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.widget.EaseImageView;

public class AddContactAdapter extends EaseBaseRecyclerViewAdapter<String> {

    private OnItemAddClickListener mListener;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.em_item_search_list, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.em_layout_empty_list_invisible;
    }

    private class MyViewHolder extends ViewHolder {
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
}
