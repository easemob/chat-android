package com.hyphenate.chatuidemo.common.manager;

import com.hyphenate.chatuidemo.common.model.DemoServerSetBean;
import com.hyphenate.chatuidemo.common.utils.PreferenceManager;

public class OptionsHelper {
    private static final String DEF_APPKEY = "easemob-demo#chatdemoui";
    private static final String DEF_IM_SERVER = "msync-im1.sandbox.easemob.com";
    private static final int DEF_IM_PORT = 6717;
    private static final String DEF_REST_SERVER = "a1.sdb.easemob.com";

    private static OptionsHelper instance;

    private OptionsHelper(){}

    public static OptionsHelper getInstance() {
        if(instance == null) {
            synchronized (OptionsHelper.class) {
                if(instance == null) {
                    instance = new OptionsHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 自定义服务器是否可用
     * @return
     */
    public boolean isCustomServerEnable() {
        return PreferenceManager.getInstance().isCustomServerEnable();
    }

    /**
     * 这是自定义服务器是否可用
     * @param enable
     */
    public void enableCustomServer(boolean enable){
        PreferenceManager.getInstance().enableCustomServer(enable);
    }

    /**
     * 设置闲置服务器
     * @param restServer
     */
    public void setRestServer(String restServer){
        PreferenceManager.getInstance().setRestServer(restServer);
    }

    /**
     * 获取闲置服务器
     * @return
     */
    public String getRestServer(){
        return  PreferenceManager.getInstance().getRestServer();
    }

    /**
     * 设置IM服务器
     * @param imServer
     */
    public void setIMServer(String imServer){
        PreferenceManager.getInstance().setIMServer(imServer);
    }

    /**
     * 获取IM服务器
     * @return
     */
    public String getIMServer(){
        return PreferenceManager.getInstance().getIMServer();
    }

    /**
     * 设置端口号
     * @param port
     */
    public void setIMServerPort(int port) {
        PreferenceManager.getInstance().setIMServerPort(port);
    }

    public int getIMServerPort() {
        return PreferenceManager.getInstance().getIMServerPort();
    }

    /**
     * 设置自定义appkey是否可用
     * @param enable
     */
    public void enableCustomAppkey(boolean enable) {
        PreferenceManager.getInstance().enableCustomAppkey(enable);
    }

    /**
     * 获取自定义appkey是否可用
     * @return
     */
    public boolean isCustomAppkeyEnabled() {
        return PreferenceManager.getInstance().isCustomAppkeyEnabled();
    }

    /**
     * 设置自定义appkey
     * @param appkey
     */
    public void setCustomAppkey(String appkey) {
        PreferenceManager.getInstance().setCustomAppkey(appkey);
    }

    /**
     * 获取自定义appkey
     * @return
     */
    public String getCustomAppkey() {
        return PreferenceManager.getInstance().getCustomAppkey();
    }


    /**
     * 设置是否只使用Https
     * @param usingHttpsOnly
     */
    public void setUsingHttpsOnly(boolean usingHttpsOnly) {
        PreferenceManager.getInstance().setUsingHttpsOnly(usingHttpsOnly);
    }

    /**
     * 获取是否只使用Https
     * @return
     */
    public boolean getUsingHttpsOnly() {
        return PreferenceManager.getInstance().getUsingHttpsOnly();
    }

    /**
     * 设置是否允许聊天室owner离开并删除会话记录，意味着owner再不会受到任何消息
     * @param value
     */
    public void allowChatroomOwnerLeave(boolean value){
        PreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }

    /**
     * 获取聊天室owner离开时的设置
     * @return
     */
    public boolean isChatroomOwnerLeaveAllowed(){
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    /**
     * 设置退出(主动和被动退出)群组时是否删除聊天消息
     * @param value
     */
    public void setDeleteMessagesAsExitGroup(boolean value) {
        PreferenceManager.getInstance().setDeleteMessagesAsExitGroup(value);
    }

    /**
     * 获取退出(主动和被动退出)群组时是否删除聊天消息
     * @return
     */
    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }

    /**
     * 设置是否自动接受加群邀请
     * @param value
     */
    public void setAutoAcceptGroupInvitation(boolean value) {
        PreferenceManager.getInstance().setAutoAcceptGroupInvitation(value);
    }

    /**
     * 获取是否自动接受加群邀请
     * @return
     */
    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }

    /**
     * 设置是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @param value
     */
    public void setTransfeFileByUser(boolean value) {
        PreferenceManager.getInstance().setTransferFileByUser(value);
    }

    /**
     * 获取是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
     * @return
     */
    public boolean isSetTransferFileByUser() {
        return PreferenceManager.getInstance().isSetTransferFileByUser();
    }

    /**
     * 是否自动下载缩略图，默认是true为自动下载
     * @param autodownload
     */
    public void setAutodownloadThumbnail(boolean autodownload) {
        PreferenceManager.getInstance().setAudodownloadThumbnail(autodownload);
    }

    /**
     * 获取是否自动下载缩略图
     * @return
     */
    public boolean isSetAutodownloadThumbnail() {
        return PreferenceManager.getInstance().isSetAutodownloadThumbnail();
    }

    public String getDefAppkey() {
        return DEF_APPKEY;
    }

    public String getDefImServer() {
        return DEF_IM_SERVER;
    }

    public int getDefImPort() {
        return DEF_IM_PORT;
    }

    public String getDefRestServer() {
        return DEF_REST_SERVER;
    }

    /**
     * 获取服务设置
     * @return
     */
    public DemoServerSetBean getServerSet() {
        DemoServerSetBean bean = new DemoServerSetBean();
        bean.setAppkey(getCustomAppkey());
        bean.setCustomServerEnable(isCustomServerEnable());
        bean.setHttpsOnly(getUsingHttpsOnly());
        bean.setImServer(getIMServer());
        bean.setRestServer(getRestServer());
        return bean;
    }

    /**
     * 获取默认服务设置
     * @return
     */
    public DemoServerSetBean getDefServerSet() {
        DemoServerSetBean bean = new DemoServerSetBean();
        bean.setAppkey(getDefAppkey());
        bean.setRestServer(getDefRestServer());
        bean.setImServer(getDefImServer());
        bean.setImPort(getDefImPort());
        bean.setHttpsOnly(getUsingHttpsOnly());
        bean.setCustomServerEnable(isCustomServerEnable());
        return bean;
    }

}
