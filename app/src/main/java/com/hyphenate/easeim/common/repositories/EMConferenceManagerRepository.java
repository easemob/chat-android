package com.hyphenate.easeim.common.repositories;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeim.section.chat.model.KV;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;

import java.util.ArrayList;
import java.util.List;

public class EMConferenceManagerRepository extends BaseEMRepository {

    public LiveData<Resource<List<KV<String, Integer>>>> getConferenceMembers(String groupId) {
        return new NetworkOnlyResource<List<KV<String, Integer>>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<KV<String, Integer>>>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    List<EMConferenceMember> existMembers = getConferenceManager().getConferenceMemberList();
                    List<String> contactList = new ArrayList<>();
                    if(TextUtils.isEmpty(groupId)) {
                        // 直接从本地加载所有的联系人
                        contactList.addAll(DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao().loadAllUsers());
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

                            if(memberContains(it, existMembers) != null) {
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

    private EMConferenceMember memberContains(String name, List<EMConferenceMember> existMembers) {
        for (EMConferenceMember item : existMembers) {
            if(TextUtils.equals(EasyUtils.useridFromJid(item.memberName), name)) {
                return item;
            }
        }
        return null;
    }
}
