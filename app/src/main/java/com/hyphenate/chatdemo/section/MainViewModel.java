package com.hyphenate.chatdemo.section;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.common.db.DemoDbHelper;
import com.hyphenate.chatdemo.common.db.dao.InviteMessageDao;
import com.hyphenate.chatdemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatdemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatdemo.common.net.Resource;

public class MainViewModel extends AndroidViewModel {
    private MainRepository mainReposity;
    private InviteMessageDao inviteMessageDao;
    private SingleSourceLiveData<Integer> switchObservable;
    private MutableLiveData<String> homeUnReadObservable;
    private SingleSourceLiveData<Resource<String>> fetchRobotObservable;


    public MainViewModel(@NonNull Application application) {
        super(application);
        mainReposity=new MainRepository();
        switchObservable = new SingleSourceLiveData<>();
        inviteMessageDao = DemoDbHelper.getInstance(application).getInviteMessageDao();
        homeUnReadObservable = new MutableLiveData<>();
        fetchRobotObservable = new SingleSourceLiveData<>();
    }


    public LiveData<Resource<String>> getFetchRobotObservable() {
        return fetchRobotObservable;
    }

    public void fetchRobotObservable(){
        fetchRobotObservable.setSource(mainReposity.fetchRobotInfo());
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

    public LiveData<String> homeUnReadObservable() {
        return homeUnReadObservable;
    }

    public LiveDataBus messageChangeObservable() {
        return LiveDataBus.get();
    }

    public void checkUnreadMsg() {
        int unreadCount = 0;
        if(inviteMessageDao != null) {
            unreadCount = inviteMessageDao.queryUnreadCount();
        }
        int unreadMessageCount = DemoHelper.getInstance().getChatManager().getUnreadMessageCount();
        String count = getUnreadCount(unreadCount + unreadMessageCount);
        homeUnReadObservable.postValue(count);
    }

    /**
     * 获取未读消息数目
     * @param count
     * @return
     */
    private String getUnreadCount(int count) {
        if(count <= 0) {
            return null;
        }
        if(count > 99) {
            return "99+";
        }
        return String.valueOf(count);
    }

}
