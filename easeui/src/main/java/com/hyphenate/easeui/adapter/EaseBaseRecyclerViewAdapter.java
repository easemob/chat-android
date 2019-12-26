package com.hyphenate.easeui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.PSource;

/**
 * 作为RecyclerView Adapter的基类，有默认空白布局
 * @param <T>
 */
public abstract class EaseBaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<EaseBaseRecyclerViewAdapter.ViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 1;
    private static final int VIEW_TYPE_ITEM = 0;
    private OnItemClickListener mOnItemClickListener;
    public Context mContext;
    public List<T> mData;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if(viewType == VIEW_TYPE_EMPTY) {
            return getEmptyViewHolder(parent);
        }
        return getViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull EaseBaseRecyclerViewAdapter.ViewHolder holder, final int position) {
        if(mData == null || mData.isEmpty()) {
            return;
        }
        T item = mData.get(position);
        holder.setHolderData(item, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickAction(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mData == null || mData.isEmpty()) ? 1 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (mData == null || mData.isEmpty()) ? VIEW_TYPE_EMPTY : VIEW_TYPE_ITEM;
    }


    /**
     * 点击事件
     * @param v
     * @param position
     */
    public void itemClickAction(View v, int position) {
        if(mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, position);
        }
    }

    public ViewHolder getViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(getItemLayoutId(), parent, false);
        return new ViewHolder(view);
    }

    private ViewHolder getEmptyViewHolder(ViewGroup parent) {
        View emptyView = getEmptyView(parent);
        return new ViewHolder(emptyView);
    }

    /**
     * 获取空白布局
     * @param parent
     * @return
     */
    private View getEmptyView(ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(getEmptyLayoutId(), parent, false);
    }

    /**
     * 根据position获取相应的data
     * @param position
     * @return
     */
    public T getItem(int position) {
        return mData.get(position);
    }

    /**
     * 添加数据
     * @param data
     */
    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    /**
     * 添加单个数据
     * @param item
     */
    public void addData(T item) {
        if(this.mData == null) {
            this.mData = new ArrayList<>();
        }
        this.mData.add(item);
        notifyDataSetChanged();
    }

    /**
     * 添加更多数据
     * @param data
     */
    public void addData(List<T> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        if(this.mData == null) {
            this.mData = data;
        }else {
            this.mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取数据
     * @return
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 清除数据
     */
    public void clearData() {
        if(mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * set item click
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        public void setHolderData(T item, int position) {
            setData(item, position);
        }
    }

    /**
     * 返回空白布局
     * @return
     */
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }

    /**
     * 获取item布局
     * @return
     */
    public abstract int getItemLayoutId();

    /**
     * 初始化控件
     * @param itemView
     */
    public abstract void initView(View itemView);

    /**
     * 设置数据
     * @param item
     * @param position
     */
    public abstract void setData(T item, int position);
}
