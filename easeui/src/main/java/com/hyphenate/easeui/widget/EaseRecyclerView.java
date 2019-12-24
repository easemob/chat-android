package com.hyphenate.easeui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以添加header和footer
 * 参考：https://github.com/idisfkj/EnhanceRecyclerView
 */
public class EaseRecyclerView extends RecyclerView {
    private static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    private static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;
    private RecyclerViewContextMenuInfo mContextMenuInfo;
    private List<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();
    private List<FixedViewInfo> mFooterViewInfos = new ArrayList<>();
    private Adapter mAdapter;
    private boolean isStaggered;
    private boolean isShouldSpan;

    public EaseRecyclerView(@NonNull Context context) {
        super(context);
    }

    public EaseRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addHeaderView(View view) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + mHeaderViewInfos.size();
        mHeaderViewInfos.add(info);

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void addFooterView(View view) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_FOOTER_VIEW_TYPE + mFooterViewInfos.size();
        mFooterViewInfos.add(info);

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if(!(adapter instanceof WrapperRecyclerViewAdapter)) {
            mAdapter = new WrapperRecyclerViewAdapter(adapter);
        }
        super.setAdapter(mAdapter);
        if(isShouldSpan) {
            ((WrapperRecyclerViewAdapter)mAdapter).adjustSpanSize(this);
        }
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        if(layout instanceof GridLayoutManager || layout instanceof  StaggeredGridLayoutManager) {
            isShouldSpan = true;
        }
        super.setLayoutManager(layout);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        int longPressPosition = getChildAdapterPosition(originalView);
        if(longPressPosition >= 0) {
            long longPressId = getAdapter().getItemId(longPressPosition);
            mContextMenuInfo = new RecyclerViewContextMenuInfo(longPressPosition, longPressId);
            return super.showContextMenuForChild(originalView);
        }
        return false;
    }

    private int getFootersCount() {
        return mFooterViewInfos.size();
    }

    private int getHeadersCount() {
        return mHeaderViewInfos.size();
    }

    public class FixedViewInfo {
        public View view;
        public int viewType;
    }

    private class WrapperRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Adapter mAdapter;

        public WrapperRecyclerViewAdapter(Adapter adapter) {
            this.mAdapter = adapter;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType >= BASE_HEADER_VIEW_TYPE && viewType < BASE_HEADER_VIEW_TYPE + getHeadersCount()) {
                View view = mHeaderViewInfos.get(viewType - BASE_HEADER_VIEW_TYPE).view;
                return viewHolder(view);
            }else if(viewType >= BASE_FOOTER_VIEW_TYPE && viewType < BASE_FOOTER_VIEW_TYPE + getFootersCount()) {
                View view = mFooterViewInfos.get(viewType - BASE_FOOTER_VIEW_TYPE).view;
                return viewHolder(view);
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int headersCount = getHeadersCount();
            if(position < headersCount) {
                return;
            }
            int adjPosition = position - headersCount;
            int adapterCount = 0;
            if(mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if(adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            int headersCount = getHeadersCount();
            if(mAdapter != null && position >= headersCount) {
                int adjPosition = position - headersCount;
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public int getItemCount() {
            if(mAdapter != null) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            }else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            int headersCount = getHeadersCount();
            if(position < headersCount) {
                return mHeaderViewInfos.get(position).viewType;
            }
            int adjPosition = position - headersCount;
            int adapterPosition = 0;
            if(mAdapter != null) {
                adapterPosition = mAdapter.getItemCount();
                if(adjPosition < adapterPosition) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return mFooterViewInfos.get(position - adapterPosition - getHeadersCount()).viewType;
        }

        public void adjustSpanSize(RecyclerView recyclerView) {
            if(recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int headersCount = getHeadersCount();
                        int adjPosition = position - headersCount;
                        if(position < headersCount || adjPosition >= mAdapter.getItemCount()) {
                            return manager.getSpanCount();
                        }
                        return 1;
                    }
                });
            }

            if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                isStaggered = true;
            }
        }
    }

    private RecyclerView.ViewHolder viewHolder(View itemView) {
        if(isStaggered) {
            StaggeredGridLayoutManager.LayoutParams params =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            itemView.setLayoutParams(params);
        }
        return new RecyclerView.ViewHolder(itemView){};
    }

    public static class RecyclerViewContextMenuInfo implements ContextMenu.ContextMenuInfo {
        public int position;
        public long id;

        public RecyclerViewContextMenuInfo(int position, long id) {
            this.position = position;
            this.id = id;
        }
    }

}
