package com.hyphenate.easeui.adapter;

import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EaseBaseDelegateAdapter<T> extends EaseBaseRecyclerViewAdapter<T> {
    private EaseAdapterDelegatesManager delegatesManager;

    public EaseBaseDelegateAdapter() {
        this.delegatesManager = new EaseAdapterDelegatesManager(false);
    }

    public EaseBaseDelegateAdapter(EaseAdapterDelegatesManager delegatesManager) {
        this.delegatesManager = delegatesManager;
    }

    public EaseBaseDelegateAdapter addDelegate(EaseAdapterDelegate delegate) {
        delegatesManager.addDelegate(delegate, delegate.tag);
        notifyDataSetChanged();
        return this;
    }

    public EaseBaseDelegateAdapter addDelegate(EaseAdapterDelegate delegate, String tag) {
        delegate.tag = tag;
        delegatesManager.addDelegate(delegate, tag);
        notifyDataSetChanged();
        return this;
    }

    public int getDelegateViewType(EaseAdapterDelegate delegate) {
        return delegatesManager.getDelegateViewType(delegate);
    }

    public EaseBaseDelegateAdapter setFallbackDelegate(EaseAdapterDelegate delegate) {
        delegatesManager.fallbackDelegate = delegate;
        return this;
    }

    public EaseAdapterDelegate getAdapterDelegate(int viewType) {
        return delegatesManager.getDelegate(viewType);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        try {
            viewType = delegatesManager.getItemViewType(getItem(position), position);
        } catch (Exception e) {
            return super.getItemViewType(position);
        }
        return viewType;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        delegatesManager.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        return delegatesManager.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        delegatesManager.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        delegatesManager.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        delegatesManager.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        delegatesManager.onDetachedFromRecyclerView(recyclerView);
    }

}
