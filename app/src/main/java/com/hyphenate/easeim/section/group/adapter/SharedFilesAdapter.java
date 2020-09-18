package com.hyphenate.easeim.section.group.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.util.TextFormater;

import java.util.Date;

public class SharedFilesAdapter extends EaseBaseRecyclerViewAdapter<EMMucSharedFile> {

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_layout_item_shared_file_row, parent, false));
    }

    private class MyViewHolder extends ViewHolder<EMMucSharedFile> {

        private TextView tvFileName;
        private TextView tvFileSize;
        private TextView tvUpdateTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            tvFileName = findViewById(R.id.tv_file_name);
            tvFileSize = findViewById(R.id.tv_file_size);
            tvUpdateTime = findViewById(R.id.tv_update_time);
        }

        @Override
        public void setData(EMMucSharedFile item, int position) {
            tvFileName.setText(item.getFileName());
            tvFileSize.setText(TextFormater.getDataSize(item.getFileSize()));
            tvUpdateTime.setText(new Date(item.getFileUpdateTime()).toString());
        }
    }
}
