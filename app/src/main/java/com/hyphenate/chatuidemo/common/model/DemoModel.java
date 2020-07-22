package com.hyphenate.chatuidemo.common.model;

import android.content.Context;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.db.dao.AppKeyDao;
import com.hyphenate.chatuidemo.common.db.dao.EmUserDao;
import com.hyphenate.chatuidemo.common.db.entity.AppKeyEntity;
import com.hyphenate.chatuidemo.common.db.entity.EmUserEntity;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;
import com.hyphenate.chatuidemo.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.chatuidemo.common.manager.OptionsHelper;
import com.hyphenate.chatuidemo.common.utils.PreferenceManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EasePreferenceManager;

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
    
    public DemoModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }
    
    public boolean saveContactList(List<EaseUser> contactList) {
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
        List<EaseUser> users = dao.loadAllEaseUsers();
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
        Map<String, EaseUser> contactList = getContactList();
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
        List<AppKeyEntity> keys = dao.loadAllAppKeys();
        if(keys == null || keys.isEmpty()) {
            dao.insert(new AppKeyEntity(OptionsHelper.getInstance().getDefAppkey()));
            dao.insert(new AppKeyEntity(EMClient.getInstance().getOptions().getAppKey()));
        }
        keys = dao.loadAllAppKeys();
        return keys;
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
        List<AppKeyEntity> keys = dao.loadAllAppKeys();
        if(keys == null || keys.isEmpty()) {
            dao.insert(new AppKeyEntity(OptionsHelper.getInstance().getDefAppkey()));
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
            dbHelper.getInviteMessageDao().insert((InviteMessage) object);
        }else if(object instanceof MsgTypeManageEntity) {
            dbHelper.getMsgTypeManageDao().insert((MsgTypeManageEntity) object);
        }else if(object instanceof EmUserEntity) {
            dbHelper.getUserDao().insert((EmUserEntity) object);
        }
    }

    /**
     * update
     * @param object
     */
    public void update(Object object) {
        DemoDbHelper dbHelper = getDbHelper();
        if(object instanceof InviteMessage) {
            dbHelper.getInviteMessageDao().update((InviteMessage) object);
        }else if(object instanceof MsgTypeManageEntity) {
            dbHelper.getMsgTypeManageDao().update((MsgTypeManageEntity) object);
        }else if(object instanceof EmUserEntity) {
            dbHelper.getUserDao().insert((EmUserEntity) object);
        }
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

    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
