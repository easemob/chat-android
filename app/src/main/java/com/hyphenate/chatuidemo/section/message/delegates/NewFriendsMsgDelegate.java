package com.hyphenate.chatuidemo.section.message.delegates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;

public abstract class NewFriendsMsgDelegate<T, VH extends EaseBaseRecyclerViewAdapter.ViewHolder> extends EaseAdapterDelegate<T, VH> {

    @Override
    public VH onCreateViewHolder(ViewGroup parent, String tag) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
        return createViewHolder(view);
    }

    protected abstract int getLayoutId();

    protected abstract VH createViewHolder(View view);
}
