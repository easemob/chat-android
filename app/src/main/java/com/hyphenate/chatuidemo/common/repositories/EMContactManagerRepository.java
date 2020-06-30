package com.hyphenate.chatuidemo.common.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EMContactManagerRepository extends BaseEMRepository{

    public LiveData<Resource<Boolean>> addContact(String username, String reason) {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                if(getCurrentUser().equalsIgnoreCase(username)) {
                    callBack.onError(ErrorCode.EM_ADD_SELF_ERROR);
                    return;
                }
                List<String> users = getUserDao().loadAllUsers();
                if(users != null && users.contains(username)) {
                    if(getContactManager().getBlackListUsernames().contains(username)) {
                        callBack.onError(ErrorCode.EM_FRIEND_BLACK_ERROR);
                        return;
                    }
                    callBack.onError(ErrorCode.EM_FRIEND_ERROR);
                    return;
                }
                getContactManager().aysncAddContact(username, reason, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(new MutableLiveData<>(true));
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

    public LiveData<Resource<List<EaseUser>>> getContactList() {
        return new NetworkBoundResource<List<EaseUser>, List<EaseUser>>() {

            @Override
            protected boolean shouldFetch(List<EaseUser> data) {
                return true;
            }

            @Override
            protected LiveData<List<EaseUser>> loadFromDb() {
                return getUserDao().loadUsers();
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                runOnIOThread(()-> {
                    try {
                        List<String> usernames = getContactManager().getAllContactsFromServer();
                        List<String> ids = getContactManager().getSelfIdsOnOtherPlatform();
                        if(usernames == null) {
                            usernames = new ArrayList<>();
                        }
                        if(ids != null && !ids.isEmpty()) {
                            usernames.addAll(ids);
                        }
                        List<EaseUser> easeUsers = EmUserEntity.parse(usernames);
                        if(easeUsers != null && !easeUsers.isEmpty()) {
                            List<String> blackListFromServer = getContactManager().getBlackListFromServer();
                            for (EaseUser user : easeUsers) {
                                if(blackListFromServer != null && !blackListFromServer.isEmpty()) {
                                    if(blackListFromServer.contains(user.getUsername())) {
                                        user.setContact(1);
                                    }
                                }
                            }
                        }
                        sortData(easeUsers);
                        callBack.onSuccess(createLiveData(easeUsers));

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }

            @Override
            protected void saveCallResult(List<EaseUser> items) {
                getUserDao().insert(EmUserEntity.parseList(items));
            }

        }.asLiveData();
    }

    private void sortData(List<EaseUser> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        Collections.sort(data, new Comparator<EaseUser>() {

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

    /**
     * 获取黑名单
     * @return
     */
    public LiveData<Resource<List<EaseUser>>> getBlackContactList() {
        return new NetworkBoundResource<List<EaseUser>, List<EaseUser>>() {
            @Override
            protected boolean shouldFetch(List<EaseUser> data) {
                return true;
            }

            @Override
            protected LiveData<List<EaseUser>> loadFromDb() {
                return getUserDao().loadBlackUsers();
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                getContactManager().aysncGetBlackListFromServer(new EMValueCallBack<List<String>>() {
                    @Override
                    public void onSuccess(List<String> value) {
                        List<EaseUser> users = EmUserEntity.parse(value);
                        if(users != null && !users.isEmpty()) {
                            for (EaseUser user : users) {
                                user.setContact(1);
                            }
                        }
                        callBack.onSuccess(createLiveData(users));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

            @Override
            protected void saveCallResult(List<EaseUser> items) {
                getUserDao().clearBlackUsers();
                getUserDao().insert(EmUserEntity.parseList(items));
            }

        }.asLiveData();
    }

    /**
     * 删除联系人
     * @param username
     * @return
     */
    public LiveData<Resource<Boolean>> deleteContact(String username) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getContactManager().asyncDeclineInvitation(username, new EMCallBack() {
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
     * 添加到黑名单
     * @param username
     * @param both 把用户加入黑民单时，如果是both双方发消息时对方都收不到；如果不是，
     *             则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
     * @return
     */
    public LiveData<Resource<Boolean>> addUserToBlackList(String username, boolean both) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getContactManager().aysncAddUserToBlackList(username, both, new EMCallBack() {
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
     * 移出黑名单
     * @param username
     * @return
     */
    public LiveData<Resource<Boolean>> removeUserFromBlackList(String username) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getContactManager().aysncRemoveUserFromBlackList(username, new EMCallBack() {
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
}
