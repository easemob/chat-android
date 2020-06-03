package com.hyphenate.easeui.manager;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.adapter.EaseBaseDelegateAdapter;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.ui.chat.delegates.EaseTextAdapterDelegate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EaseConTypeSetManager {
    private static EaseConTypeSetManager mInstance;
    private Set<EaseAdapterDelegate<?, ?>> delegates = new HashSet<>();
    private EaseAdapterDelegate<?,?> defaultDelegate = new EaseTextAdapterDelegate();
    private boolean hasConsistItemType;

    private EaseConTypeSetManager(){}

    public static EaseConTypeSetManager getInstance() {
        if(mInstance == null) {
            synchronized (EaseConTypeSetManager.class) {
                if(mInstance == null) {
                    mInstance = new EaseConTypeSetManager();
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
    public EaseConTypeSetManager setConsistItemType(boolean hasConsistItemType) {
        this.hasConsistItemType = hasConsistItemType;
        return this;
    }

    /**
     * 添加对话类型
     * @param delegate
     * @return
     */
    public EaseConTypeSetManager addConversationType(EaseAdapterDelegate<?, ?> delegate) {
        delegates.add(delegate);
        return this;
    }

    /**
     * 设置默认的对话类型
     * @param delegate
     * @return
     */
    public EaseConTypeSetManager setDefaultConversionType(EaseAdapterDelegate<?, ?> delegate) {
        this.defaultDelegate = delegate;
        return this;
    }

    /**
     * 注册对话类型
     * @param adapter
     */
    public void registerConversationType(EaseBaseDelegateAdapter adapter) {
        if(adapter == null) {
            return;
        }
        if(delegates.size() <= 0) {
            return;
        }
        for (EaseAdapterDelegate<?, ?> delegate : delegates) {
            if(adapter instanceof EaseMessageAdapter) {
                adapter.addDelegate(delegate, EMMessage.Direct.SEND.toString());
                adapter.addDelegate(delegate, EMMessage.Direct.RECEIVE.toString());
            }else {
                adapter.addDelegate(delegate);
            }
        }
        if(defaultDelegate == null) {
            defaultDelegate = new EaseTextAdapterDelegate();
        }
        adapter.setFallbackDelegate(defaultDelegate);
    }

    public boolean hasConsistItemType() {
        return this.hasConsistItemType;
    }

    public List<EaseAdapterDelegate<?,?>> getConversationTypeList() {
        return new ArrayList<>(delegates);
    }
}
