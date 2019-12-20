package com.hyphenate.chatuidemo.section;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;

public class MainViewModel extends AndroidViewModel {

    private SingleSourceLiveData<Integer> switchObservable;

    public MainViewModel(@NonNull Application application) {
        super(application);
        switchObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Integer> getSwitchObservable() {
        return switchObservable;
    }

    /**
     * 设置可见的fragment
     * @param title
     */
    public void setVisibleFragment(Integer title) {
        switchObservable.setValue(title);
    }

}
