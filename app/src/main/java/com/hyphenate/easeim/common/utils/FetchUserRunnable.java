package com.hyphenate.easeim.common.utils;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.livedatas.EaseLiveDataBus;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/28/2021
 */

public class FetchUserRunnable implements Runnable{

    private static final String TAG = FetchUserRunnable.class.getSimpleName();

    private FetchUserInfoList infoList;

    // 轮询时间
    private final int SLEEP_TIME = 1000;
    // 是否停止
    private volatile boolean isStop = false;

    public FetchUserRunnable() {
        infoList = FetchUserInfoList.getInstance();
    }

    @Override
    public void run() {
        while (!isStop) {
            int size = infoList.getUserSize();
            if (size > 0) {
                //判断长度是否大于100 最多能一次性获取100个用户属性
                if (size > 100) {
                    size = 100;
                }
                String[] userIds = new String[size];
                for (int i = 0; i < size; i++) {
                    userIds[i] = infoList.getUserId();
                }
                EMLog.i(TAG, "FetchUserRunnable exec  userId:" + userIds.toString());
                EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(userIds, new EMValueCallBack<Map<String, EMUserInfo>>() {
                    @Override
                    public void onSuccess(Map<String, EMUserInfo> userInfos) {
                        EMLog.i(TAG, "fetchUserInfoByUserId userInfo:" + userInfos.keySet().toString());
                        if (userInfos != null && userInfos.size() > 0) {
                            //更新本地数据库 同时刷新UI列表
                            warpEMUserInfo(userInfos);
                        } else {
                            EMLog.e(TAG, "fetchUserInfoByUserId userInfo is null");
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        EMLog.e(TAG, "fetchUserInfoByUserId  error" + error + "  errorMsg" + errorMsg);
                    }
                });
            } else {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (isStop) {
            //清空列表缓存
            infoList.init();
        }
    }

    /**
     * @param isStop
     *            the isStop to set
     */
    public void setStop(boolean isStop) {
        this.isStop = isStop;

    }

    private void warpEMUserInfo(Map<String, EMUserInfo> userInfos){
        Iterator<String> it_user = userInfos.keySet().iterator();
        List<EaseUser> userEntities = new ArrayList<>();
        boolean refreshContact = false;
        Map<String, EaseUser> exitUsers = DemoHelper.getInstance().getContactList();
        while(it_user.hasNext()) {
            String userId = it_user.next();
            EMUserInfo userInfo = userInfos.get(userId);
            if (userInfo != null) {
                EMLog.e(TAG, "start warpEMUserInfo userId:" + userInfo.getUserId());
                EaseUser userEntity = new EaseUser();
                userEntity.setUsername(userInfo.getUserId());
                userEntity.setNickname(userInfo.getNickName());
                userEntity.setEmail(userInfo.getEmail());
                userEntity.setAvatar(userInfo.getAvatarUrl());
                userEntity.setBirth(userInfo.getBirth());
                userEntity.setGender(userInfo.getGender());
                userEntity.setExt(userInfo.getExt());
                userEntity.setSign(userInfo.getSignature());
                EaseCommonUtils.setUserInitialLetter(userEntity);
                //判断当前更新的是否为好友关系
                if(exitUsers.containsKey(userInfo.getUserId())) {
                    EaseUser user = exitUsers.get(userInfo.getUserId());
                    if(user != null) {
                        if(user.getContact() == 0 || user.getContact() == 1) {
                            refreshContact = true;
                        }
                        userEntity.setContact(user.getContact());
                    }else {
                        userEntity.setContact(3);
                    }
                }else {
                    userEntity.setContact(3);
                }
                userEntities.add(userEntity);

                //通知callKit更新头像昵称
                EaseCallUserInfo info = new EaseCallUserInfo(userInfo.getNickName(),userInfo.getAvatarUrl());
                info.setUserId(userInfo.getUserId());
                EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO).postValue(info);
            }
        }

        //更新本地数据库信息
        DemoHelper.getInstance().updateUserList(userEntities);

        //更新本地联系人列表
        DemoHelper.getInstance().updateContactList();

        if(refreshContact) {
            //通知UI刷新列表
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_UPDATE, EaseEvent.TYPE.CONTACT);
            event.message = userInfos.keySet().toString();

            //发送联系人更新事件
            LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE).postValue(event);
        }
        EMLog.e(TAG," warpEMUserInfo userId:" + userInfos.keySet().toString() + "  end");
    }
}
