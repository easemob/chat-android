package com.hyphenate.easeui.adapter;

import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EaseAdapterDelegate<T, VH extends RecyclerView.ViewHolder> {
    public static final String DEFAULT_TAG = "";
    public String tag = DEFAULT_TAG;

    public EaseAdapterDelegate() {}

    public EaseAdapterDelegate(String tag) {
        this.tag = tag;
    }

    public boolean isForViewType(T item, int position) {
        return true;
    }

    public abstract VH onCreateViewHolder(ViewGroup parent);

    public void onBindViewHolder(VH holder, int position, T item){}

    public int getItemCount() {
        return 0;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     */
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position,
                                 @NonNull List<Object> payloads,
                                 T item) {}

    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {}

    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        return false;
    }

    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {}

    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {}

    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {}

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {}

    public int getItemViewType() { return 0;}
}
