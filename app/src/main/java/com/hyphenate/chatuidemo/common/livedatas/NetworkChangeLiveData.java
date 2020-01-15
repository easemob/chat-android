package com.hyphenate.chatuidemo.common.livedatas;

import androidx.lifecycle.MutableLiveData;

/**
 * 检测网络变化
 */
public class NetworkChangeLiveData extends MutableLiveData<Integer> {
    private static NetworkChangeLiveData instance;
    private NetworkChangeLiveData(){}

    public static NetworkChangeLiveData getInstance() {
        if(instance == null) {
            synchronized (NetworkChangeLiveData.class) {
                if(instance == null) {
                    instance = new NetworkChangeLiveData();
                }
            }
        }
        return instance;
    }
}
