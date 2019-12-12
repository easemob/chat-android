package com.hyphenate.chatuidemo.core.livedatas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.hyphenate.chatuidemo.core.bean.EaseUser;
import com.hyphenate.chatuidemo.core.net.Resource;

/**
 * 设置并监听单一数据源时使用 LiveData
 * 方便于当需要切换数据源时自动取消掉前一个数据源的监听
 *
 */
public class UserInstanceLiveData extends MutableLiveData<Resource<EaseUser>> {
    private static UserInstanceLiveData instance;
    private LiveData<Resource<EaseUser>> lastSource;
    private Resource<EaseUser> lastData;
    private final Observer<Resource<EaseUser>> observer = new Observer<Resource<EaseUser>>() {
        @Override
        public void onChanged(Resource<EaseUser> t) {
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
    public void setSource(LiveData<Resource<EaseUser>> source) {
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
