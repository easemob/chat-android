package com.hyphenate.easeim.common.utils;


import com.hyphenate.util.EMLog;
import java.util.LinkedList;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/28/2021
 */

public class FetchUserInfoList {
    private static final String TAG = FetchUserInfoList.class.getSimpleName();
    private static LinkedList<String> fetchUsers;
    private static FetchUserInfoList mInstance;

    private FetchUserInfoList() {
        fetchUsers = new LinkedList<>();
        init();
    }


    public static synchronized FetchUserInfoList getInstance() {
        if (mInstance == null) {
            mInstance = new FetchUserInfoList();
        }
        return mInstance;
    }


    /**
     * 初始化
      */
   public void init(){
       synchronized (fetchUsers) {
           fetchUsers.clear();
       }
   }

    /**
     * 获取队列长度
     */
    public int getUserSize(){
        synchronized (fetchUsers) {
            return fetchUsers.size();
        }
    }

    /**
     * 入队列
     * @param userId
     */
   public void addUserId(String userId){
       synchronized (fetchUsers) {
           if (!fetchUsers.contains(userId)) {
               // 增加用户信息ID
               fetchUsers.addLast(userId);
               EMLog.i(TAG,"push addFetchUser userId:" + userId + "  size:" + fetchUsers.size());
           }else {
               EMLog.i(TAG,"current user is already in fetchUserList userId:" + userId);
           }
       }
    }

    /**
     * 出队列
     * @return
     */
    public String getUserId() {
        synchronized (fetchUsers) {
            if (fetchUsers.size() > 0) {
                String userId = fetchUsers.removeFirst();
                EMLog.i(TAG,"pop fetchUsers  UserId:" + userId
                        + " size:" + fetchUsers.size());
                return userId;
            }else{
                return null;
            }
        }
    }
}
