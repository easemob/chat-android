package com.hyphenate.chatuidemo.common.livedatas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.hyphenate.chatuidemo.common.model.EaseUser;
import com.hyphenate.chatuidemo.common.net.Resource;

/**
 * 设置并监听单一数据源时使用 LiveData
 * 方便于当需要切换数据源时自动取消掉前一个数据源的监听
 *
 */
public class UserInstanceLiveData extends MutableLiveData<EaseUser> {
    private static UserInstanceLiveData instance;
    private LiveData<EaseUser> lastSource;
    private EaseUser lastData;
    private final Observer<EaseUser> observer = new Observer<EaseUser>() {
        @Override
        public void onChanged(EaseUser t) {
            if (t != null && t == lastData) {
                return;
            }

            lastData = t;
            setValue(t);
        }
    };
    private UserInstanceLiveData(){}

    public static UserInstanceLiveData getInstance() {
        if(instance == null) {
            synchronized (UserInstanceLiveData.class) {
                if(instance == null) {
                    instance = new UserInstanceLiveData();
                }
            }
        }
        return instance;
    }

    /**
     * 设置数据源，当有已设置过的数据源时会取消该数据源的监听
     *
     * @param source
     */
    public void setSource(LiveData<EaseUser> source) {
        if (lastSource == source) {
            return;
        }

        if (lastSource != null) {
            lastSource.removeObserver(observer);
        }
        lastSource = source;

        if (hasActiveObservers()) {
            lastSource.observeForever(observer);
        }
    }

    @Override
    protected void onActive() {
        super.onActive();

        if (lastSource != null) {
            lastSource.observeForever(observer);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        if (lastSource != null) {
            lastSource.removeObserver(observer);
        }
    }
}
