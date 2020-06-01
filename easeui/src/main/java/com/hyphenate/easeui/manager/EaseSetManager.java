package com.hyphenate.easeui.manager;

import androidx.collection.SparseArrayCompat;

import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.ui.chat.delegates.EaseTextAdapterDelegate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EaseSetManager {
    private static EaseSetManager mInstance;
    private Set<EaseAdapterDelegate<?, ?>> delegates = new HashSet<>();
    private EaseAdapterDelegate<?,?> defaultDelegate = new EaseTextAdapterDelegate();
    private boolean hasConsistItemType;

    private EaseSetManager(){}

    public static EaseSetManager getInstance() {
        if(mInstance == null) {
            synchronized (EaseSetManager.class) {
                if(mInstance == null) {
                    mInstance = new EaseSetManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 是否使用自定义的item ViewType
     * @param hasConsistItemType
     * @return
     */
    public EaseSetManager setConsistItemType(boolean hasConsistItemType) {
        this.hasConsistItemType = hasConsistItemType;
        return this;
    }

    /**
     * 添加对话类型
     * @param delegate
     * @return
     */
    public EaseSetManager addConversationType(EaseAdapterDelegate<?, ?> delegate) {
        delegates.add(delegate);
        return this;
    }

    /**
     * 设置默认的对话类型
     * @param delegate
     * @return
     */
    public EaseSetManager setDefaultConversionType(EaseAdapterDelegate<?, ?> delegate) {
        this.defaultDelegate = delegate;
        return this;
    }

    public boolean hasConsistItemType() {
        return this.hasConsistItemType;
    }

    public List<EaseAdapterDelegate<?,?>> getConversationTypeList() {
        return new ArrayList<>(delegates);
    }
}
