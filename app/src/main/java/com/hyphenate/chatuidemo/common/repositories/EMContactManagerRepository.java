package com.hyphenate.chatuidemo.common.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.hyphenate.EMCallBack;
import com.hyphenate.chatuidemo.DemoApp;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class EMContactManagerRepository {

    public LiveData<Resource<Boolean>> addContact(String username, String reason) {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                if(DemoHelper.getInstance().getEMClient().getCurrentUser().equalsIgnoreCase(username)) {
                    callBack.onError(ErrorCode.EM_ADD_SELF_ERROR);
                    return;
                }
                List<String> users = DemoDbHelper.getInstance(DemoApp.getInstance()).getUserDao().loadAllUsers();
                if(users != null && users.contains(username)) {
                    if(DemoHelper.getInstance().getContactManager().getBlackListUsernames().contains(username)) {
                        callBack.onError(ErrorCode.EM_FRIEND_BLACK_ERROR);
                        return;
                    }
                    callBack.onError(ErrorCode.EM_FRIEND_ERROR);
                    return;
                }
                DemoHelper.getInstance().getContactManager().aysncAddContact(username, reason, new EMCallBack() {
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
                return DemoDbHelper.getInstance(DemoApp.getInstance()).getUserDao().loadUsers();
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                ThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        List<String> usernames = DemoHelper.getInstance().getContactManager().getAllContactsFromServer();
                        List<String> ids = DemoHelper.getInstance().getContactManager().getSelfIdsOnOtherPlatform();
                        if(usernames == null) {
                            usernames = new ArrayList<>();
                        }
                        if(ids != null && !ids.isEmpty()) {
                            usernames.addAll(ids);
                        }
                        callBack.onSuccess(new MutableLiveData<>(EmUserEntity.parse(usernames)));

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }

            @Override
            protected void saveCallResult(List<EaseUser> items) {
                DemoDbHelper.getInstance(DemoApp.getInstance()).getUserDao().insert(EmUserEntity.parseList(items));
            }

        }.asLiveData();
    }
}
