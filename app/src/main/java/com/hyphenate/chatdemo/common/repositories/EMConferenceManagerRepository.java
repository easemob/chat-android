package com.hyphenate.chatdemo.common.repositories;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.chatdemo.section.chat.model.KV;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;

import java.util.ArrayList;
import java.util.List;

public class EMConferenceManagerRepository extends BaseEMRepository {


    public LiveData<Resource<List<KV<String, Integer>>>> getConferenceMembers(String groupId,String[] existMember) {
        return new NetworkOnlyResource<List<KV<String, Integer>>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<KV<String, Integer>>>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    List<String> contactList = new ArrayList<>();
                    if(TextUtils.isEmpty(groupId)) {
                        //从本地加载好友联系人
                        if(getUserDao() != null) {
                            contactList.addAll(getUserDao().loadContactUsers());
                        }
                    }else {
                        // 根据groupId获取群组中所有成员
                        contactList = new EMGroupManagerRepository().getAllGroupMemberByServer(groupId);
                    }
                    //获取管理员列表
                    try {
                        EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(groupId, true);
                        if(group != null) {
                            if(group.getAdminList() != null) {
                                contactList.addAll(group.getAdminList());
                            }
                            contactList.add(group.getOwner());
                        }

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    List<KV<String, Integer>> contacts = new ArrayList<>();
                    for (String it : contactList) {
                        if(!it.equals(DemoConstant.NEW_FRIENDS_USERNAME)
                            && !it.equals(DemoConstant.GROUP_USERNAME)
                                && !it.equals(DemoConstant.CHAT_ROOM)
                                && !it.equals(DemoConstant.CHAT_ROBOT)
                                && !it.equals(getCurrentUser())) {
                            if(memberContains(existMember,it)){
                                contacts.add(new KV<>(it, 2));
                            }else {
                                contacts.add(new KV<>(it, 0));
                            }
                        }
                    }
                    callBack.onSuccess(createLiveData(contacts));
                });

            }

        }.asLiveData();
    }


    private boolean memberContains(String[] existMember, String name) {
        if(existMember != null && existMember.length > 0){
            for (String userId : existMember) {
                if(TextUtils.equals(EasyUtils.useridFromJid(userId), name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
