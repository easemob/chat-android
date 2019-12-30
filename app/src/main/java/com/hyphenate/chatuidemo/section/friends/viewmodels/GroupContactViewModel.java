package com.hyphenate.chatuidemo.section.friends.viewmodels;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;

import java.util.ArrayList;
import java.util.List;

public class GroupContactViewModel extends AndroidViewModel {
    private String currentUser;
    private EMGroupManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EMGroup>>> allGroupObservable;

    public GroupContactViewModel(@NonNull Application application) {
        super(application);
        currentUser = DemoHelper.getInstance().getCurrentUser();
        mRepository = new EMGroupManagerRepository();
        allGroupObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EMGroup>>> getAllGroups() {
        return allGroupObservable;
    }

    public void loadAllGroups() {
        allGroupObservable.setSource(mRepository.getAllGroups());
    }
    
    public List<EMGroup> getManageGroups(List<EMGroup> allGroups) {
        return mRepository.getAllManageGroups(allGroups);
    }

    public List<EMGroup> getJoinGroups(List<EMGroup> allGroups) {
        return mRepository.getAllJoinGroups(allGroups);
    }

}
