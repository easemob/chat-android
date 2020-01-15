package com.hyphenate.chatuidemo.common.repositories;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EMGroupManagerRepository extends BaseEMRepository{

    /**
     * 获取所有的群组列表
     * @return
     */
    public LiveData<Resource<List<EMGroup>>> getAllGroups() {
        return new NetworkBoundResource<List<EMGroup>, List<EMGroup>>() {
            @Override
            protected boolean shouldFetch(List<EMGroup> data) {
                return true;
            }

            @Override
            protected LiveData<List<EMGroup>> loadFromDb() {
                List<EMGroup> allGroups = getGroupManager().getAllGroups();
                return new MutableLiveData<>(allGroups);
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<List<EMGroup>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                getGroupManager().asyncGetJoinedGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
                    @Override
                    public void onSuccess(List<EMGroup> value) {
                        callBack.onSuccess(new MutableLiveData<>(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

            @Override
            protected void saveCallResult(List<EMGroup> item) {

            }

        }.asLiveData();
    }

    /**
     * 获取群组成员列表
     * @param groupId
     * @return
     */
    public LiveData<Resource<List<EaseUser>>> getGroupMembers(String groupId) {
        return new NetworkOnlyResource<List<EaseUser>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                DemoHelper.getInstance().getGroupManager().asyncGetGroupFromServer(groupId, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        List<String> members = value.getMembers();
                        members.addAll(value.getAdminList());
                        members.add(value.getOwner());
                        if(!members.isEmpty()) {
                            List<EaseUser> users = EmUserEntity.parse(members);
                            sortUserData(users);
                            callBack.onSuccess(createLiveData(users));
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

        }.asLiveData();
    }

    private void sortUserData(List<EaseUser> users) {
        Collections.sort(users, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNickname().compareTo(rhs.getNickname());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
    }

    public List<EMGroup> getAllManageGroups(List<EMGroup> allGroups) {
        if(allGroups != null && allGroups.size() > 0) {
            List<EMGroup> manageGroups = new ArrayList<>();
            for (EMGroup group : allGroups) {
                if(TextUtils.equals(group.getOwner(), getCurrentUser()) || group.getAdminList().contains(getCurrentUser())) {
                    manageGroups.add(group);
                }
            }
            // 对数据进行排序
            sortData(manageGroups);
            return manageGroups;
        }
        return new ArrayList<>();
    }

    /**
     * get all join groups, not contain manage groups
     * @return
     */
    public List<EMGroup> getAllJoinGroups(List<EMGroup> allGroups) {
        if(allGroups != null && allGroups.size() > 0) {
            List<EMGroup> joinGroups = new ArrayList<>();
            for (EMGroup group : allGroups) {
                if(!TextUtils.equals(group.getOwner(), getCurrentUser()) && !group.getAdminList().contains(getCurrentUser())) {
                    joinGroups.add(group);
                }
            }
            // 对数据进行排序
            sortData(joinGroups);
            return joinGroups;
        }
        return new ArrayList<>();
    }

    /**
     * 对数据进行排序
     * @param groups
     */
    private void sortData(List<EMGroup> groups) {
        Collections.sort(groups, new Comparator<EMGroup>() {
            @Override
            public int compare(EMGroup o1, EMGroup o2) {
                String name1 = EaseCommonUtils.getLetter(o1.getGroupName());
                String name2 = EaseCommonUtils.getLetter(o2.getGroupName());
                if(name1.equals(name2)){
                    return o1.getGroupId().compareTo(o2.getGroupId());
                }else{
                    if("#".equals(name1)){
                        return 1;
                    }else if("#".equals(name2)){
                        return -1;
                    }
                    return name1.compareTo(name2);
                }
            }
        });
    }
}
