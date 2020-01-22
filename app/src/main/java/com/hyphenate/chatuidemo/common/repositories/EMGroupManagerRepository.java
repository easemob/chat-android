package com.hyphenate.chatuidemo.common.repositories;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 获取群组信息
     * @param groupId
     * @return
     */
    public LiveData<Resource<EMGroup>> getGroupFromServer(String groupId) {
        return new NetworkOnlyResource<EMGroup>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMGroup>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                DemoHelper.getInstance().getGroupManager().asyncGetGroupFromServer(groupId, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<String>>> getGroupMembersByName(String groupId) {
        return new NetworkOnlyResource<List<String>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                DemoHelper.getInstance().getGroupManager().asyncGetGroupFromServer(groupId, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        List<String> members = value.getMembers();
                        if(members.size() < value.getMemberCount()) {
                            members = getAllGroupMemberByServer(groupId);
                        }
                        members.addAll(value.getAdminList());
                        members.add(value.getOwner());
                        if(!members.isEmpty()) {
                            callBack.onSuccess(createLiveData(members));
                        }else {
                            callBack.onError(ErrorCode.EM_ERR_GROUP_NO_MEMBERS);
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

    /**
     * 获取群组成员列表(包含管理员和群主)
     * @param groupId
     * @return
     */
    public LiveData<Resource<List<EaseUser>>> getGroupAllMembers(String groupId) {
        return new NetworkOnlyResource<List<EaseUser>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                DemoHelper.getInstance().getGroupManager().asyncGetGroupFromServer(groupId, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        List<String> members = value.getMembers();
                        if(members.size() < value.getMemberCount()) {
                            members = getAllGroupMemberByServer(groupId);
                        }
                        members.addAll(value.getAdminList());
                        members.add(value.getOwner());
                        if(!members.isEmpty()) {
                            List<EaseUser> users = EmUserEntity.parse(members);
                            sortUserData(users);
                            callBack.onSuccess(createLiveData(users));
                        }else {
                            callBack.onError(ErrorCode.EM_ERR_GROUP_NO_MEMBERS);
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

    /**
     * 获取群组成员列表(包含管理员和群主)
     * @param groupId
     * @return
     */
    public LiveData<Resource<List<EaseUser>>> getGroupMembers(String groupId) {
        return new NetworkOnlyResource<List<EaseUser>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                DemoHelper.getInstance().getGroupManager().asyncGetGroupFromServer(groupId, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        List<String> members = value.getMembers();
                        if(members.size() < value.getMemberCount()) {
                            members = getAllGroupMemberByServer(groupId);
                        }
                        if(!members.isEmpty()) {
                            List<EaseUser> users = EmUserEntity.parse(members);
                            sortUserData(users);
                            callBack.onSuccess(createLiveData(users));
                        }else {
                            callBack.onError(ErrorCode.EM_ERR_GROUP_NO_MEMBERS);
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

    /**
     * 获取禁言列表
     * @param groupId
     * @return
     */
    public LiveData<Resource<Map<String, Long>>> getGroupMuteMap(String groupId) {
        return new NetworkOnlyResource<Map<String, Long>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Map<String, Long>>> callBack) {
                Map<String, Long> map = null;
                Map<String, Long> result = new HashMap<>();
                int pageSize = 200;
                do{
                    try {
                        map = getGroupManager().fetchGroupMuteList(groupId, 0, pageSize);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    if(map != null) {
                        result.putAll(map);
                    }
                }while (map == null || map.isEmpty() || map.size() < 200);
                callBack.onSuccess(createLiveData(result));
            }

        }.asLiveData();
    }

    /**
     * 获取群组黑名单列表
     * @param groupId
     * @return
     */
    public LiveData<Resource<List<String>>> getGroupBlackList(String groupId) {
        return new NetworkOnlyResource<List<String>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                List<String> list = null;
                List<String> result = new ArrayList<>();
                int pageSize = 200;
                do{
                    try {
                        list = getGroupManager().fetchGroupBlackList(groupId, 0, pageSize);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    if(list != null) {
                        result.addAll(list);
                    }
                }while (list == null || list.isEmpty() || list.size() < 200);
                callBack.onSuccess(createLiveData(result));
            }

        }.asLiveData();
    }

    /**
     * 获取群公告
     * @param groupId
     * @return
     */
    public LiveData<Resource<String>> getGroupAnnouncement(String groupId) {
        return new NetworkOnlyResource<String>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncFetchGroupAnnouncement(groupId, new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

        }.asLiveData();
    }

    /**
     * 获取所有成员
     * @param groupId
     * @return
     */
    public List<String> getAllGroupMemberByServer(String groupId) {
        // 根据groupId获取群组中所有成员
        List<String> contactList = new ArrayList<>();
        EMCursorResult<String> result = null;
        do {
            try {
                result = getGroupManager().fetchGroupMembers(groupId, result != null ? result.getCursor() : "", 20);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            if(result != null) {
                contactList.addAll(result.getData());
            }
        } while (result != null && !TextUtils.isEmpty(result.getCursor()));
        return contactList;
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

    /**
     * 设置群组名称
     * @param groupId
     * @param groupName
     * @return
     */
    public LiveData<Resource<String>> setGroupName(String groupId, String groupName) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncChangeGroupName(groupId, groupName, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(groupName));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code,  error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    public LiveData<Resource<String>> setGroupAnnouncement(String groupId, String announcement) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncUpdateGroupAnnouncement(groupId, announcement, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e("TAG", "setGroupAnnouncement success");
                        callBack.onSuccess(createLiveData(announcement));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    public LiveData<Resource<String>> setGroupDescription(String groupId, String description) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncChangeGroupDescription(groupId, description, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(description));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 获取共享文件
     * @param groupId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public LiveData<Resource<List<EMMucSharedFile>>> getSharedFiles(String groupId, int pageNum, int pageSize) {
        return new NetworkOnlyResource<List<EMMucSharedFile>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EMMucSharedFile>>> callBack) {
                getGroupManager().asyncFetchGroupSharedFileList(groupId, pageNum, pageSize, new EMValueCallBack<List<EMMucSharedFile>>() {
                    @Override
                    public void onSuccess(List<EMMucSharedFile> value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 下载共享文件
     * @param groupId
     * @param fileId
     * @param localFile
     * @return
     */
    public LiveData<Resource<File>> downloadFile(String groupId, String fileId, File localFile) {
        return new NetworkOnlyResource<File>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<File>> callBack) {
                getGroupManager().asyncDownloadGroupSharedFile(groupId, fileId, localFile.getAbsolutePath(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(localFile));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 删除服务器端的文件
     * @param groupId
     * @param fileId
     * @return
     */
    public LiveData<Resource<Boolean>> deleteFile(String groupId, String fileId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getGroupManager().asyncDeleteGroupSharedFile(groupId, fileId, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 上传文件
     * @param groupId
     * @param filePath
     * @return
     */
    public LiveData<Resource<Boolean>> uploadFile(String groupId, String filePath) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getGroupManager().asyncUploadGroupSharedFile(groupId, filePath, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 邀请群成员
     * @param isOwner
     * @param groupId
     * @param members
     * @return
     */
    public LiveData<Resource<Boolean>> addMembers(boolean isOwner, String groupId, String[] members) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                if(isOwner) {
                    getGroupManager().asyncAddUsersToGroup(groupId, members, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            callBack.onSuccess(createLiveData(true));
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });
                }else {
                    getGroupManager().asyncInviteUser(groupId, members, null, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            callBack.onSuccess(createLiveData(true));
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });
                }
            }
        }.asLiveData();
    }
}
