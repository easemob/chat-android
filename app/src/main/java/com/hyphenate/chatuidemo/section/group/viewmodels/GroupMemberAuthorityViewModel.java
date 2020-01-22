package com.hyphenate.chatuidemo.section.group.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class GroupMemberAuthorityViewModel extends AndroidViewModel {
    private EMGroupManagerRepository repository;
    private SingleSourceLiveData<List<EaseUser>> adminObservable;
    private SingleSourceLiveData<Resource<List<EaseUser>>> membersObservable;

    public GroupMemberAuthorityViewModel(@NonNull Application application) {
        super(application);
        repository = new EMGroupManagerRepository();
        adminObservable = new SingleSourceLiveData<>();
        membersObservable = new SingleSourceLiveData<>();
    }

    public LiveData<List<EaseUser>> getAdminObservable(String groupId) {
        EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(groupId);
        List<String> adminList = group.getAdminList();
        adminObservable.postValue(EmUserEntity.parse(adminList));
        return adminObservable;
    }

    public LiveData<Resource<List<EaseUser>>> getMemberObservable() {
        return membersObservable;
    }

    public void getMembers(String groupId) {
        membersObservable.setSource(repository.getGroupMembers(groupId));
    }

}
