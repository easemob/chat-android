package com.hyphenate.easeim.common.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.db.dao.AppKeyDao;
import com.hyphenate.easeim.common.db.dao.EmUserDao;
import com.hyphenate.easeim.common.db.entity.AppKeyEntity;
import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeim.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.easeim.common.manager.OptionsHelper;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EasePreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DemoModel主要用于SP存取及一些数据库的存取
 */
public class DemoModel {
    EmUserDao dao = null;
    protected Context context = null;
    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();
    public List<EMChatRoom> chatRooms;

    //用户属性数据过期时间设置
    public static long userInfoTimeOut =  7 * 24 * 60 * 60 * 1000;
    
    public DemoModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }

    public long getUserInfoTimeOut() {
        return userInfoTimeOut;
    }

    public void setUserInfoTimeOut(long userInfoTimeOut) {
        if(userInfoTimeOut > 0){
            this.userInfoTimeOut = userInfoTimeOut;
        }
    }


    public boolean updateContactList(List<EaseUser> contactList) {
        List<EmUserEntity> userEntities = EmUserEntity.parseList(contactList);
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao != null) {
            dao.insert(userEntities);
            return true;
        }
        return false;
    }

    public Map<String, EaseUser> getContactList() {
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<EaseUser> users = dao.loadAllContactUsers();
        if(users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }


    public Map<String, EaseUser> getAllUserList() {
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<EaseUser> users = dao.loadAllEaseUsers();
        if(users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }


    public Map<String, EaseUser> getFriendContactList() {
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<EaseUser> users = dao.loadContacts();
        if(users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }

    /**
     * 判断是否是联系人
     * @param userId
     * @return
     */
    public boolean isContact(String userId) {
        Map<String, EaseUser> contactList = getFriendContactList();
        return contactList.keySet().contains(userId);
    }
    
    public void saveContact(EaseUser user){
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return;
        }
        dao.insert(EmUserEntity.parseParent(user));
    }

    public List<AppKeyEntity> getAppKeys() {
        AppKeyDao dao = DemoDbHelper.getInstance(context).getAppKeyDao();
        if(dao == null) {
            return new ArrayList<>();
        }
        String defAppkey = OptionsHelper.getInstance().getDefAppkey();
        String appKey = EMClient.getInstance().getOptions().getAppKey();
        if(!TextUtils.equals(defAppkey, appKey)) {
            List<AppKeyEntity> appKeys = dao.queryKey(appKey);
            if(appKeys == null || appKeys.isEmpty()) {
                dao.insert(new AppKeyEntity(appKey));
            }
        }
        return dao.loadAllAppKeys();
    }

    /**
     * 保存appKey
     * @param appKey
     */
    public void saveAppKey(String appKey) {
        AppKeyDao dao = DemoDbHelper.getInstance(context).getAppKeyDao();
        if(dao == null) {
            return;
        }
        AppKeyEntity entity = new AppKeyEntity(appKey);
        dao.insert(entity);
    }

    public void deleteAppKey(String appKey) {
        AppKeyDao dao = DemoDbHelper.getInstance(context).getAppKeyDao();
        if(dao == null) {
            return;
        }
        dao.deleteAppKey(appKey);
    }

    /**
     * get DemoDbHelper
     * @return
     */
    public DemoDbHelper getDbHelper() {
        return DemoDbHelper.getInstance(DemoApplication.getInstance());
    }

    /**
     * 向数据库中插入数据
     * @param object
     */
    public void insert(Object object) {
        DemoDbHelper dbHelper = getDbHelper();
        if(object instanceof InviteMessage) {
            if(dbHelper.getInviteMessageDao() != null) {
                dbHelper.getInviteMessageDao().insert((InviteMessage) object);
            }
        }else if(object instanceof MsgTypeManageEntity) {
            if(dbHelper.getMsgTypeManageDao() != null) {
                dbHelper.getMsgTypeManageDao().insert((MsgTypeManageEntity) object);
            }
        }else if(object instanceof EmUserEntity) {
            if(dbHelper.getUserDao() != null) {
                dbHelper.getUserDao().insert((EmUserEntity) object);
            }
        }
    }

    /**
     * update
     * @param object
     */
    public void update(Object object) {
        DemoDbHelper dbHelper = getDbHelper();
        if(object instanceof InviteMessage) {
            if(dbHelper.getInviteMessageDao() != null) {
                dbHelper.getInviteMessageDao().update((InviteMessage) object);
            }
        }else if(object instanceof MsgTypeManageEntity) {
            if(dbHelper.getMsgTypeManageDao() != null) {
                dbHelper.getMsgTypeManageDao().update((MsgTypeManageEntity) object);
            }
        }else if(object instanceof EmUserEntity) {
            if(dbHelper.getUserDao() != null) {
                dbHelper.getUserDao().insert((EmUserEntity) object);
            }
        }
    }


    /**
     * 查找有关用户用户属性过期的用户ID
     *
     */
    public List<String> selectTimeOutUsers() {
        DemoDbHelper dbHelper = getDbHelper();
        List<String> users = null;
        if(dbHelper.getUserDao() != null) {
            users = dbHelper.getUserDao().loadTimeOutEaseUsers(userInfoTimeOut,System.currentTimeMillis());
        }
        return users;
    }
    
    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsername(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }

    /**
     * 保存是否删除联系人的状态
     * @param username
     * @param isDelete
     */
    public void deleteUsername(String username, boolean isDelete) {
        SharedPreferences sp = context.getSharedPreferences("save_delete_username_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(username, isDelete);
        edit.commit();
    }

    /**
     * 查看联系人是否删除
     * @param username
     * @return
     */
    public boolean isDeleteUsername(String username) {
        SharedPreferences sp = context.getSharedPreferences("save_delete_username_status", Context.MODE_PRIVATE);
        return sp.getBoolean(username, false);
    }

    /**
     * 保存当前用户密码
     * 此处保存密码是为了查看多端设备登录是，调用接口不再输入用户名及密码，实际开发中，不可在本地保存密码！
     * 注：实际开发中不可进行此操作！！！
     * @param pwd
     */
    public void setCurrentUserPwd(String pwd) {
        PreferenceManager.getInstance().setCurrentUserPwd(pwd);
    }

    public String getCurrentUserPwd(){
        return PreferenceManager.getInstance().getCurrentUserPwd();
    }

    /**
     * 设置昵称
     * @param nickname
     */
    public void setCurrentUserNick(String nickname) {
        PreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    public String getCurrentUserNick() {
        return PreferenceManager.getInstance().getCurrentUserNick();
    }

    /**
     * 设置头像
     * @param avatar
     */
    private void setCurrentUserAvatar(String avatar) {
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    private String getCurrentUserAvatar() {
        return PreferenceManager.getInstance().getCurrentUserAvatar();
    }
    
    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {        
        Object val = valueCache.get(Key.SpakerOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }


    public void setDisabledGroups(List<String> groups){
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        List<String> list = new ArrayList<String>();
//        list.addAll(groups);
//        for(int i = 0; i < list.size(); i++){
//            if(EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))){
//                list.remove(i);
//                i--;
//            }
//        }
//
//        dao.setDisabledGroups(list);
//        valueCache.put(Key.DisabledGroups, list);
    }
    
    public List<String> getDisabledGroups(){
        Object val = valueCache.get(Key.DisabledGroups);

//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        if(val == null){
//            val = dao.getDisabledGroups();
//            valueCache.put(Key.DisabledGroups, val);
//        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setDisabledIds(List<String> ids){
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        dao.setDisabledIds(ids);
//        valueCache.put(Key.DisabledIds, ids);
    }
    
    public List<String> getDisabledIds(){
        Object val = valueCache.get(Key.DisabledIds);
        
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        if(val == null){
//            val = dao.getDisabledIds();
//            valueCache.put(Key.DisabledIds, val);
//        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setGroupsSynced(boolean synced){
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }
    
    public boolean isGroupsSynced(){
        return PreferenceManager.getInstance().isGroupsSynced();
    }
    
    public void setContactSynced(boolean synced){
        PreferenceManager.getInstance().setContactSynced(synced);
    }
    
    public boolean isContactSynced(){
        return PreferenceManager.getInstance().isContactSynced();
    }
    
    public void setBlacklistSynced(boolean synced){
        PreferenceManager.getInstance().setBlacklistSynced(synced);
    }
    
    public boolean isBacklistSynced(){
        return PreferenceManager.getInstance().isBacklistSynced();
    }
    

    public void setAdaptiveVideoEncode(boolean value) {
        PreferenceManager.getInstance().setAdaptiveVideoEncode(value);
    }
    
    public boolean isAdaptiveVideoEncode() {
        return PreferenceManager.getInstance().isAdaptiveVideoEncode();
    }

    public void setPushCall(boolean value) {
        PreferenceManager.getInstance().setPushCall(value);
    }

    public boolean isPushCall() {
        return PreferenceManager.getInstance().isPushCall();
    }

    public boolean isMsgRoaming() {
        return PreferenceManager.getInstance().isMsgRoaming();
    }

    public void setMsgRoaming(boolean roaming) {
        PreferenceManager.getInstance().setMsgRoaming(roaming);
    }

    public boolean isShowMsgTyping() {
        return PreferenceManager.getInstance().isShowMsgTyping();
    }

    public void showMsgTyping(boolean show) {
        PreferenceManager.getInstance().showMsgTyping(show);
    }

    /**
     * 获取默认的服务器设置
     * @return
     */
    public DemoServerSetBean getDefServerSet() {
        return OptionsHelper.getInstance().getDefServerSet();
    }

    /**
     * 设置是否使用google推送
     * @param useFCM
     */
    public void setUseFCM(boolean useFCM) {
        PreferenceManager.getInstance().setUseFCM(useFCM);
    }

    /**
     * 获取设置，是否设置google推送
     * @return
     */
    public boolean isUseFCM() {
        return PreferenceManager.getInstance().isUseFCM();
    }

    /**
     * 自定义服务器是否可用
     * @return
     */
    public boolean isCustomServerEnable() {
        return OptionsHelper.getInstance().isCustomServerEnable();
    }

    /**
     * 这是自定义服务器是否可用
     * @param enable
     */
    public void enableCustomServer(boolean enable){
        OptionsHelper.getInstance().enableCustomServer(enable);
    }

    /**
     * 自定义配置是否可用
     * @return
     */
    public boolean isCustomSetEnable() {
        return OptionsHelper.getInstance().isCustomSetEnable();
    }

    /**
     * 自定义配置是否可用
     * @param enable
     */
    public void enableCustomSet(boolean enable){
        OptionsHelper.getInstance().enableCustomSet(enable);
    }

    /**
     * 设置闲置服务器
     * @param restServer
     */
    public void setRestServer(String restServer){
        OptionsHelper.getInstance().setRestServer(restServer);
    }

    /**
     * 获取闲置服务器
     * @return
     */
    public String getRestServer(){
        return  OptionsHelper.getInstance().getRestServer();
    }

    /**
     * 设置IM服务器
     * @param imServer
     */
    public void setIMServer(String imServer){
        OptionsHelper.getInstance().setIMServer(imServer);
    }

    /**
     * 获取IM服务器
     * @return
     */
    public String getIMServer(){
        return OptionsHelper.getInstance().getIMServer();
    }

    /**
     * 设置端口号
     * @param port
     */
    public void setIMServerPort(int port) {
        OptionsHelper.getInstance().setIMServerPort(port);
    }

    public int getIMServerPort() {
        return OptionsHelper.getInstance().getIMServerPort();
    }

    /**
     * 设置自定义appkey是否可用
     * @param enable
     */
    public void enableCustomAppkey(boolean enable) {
        OptionsHelper.getInstance().enableCustomAppkey(enable);
    }

    /**
     * 获取自定义appkey是否可用
     * @return
     */
    public boolean isCustomAppkeyEnabled() {
        return OptionsHelper.getInstance().isCustomAppkeyEnabled();
    }

    /**
     * 设置自定义appkey
     * @param appkey
     */
    public void setCustomAppkey(String appkey) {
        OptionsHelper.getInstance().setCustomAppkey(appkey);
    }

    /**
     * 获取自定义appkey
     * @return
     */
    public String getCutomAppkey() {
        return OptionsHelper.getInstance().getCustomAppkey();
    }

    /**
     * 设置是否允许聊天室owner离开并删除会话记录，意味着owner再不会受到任何消息
     * @param value
     */
    public void allowChatroomOwnerLeave(boolean value){
        OptionsHelper.getInstance().allowChatroomOwnerLeave(value);
    }

    /**
     * 获取聊天室owner离开时的设置
     * @return
     */
    public boolean isChatroomOwnerLeaveAllowed(){
        return OptionsHelper.getInstance().isChatroomOwnerLeaveAllowed();
    }

    /**
     * 设置退出(主动和被动退出)群组时是否删除聊天消息
     * @param value
     */
    public void setDeleteMessagesAsExitGroup(boolean value) {
        OptionsHelper.getInstance().setDeleteMessagesAsExitGroup(value);
    }

    /**
     * 获取退出(主动和被动退出)群组时是否删除聊天消息
     * @return
     */
    public boolean isDeleteMessagesAsExitGroup() {
        return OptionsHelper.getInstance().isDeleteMessagesAsExitGroup();
    }

    /**
     * 设置退出（主动和被动）聊天室时是否删除聊天信息
     * @param value
     */
    public void setDeleteMessagesAsExitChatRoom(boolean value) {
        OptionsHelper.getInstance().setDeleteMessagesAsExitChatRoom(value);
    }

    /**
     * 获取退出(主动和被动退出)聊天室时是否删除聊天消息
     * @return
     */
    public boolean isDeleteMessagesAsExitChatRoom() {
        return OptionsHelper.getInstance().isDeleteMessagesAsExitChatRoom();
    }

    /**
     * 设置是否自动接受加群邀请
     * @param value
     */
    public void setAutoAcceptGroupInvitation(boolean value) {
        OptionsHelper.getInstance().setAutoAcceptGroupInvitation(value);
    }

    /**
     * 获取是否自动接受加群邀请
     * @return
     */
    public boolean isAutoAcceptGroupInvitation() {
        return OptionsHelper.getInstance().isAutoAcceptGroupInvitation();
    }

    /**
     * 设置是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @param value
     */
    public void setTransfeFileByUser(boolean value) {
        OptionsHelper.getInstance().setTransfeFileByUser(value);
    }

    /**
     * 获取是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @return
     */
    public boolean isSetTransferFileByUser() {
        return OptionsHelper.getInstance().isSetTransferFileByUser();
    }

    /**
     * 是否自动下载缩略图，默认是true为自动下载
     * @param autodownload
     */
    public void setAutodownloadThumbnail(boolean autodownload) {
        OptionsHelper.getInstance().setAutodownloadThumbnail(autodownload);
    }

    /**
     * 获取是否自动下载缩略图
     * @return
     */
    public boolean isSetAutodownloadThumbnail() {
        return OptionsHelper.getInstance().isSetAutodownloadThumbnail();
    }


    /**
     * 设置是否只使用Https
     * @param usingHttpsOnly
     */
    public void setUsingHttpsOnly(boolean usingHttpsOnly) {
        OptionsHelper.getInstance().setUsingHttpsOnly(usingHttpsOnly);
    }

    /**
     * 获取是否只使用Https
     * @return
     */
    public boolean getUsingHttpsOnly() {
        return OptionsHelper.getInstance().getUsingHttpsOnly();
    }

    public void setSortMessageByServerTime(boolean sortByServerTime) {
        OptionsHelper.getInstance().setSortMessageByServerTime(sortByServerTime);
    }

    public boolean isSortMessageByServerTime() {
        return OptionsHelper.getInstance().isSortMessageByServerTime();
    }

    /**
     * 是否允许token登录
     * @param isChecked
     */
    public void setEnableTokenLogin(boolean isChecked) {
        PreferenceManager.getInstance().setEnableTokenLogin(isChecked);
    }

    public boolean isEnableTokenLogin() {
        return PreferenceManager.getInstance().isEnableTokenLogin();
    }

    /**
     * 保存未发送的文本消息内容
     * @param toChatUsername
     * @param content
     */
    public void saveUnSendMsg(String toChatUsername, String content) {
        EasePreferenceManager.getInstance().saveUnSendMsgInfo(toChatUsername, content);
    }

    public String getUnSendMsg(String toChatUsername) {
        return EasePreferenceManager.getInstance().getUnSendMsgInfo(toChatUsername);
    }

    /**
     * 检查是否是第一次安装登录
     * 默认值是true, 需要在用api拉取完会话列表后，就其置为false.
     * @return
     */
    public boolean isFirstInstall() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_first_install", true);
    }

    /**
     * 将状态置为非第一次安装，在调用获取会话列表的api后调用
     * 并将会话列表是否来自服务器置为true
     */
    public void makeNotFirstInstall() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("is_first_install", false).apply();
        preferences.edit().putBoolean("is_conversation_come_from_server", true).apply();
    }

    /**
     * 检查会话列表是否从服务器返回数据
     * @return
     */
    public boolean isConComeFromServer() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_conversation_come_from_server", false);
    }

    /**
     * 将会话列表从服务器取数据的状态置为false，即后面应该采用本地数据库数据。
     */
    public void modifyConComeFromStatus() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("is_conversation_come_from_server", false).apply();
    }

    /**
     *  获取目标翻译语言
     */
    public String getTargetLanguage() {
        return PreferenceManager.getInstance().getTargetLanguage();
    }

    /**
     *  设置目标翻译语言
     */
    public void setTargetLanguage(String languageCode) {
        PreferenceManager.getInstance().setTargetLanguage(languageCode);
    }

    public boolean isDeveloperMode(){
        return PreferenceManager.getInstance().isDeveloperMode();
    }

    public void setDeveloperMode(boolean isDeveloper){
        PreferenceManager.getInstance().setDeveloperMode(isDeveloper);
    }

    public void setPhoneNumber(String phoneNumber){
        PreferenceManager.getInstance().setPhoneNumber(phoneNumber);
    }

    public String getPhoneNumber(){
        return PreferenceManager.getInstance().getPhoneNumber();
    }

    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
