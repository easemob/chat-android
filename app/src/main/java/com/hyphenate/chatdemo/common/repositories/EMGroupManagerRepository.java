package com.hyphenate.chatdemo.common.repositories;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chatdemo.BuildConfig;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatdemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatdemo.common.net.ErrorCode;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.utils.GsonTools;
import com.hyphenate.chatdemo.section.group.MemberAttributeBean;
import com.hyphenate.cloud.HttpClientManager;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;
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
     * 获取所有群组列表
     * @param callBack
     */
    public void getAllGroups(ResultCallBack<List<EMGroup>> callBack) {
        if(!isLoggedIn()) {
            callBack.onError(ErrorCode.EM_NOT_LOGIN);
            return;
        }else {
            initDb();
        }
        getGroupManager().asyncGetJoinedGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
            @Override
            public void onSuccess(List<EMGroup> value) {
                if(callBack != null) {
                    callBack.onSuccess(value);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(callBack != null) {
                    callBack.onError(error, errorMsg);
                }
            }
        });
    }

    /**
     * 从服务器分页获取加入的群组
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public LiveData<Resource<List<EMGroup>>> getGroupListFromServer(int pageIndex, int pageSize) {
        return new NetworkOnlyResource<List<EMGroup>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EMGroup>>> callBack) {
                getGroupManager().asyncGetJoinedGroupsFromServer(pageIndex, pageSize, new EMValueCallBack<List<EMGroup>>() {
                    @Override
                    public void onSuccess(List<EMGroup> value) {
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
     * 获取公开群
     * @param pageSize
     * @param cursor
     * @return
     */
    public LiveData<Resource<EMCursorResult<EMGroupInfo>>> getPublicGroupFromServer(int pageSize, String cursor) {
        return new NetworkOnlyResource<EMCursorResult<EMGroupInfo>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMCursorResult<EMGroupInfo>>> callBack) {
                DemoHelper.getInstance().getGroupManager().asyncGetPublicGroupsFromServer(pageSize, cursor, new EMValueCallBack<EMCursorResult<EMGroupInfo>>() {
                    @Override
                    public void onSuccess(EMCursorResult<EMGroupInfo> value) {
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

    /**
     * 加入群组
     * @param group
     * @param reason
     * @return
     */
    public LiveData<Resource<Boolean>> joinGroup(EMGroup group, String reason) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                if(group.isMemberOnly()) {
                    getGroupManager().asyncApplyJoinToGroup(group.getGroupId(), reason, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            callBack.onSuccess(createLiveData(true));
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code,error);
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });
                }else {
                    getGroupManager().asyncJoinGroup(group.getGroupId(), new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            callBack.onSuccess(createLiveData(true));
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code,error);
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });
                }

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
                        if(members.size() < (value.getMemberCount() - value.getAdminList().size() - 1)) {
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
                        if(members.size() < (value.getMemberCount() - value.getAdminList().size() - 1)) {
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
     * 获取群组成员列表(不包含管理员和群主)
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
                runOnIOThread(()-> {
                    List<String> members = getAllGroupMemberByServer(groupId);
                    List<EaseUser> users = new ArrayList<>();
                    if(members != null && !members.isEmpty()){
                        for(int i = 0; i < members.size(); i++){
                            EaseUser user = DemoHelper.getInstance().getUserInfo(members.get(i));
                            if(user != null){
                                users.add(user);
                            }else{
                                EaseUser m_user = new EaseUser(members.get(i));
                                users.add(m_user);
                            }
                        }
                    }
                    sortUserData(users);
                    callBack.onSuccess(createLiveData(users));
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
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    Map<String, Long> map = null;
                    Map<String, Long> result = new HashMap<>();
                    int pageSize = 200;
                    do{
                        try {
                            map = getGroupManager().fetchGroupMuteList(groupId, 0, pageSize);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            callBack.onError(e.getErrorCode(), e.getMessage());
                            break;
                        }
                        if(map != null) {
                            result.putAll(map);
                        }
                    }while (map != null && map.size() >= 200);
                    callBack.onSuccess(createLiveData(result));
                });

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
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    List<String> list = null;
                    try {
                        list = fetchGroupBlacklistFromServer(groupId);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getMessage());
                        return;
                    }
                    if(list == null) {
                        list = new ArrayList<>();
                    }
                    callBack.onSuccess(createLiveData(list));
                });

            }

        }.asLiveData();
    }

    private List<String> fetchGroupBlacklistFromServer(String groupId) throws HyphenateException {
        int pageSize = 200;
        List<String> list = null;
        List<String> result = new ArrayList<>();
        do{
            list = getGroupManager().fetchGroupBlackList(groupId, 0, pageSize);
            if(list != null) {
                result.addAll(list);
            }
        }while (list != null && list.size() >= pageSize);
        return result;
    }

    /**
     * 获取群公告
     * @param groupId
     * @return
     */
    public LiveData<Resource<String>> getGroupAnnouncement(String groupId) {
        return new NetworkBoundResource<String, String>() {

            @Override
            protected boolean shouldFetch(String data) {
                return true;
            }

            @Override
            protected LiveData<String> loadFromDb() {
                String announcement = DemoHelper.getInstance().getGroupManager().getGroup(groupId).getAnnouncement();
                return createLiveData(announcement);
            }

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

            @Override
            protected void saveCallResult(String item) {

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

    /**
     * 设置群公告
     * @param groupId
     * @param announcement
     * @return
     */
    public LiveData<Resource<String>> setGroupAnnouncement(String groupId, String announcement) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncUpdateGroupAnnouncement(groupId, announcement, new EMCallBack() {
                    @Override
                    public void onSuccess() {
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

    /**
     * 设置我在群里的昵称
     * @param groupId
     * @param userId
     * @param attributeMap
     * @return
     */
    public LiveData<Resource<Map<String,MemberAttributeBean>>> setGroupMemberAttributes(String groupId,String userId,Map<String,String> attributeMap) {
        return new NetworkOnlyResource<Map<String,MemberAttributeBean>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Map<String,MemberAttributeBean>>> callBack) {
                getGroupManager().asyncSetGroupMemberAttributes(groupId, userId, attributeMap, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Map<String,MemberAttributeBean> result = new HashMap<>();
                        MemberAttributeBean bean = new MemberAttributeBean();

                        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
                            if(entry.getKey().equals("nickName")){
                                bean.setNickName(entry.getValue());
                                result.put(userId,bean);
                                DemoHelper.getInstance().saveMemberAttribute(groupId,userId,bean);
                            }
                        }
                        callBack.onSuccess(createLiveData(result));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 获取我在群里的属性
     * @param groupId
     * @param userId
     * @return
     */
    public LiveData<Resource<Map<String,MemberAttributeBean>>> fetchGroupMemberDetail(String groupId, String userId) {
        return new NetworkOnlyResource<Map<String,MemberAttributeBean>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Map<String,MemberAttributeBean>>> callBack) {
                getGroupManager().asyncFetchGroupMemberAllAttributes(groupId, userId, new EMValueCallBack<Map<String, Map<String, String>>>() {
                    @Override
                    public void onSuccess(Map<String, Map<String, String>> value) {
                        if (value != null){
                            Map<String,MemberAttributeBean> result = new HashMap<>();
                            Map<String, String> map = value.get(userId);
                            if (map != null){
                                MemberAttributeBean bean = GsonTools.changeGsonToBean(new JSONObject(map).toString(),MemberAttributeBean.class);
                                DemoHelper.getInstance().saveMemberAttribute(groupId,userId,bean);
                                result.put(userId,bean);
                                callBack.onSuccess(createLiveData(result));
                            }
                        }
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 获取多个成员在群里的昵称属性
     * @param groupId
     * @param userList
     * @return
     */
    public LiveData<Resource<Map<String,MemberAttributeBean>>> fetchGroupMemberDetail(String groupId,List<String> userList ) {
        return new NetworkOnlyResource<Map<String,MemberAttributeBean>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Map<String,MemberAttributeBean>>> callBack) {
                List<String> keyList = new ArrayList<>();
                keyList.add("nickName");
                Map<String,MemberAttributeBean> result = new HashMap<>();
                getGroupManager().asyncFetchGroupMembersAttributes(groupId, userList,keyList, new EMValueCallBack<Map<String, Map<String, String>>>() {
                    @Override
                    public void onSuccess(Map<String, Map<String, String>> value) {
                        if (value != null){
                            EMLog.d("apex-wt","fetchGroupMemberDetail : " + value);
                            for (String user : userList) {
                                Map<String,String> map = value.get(user);
                                if (map != null){
                                    MemberAttributeBean bean = GsonTools.changeGsonToBean(new JSONObject(map).toString(),MemberAttributeBean.class);
                                    DemoHelper.getInstance().saveMemberAttribute(groupId,user,bean);
                                    result.put(user,bean);
                                }
                            }
                            callBack.onSuccess(createLiveData(result));
                        }
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 设置群描述
     * @param groupId
     * @param description
     * @return
     */
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

    /**
     * 移交群主权限
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<Boolean>> changeOwner(String groupId, String username) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getGroupManager().asyncChangeOwner(groupId, username, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        callBack.onSuccess(createLiveData(true));
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
     * 设为群管理员
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<String>> addGroupAdmin(String groupId, String username) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncAddGroupAdmin(groupId, username, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        callBack.onSuccess(createLiveData(getContext().getString(R.string.demo_group_member_add_admin, username)));
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
     * 移除群管理员
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<String>> removeGroupAdmin(String groupId, String username) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncRemoveGroupAdmin(groupId, username, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        callBack.onSuccess(createLiveData(getContext().getString(R.string.demo_group_member_remove_admin, username)));
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
     * 移出群
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<String>> removeUserFromGroup(String groupId, String username) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncRemoveUserFromGroup(groupId, username, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(getContext().getString(R.string.demo_group_member_remove, username)));
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
     * 添加到群黑名单
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<String>> blockUser(String groupId, String username) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncBlockUser(groupId, username, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(getContext().getString(R.string.demo_group_member_add_black, username)));
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
     * 移出群黑名单
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<String>> unblockUser(String groupId, String username) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncUnblockUser(groupId, username, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(getContext().getString(R.string.demo_group_member_remove_black, username)));
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
     * 禁言
     * @param groupId
     * @param usernames
     * @return
     */
    public LiveData<Resource<String>> muteGroupMembers(String groupId, List<String> usernames, long duration) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncMuteGroupMembers(groupId, usernames, duration, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        callBack.onSuccess(createLiveData(getContext().getString(R.string.demo_group_member_mute, usernames.get(0))));
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
     * 禁言
     * @param groupId
     * @param usernames
     * @return
     */
    public LiveData<Resource<String>> unMuteGroupMembers(String groupId, List<String> usernames) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getGroupManager().asyncUnMuteGroupMembers(groupId, usernames, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        callBack.onSuccess(createLiveData(getContext().getString(R.string.demo_group_member_remove_mute, usernames.get(0))));
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
     * 退群
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> leaveGroup(String groupId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getGroupManager().asyncLeaveGroup(groupId, new EMCallBack() {
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
     * 解散群
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> destroyGroup(String groupId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getGroupManager().asyncDestroyGroup(groupId, new EMCallBack() {
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
     * create a new group
     * @param groupName
     * @param desc
     * @param allMembers
     * @param reason
     * @param option
     * @return
     */
    public LiveData<Resource<EMGroup>> createGroup(String groupName, String desc, String[] allMembers, String reason, EMGroupOptions option) {
        return new NetworkOnlyResource<EMGroup>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMGroup>> callBack) {
                getGroupManager().asyncCreateGroup(groupName, desc, allMembers, reason, option, new EMValueCallBack<EMGroup>() {
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

    /**
     * 屏蔽群消息
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> blockGroupMessage(String groupId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getGroupManager().asyncBlockGroupMessage(groupId, new EMCallBack() {
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
     * 取消屏蔽群消息
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> unblockGroupMessage(String groupId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getGroupManager().asyncUnblockGroupMessage(groupId, new EMCallBack() {
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
     * 专用于本app上报群ID给App server使用
     * @param group
     * @return
     */
    public LiveData<Resource<EMGroup>> reportGroupIdToServer(EMGroup group) {
        return new NetworkOnlyResource<EMGroup>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMGroup>> callBack) {
                runOnIOThread(()-> {
                    String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + "/inside/app/group/" + group.getGroupId() + "?appkey=" + EMClient.getInstance().getOptions().getAppKey();
                    EMLog.e("group", "reportGroupIdToServer url: "+url);
                    try {
                        Pair<Integer, String> result = HttpClientManager.sendPostRequest(url, null,null);
                        if(result == null) {
                            callBack.onError(EMError.NETWORK_ERROR);
                        }else {
                            int code = result.first;
                            if(code >= 200 && code <= 299) {
                                callBack.onSuccess(createLiveData(group));
                            }else {
                                callBack.onError(EMError.NETWORK_ERROR);
                            }
                        }
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(EMError.NETWORK_ERROR);
                    }
                });
            }
        }.asLiveData();
    }

}
