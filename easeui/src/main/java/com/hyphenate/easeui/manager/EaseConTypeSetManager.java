package com.hyphenate.easeui.manager;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.adapter.EaseBaseDelegateAdapter;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.delegate.EaseCustomAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseExpressionAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseFileAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseImageAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseLocationAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseTextAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseVideoAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseVoiceAdapterDelegate;

import java.util.HashSet;
import java.util.Set;

public class EaseConTypeSetManager {
    private static EaseConTypeSetManager mInstance;
    private EaseAdapterDelegate<?,?> defaultDelegate = new EaseTextAdapterDelegate();
    private Class<? extends EaseAdapterDelegate<?,?>> defaultDelegateCls;
    private Set<Class<? extends EaseAdapterDelegate<?, ?>>> delegates = new HashSet<>();
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

    public EaseConTypeSetManager addConversationType(Class<? extends EaseAdapterDelegate<?, ?>> cls) {
        delegates.add(cls);
        return this;
    }

    /**
     * 设置默认的对话类型
     * @param cls
     * @return
     */
    public EaseConTypeSetManager setDefaultConversionType(Class<? extends EaseAdapterDelegate<?, ?>> cls) {
        this.defaultDelegateCls = cls;
        return this;
    }

    /**
     * 注册对话类型
     * @param adapter
     */
    public void registerConversationType(EaseBaseDelegateAdapter adapter) throws InstantiationException, IllegalAccessException{
        if(adapter == null) {
            return;
        }
        //如果没有注册聊天类型，则使用默认的
        if(delegates.size() <= 0) {
            addConversationType(EaseExpressionAdapterDelegate.class)       //自定义表情
            .addConversationType(EaseFileAdapterDelegate.class)             //文件
            .addConversationType(EaseImageAdapterDelegate.class)            //图片
            .addConversationType(EaseLocationAdapterDelegate.class)         //定位
            .addConversationType(EaseVideoAdapterDelegate.class)            //视频
            .addConversationType(EaseVoiceAdapterDelegate.class)            //声音
            .addConversationType(EaseCustomAdapterDelegate.class)           //自定义消息
            .setDefaultConversionType(EaseTextAdapterDelegate.class);       //文本
        }
        for (Class<? extends EaseAdapterDelegate<?, ?>> cls : delegates) {
            EaseAdapterDelegate delegate = cls.newInstance();
            if(adapter instanceof EaseMessageAdapter) {
                adapter.addDelegate(delegate, EMMessage.Direct.SEND.toString());
                adapter.addDelegate(delegate, EMMessage.Direct.RECEIVE.toString());
            }else {
                adapter.addDelegate(delegate);
            }
        }

        if(defaultDelegateCls == null) {
            defaultDelegate = new EaseTextAdapterDelegate();
        }else {
            defaultDelegate = defaultDelegateCls.newInstance();
        }
        adapter.setFallbackDelegate(defaultDelegate, EMMessage.Direct.SEND.toString());
        adapter.setFallbackDelegate(defaultDelegate, EMMessage.Direct.RECEIVE.toString());
    }

    public boolean hasConsistItemType() {
        return this.hasConsistItemType;
    }

}
