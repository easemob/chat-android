package com.hyphenate.easeim.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMGroupManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

public class GroupContactViewModel extends AndroidViewModel {
    private String currentUser;
    private EMGroupManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EMGroup>>> allGroupObservable;
    private SingleSourceLiveData<Resource<List<EaseUser>>> groupMemberObservable;
    private SingleSourceLiveData<Resource<EMCursorResult<EMGroupInfo>>> publicGroupObservable;
    private SingleSourceLiveData<Resource<EMCursorResult<EMGroupInfo>>> morePublicGroupObservable;
    private SingleSourceLiveData<Resource<List<EMGroup>>> groupObservable;
    private SingleSourceLiveData<Resource<List<EMGroup>>> moreGroupObservable;

    public GroupContactViewModel(@NonNull Application application) {
        super(application);
        currentUser = DemoHelper.getInstance().getCurrentUser();
        mRepository = new EMGroupManagerRepository();
        allGroupObservable = new SingleSourceLiveData<>();
        groupMemberObservable = new SingleSourceLiveData<>();
        publicGroupObservable = new SingleSourceLiveData<>();
        morePublicGroupObservable = new SingleSourceLiveData<>();
        groupObservable = new SingleSourceLiveData<>();
        moreGroupObservable = new SingleSourceLiveData<>();
    }

    public LiveDataBus getMessageObservable() {
        return LiveDataBus.get();
    }

    public LiveData<Resource<List<EMGroup>>> getAllGroupsObservable() {
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

    public void getGroupMembers(String groupId) {
        groupMemberObservable.setSource(mRepository.getGroupAllMembers(groupId));
    }

    public LiveData<Resource<List<EaseUser>>> getGroupMember() {
        return groupMemberObservable;
    }

    public LiveData<Resource<EMCursorResult<EMGroupInfo>>> getPublicGroupObservable() {
        return publicGroupObservable;
    }

    public void getPublicGroups(int pageSize) {
        publicGroupObservable.setSource(mRepository.getPublicGroupFromServer(pageSize, null));
    }

    public LiveData<Resource<EMCursorResult<EMGroupInfo>>> getMorePublicGroupObservable() {
        return morePublicGroupObservable;
    }

    public void getMorePublicGroups(int pageSize, String cursor) {
        morePublicGroupObservable.setSource(mRepository.getPublicGroupFromServer(pageSize, cursor));
    }

    public LiveData<Resource<List<EMGroup>>> getGroupObservable() {
        return groupObservable;
    }

    public void loadGroupListFromServer(int pageIndex, int pageSize) {
        groupObservable.setSource(mRepository.getGroupListFromServer(pageIndex, pageSize));
    }

    public LiveData<Resource<List<EMGroup>>> getMoreGroupObservable() {
        return moreGroupObservable;
    }

    public void loadMoreGroupListFromServer(int pageIndex, int pageSize) {
        moreGroupObservable.setSource(mRepository.getGroupListFromServer(pageIndex, pageSize));
    }
}
