package com.hyphenate.chatuidemo.common.livedatas;

import androidx.lifecycle.MutableLiveData;

public class MessageChangeLiveData extends MutableLiveData<String> {

    private static MessageChangeLiveData instance;
    private MessageChangeLiveData(){}

    public static MessageChangeLiveData getInstance() {
        if(instance == null) {
            synchronized (MessageChangeLiveData.class) {
                if(instance == null) {
                    instance = new MessageChangeLiveData();
                }
            }
        }
        return instance;
    }
}
