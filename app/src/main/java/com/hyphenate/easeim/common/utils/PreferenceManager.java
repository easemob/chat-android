/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeim.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
	/**
	 * name of preference
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceManager mPreferencemManager;
	private static SharedPreferences.Editor editor;

	private String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
	private String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
	private String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
	private String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";

	private static String SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE = "shared_key_setting_chatroom_owner_leave";
    private static String SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP = "shared_key_setting_delete_messages_when_exit_group";
	private static String SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_CHAT_ROOM = "shared_key_setting_delete_messages_when_exit_chat_room";
	private static String SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER = "shared_key_setting_transfer_file_by_user";
	private static String SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL = "shared_key_setting_autodownload_thumbnail";
	private static String SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION = "shared_key_setting_auto_accept_group_invitation";
    private static String SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE = "shared_key_setting_adaptive_video_encode";
	private static String SHARED_KEY_SETTING_OFFLINE_PUSH_CALL = "shared_key_setting_offline_push_call";
	private static String SHARED_KEY_SETTING_RECORD_ON_SERVER = "shared_key_setting_record_on_server";
	private static String SHARED_KEY_SETTING_MERGE_STREAM = "shared_key_setting_merge_stream";
	private static String SHARED_KEY_SETTING_OFFLINE_LARGE_CONFERENCE_MODE = "shared_key_setting_offline_large_conference_mode";

	private static String SHARED_KEY_SETTING_GROUPS_SYNCED = "SHARED_KEY_SETTING_GROUPS_SYNCED";
	private static String SHARED_KEY_SETTING_CONTACT_SYNCED = "SHARED_KEY_SETTING_CONTACT_SYNCED";
	private static String SHARED_KEY_SETTING_BALCKLIST_SYNCED = "SHARED_KEY_SETTING_BALCKLIST_SYNCED";

	private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
	private static String SHARED_KEY_CURRENTUSER_USER_PASSWORD = "SHARED_KEY_CURRENTUSER_USER_PASSWORD";
	private static String SHARED_KEY_CURRENTUSER_NICK = "SHARED_KEY_CURRENTUSER_NICK";
	private static String SHARED_KEY_CURRENTUSER_AVATAR = "SHARED_KEY_CURRENTUSER_AVATAR";

	private static String SHARED_KEY_REST_SERVER = "SHARED_KEY_REST_SERVER";
	private static String SHARED_KEY_IM_SERVER = "SHARED_KEY_IM_SERVER";
	private static String SHARED_KEY_IM_SERVER_PORT = "SHARED_KEY_IM_SERVER_PORT";
	private static String SHARED_KEY_ENABLE_CUSTOM_SERVER = "SHARED_KEY_ENABLE_CUSTOM_SERVER";
	private static String SHARED_KEY_ENABLE_CUSTOM_SET = "SHARED_KEY_ENABLE_CUSTOM_SET";
	private static String SHARED_KEY_ENABLE_CUSTOM_APPKEY = "SHARED_KEY_ENABLE_CUSTOM_APPKEY";
	private static String SHARED_KEY_CUSTOM_APPKEY = "SHARED_KEY_CUSTOM_APPKEY";
	private static String SHARED_KEY_MSG_ROAMING = "SHARED_KEY_MSG_ROAMING";
	private static String SHARED_KEY_SHOW_MSG_TYPING = "SHARED_KEY_SHOW_MSG_TYPING";

	private static String SHARED_KEY_CALL_MIN_VIDEO_KBPS = "SHARED_KEY_CALL_MIN_VIDEO_KBPS";
	private static String SHARED_KEY_CALL_MAX_VIDEO_KBPS = "SHARED_KEY_CALL_Max_VIDEO_KBPS";
	private static String SHARED_KEY_CALL_MAX_FRAME_RATE = "SHARED_KEY_CALL_MAX_FRAME_RATE";
	private static String SHARED_KEY_CALL_AUDIO_SAMPLE_RATE = "SHARED_KEY_CALL_AUDIO_SAMPLE_RATE";
	private static String SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION = "SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION";
	private static String SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION = "SHARED_KEY_FRONT_CAMERA_RESOLUTIOIN";
	private static String SHARED_KEY_CALL_FIX_SAMPLE_RATE = "SHARED_KEY_CALL_FIX_SAMPLE_RATE";

	private static String SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION = "SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION";
	private static String SHARED_KEY_WATER_MARK_RESOLUTION = "SHARED_KEY_WATER_MARK_RESOLUTION";

	private static String SHARED_KEY_PUSH_USE_FCM = "shared_key_push_use_fcm";
	private static String SHARED_KEY_AUTO_LOGIN = "shared_key_auto_login";
	private static String SHARED_KEY_HTTPS_ONLY = "shared_key_https_only";
	private static String SHARED_KEY_SORT_MESSAGE_BY_SERVER_TIME = "sort_message_by_server_time";

	private static String SHARED_KEY_ENABLE_TOKEN_LOGIN = "enable_token_login";

	private static String SHARED_KEY_TARGET_LANGUAGE = "shared_key_target_language";

	@SuppressLint("CommitPrefEdits")
	private PreferenceManager(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	public static synchronized void init(Context cxt){
	    if(mPreferencemManager == null){
	        mPreferencemManager = new PreferenceManager(cxt);
	    }
	}

	/**
	 * get instance of PreferenceManager
	 *
	 * @param
	 * @return
	 */
	public synchronized static PreferenceManager getInstance() {
		if (mPreferencemManager == null) {
			throw new RuntimeException("please init first!");
		}

		return mPreferencemManager;
	}

	public void setSettingMsgNotification(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
		editor.apply();
	}

	public boolean getSettingMsgNotification() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
	}

	public void setSettingMsgSound(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean);
		editor.apply();
	}

	public boolean getSettingMsgSound() {

		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true);
	}

	public void setSettingMsgVibrate(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
		editor.apply();
	}

	public boolean getSettingMsgVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
	}

	public void setSettingMsgSpeaker(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean);
		editor.apply();
	}

	public boolean getSettingMsgSpeaker() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SPEAKER, true);
	}

	public void setSettingAllowChatroomOwnerLeave(boolean value) {
        editor.putBoolean(SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE, value);
        editor.apply();
    }

	public boolean getSettingAllowChatroomOwnerLeave() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE, true);
    }

    public void setDeleteMessagesAsExitGroup(boolean value){
        editor.putBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, value);
        editor.apply();
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, true);
    }

	public void setDeleteMessagesAsExitChatRoom(boolean value){
		editor.putBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_CHAT_ROOM, value);
		editor.apply();
	}

	public boolean isDeleteMessagesAsExitChatRoom() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_CHAT_ROOM, true);
	}

	public void setTransferFileByUser(boolean value) {
		editor.putBoolean(SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER, value);
		editor.apply();
	}

	public boolean isSetTransferFileByUser() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER, true);
	}
	public void setAudodownloadThumbnail(boolean autodownload) {
		editor.putBoolean(SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL, autodownload);
		editor.apply();
	}

	public boolean isSetAutodownloadThumbnail() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL, true);
	}

	public void setAutoAcceptGroupInvitation(boolean value) {
        editor.putBoolean(SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, value);
        editor.commit();
    }

    public boolean isAutoAcceptGroupInvitation() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, true);
    }

    public void setAdaptiveVideoEncode(boolean value) {
        editor.putBoolean(SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, value);
        editor.apply();
    }

    public boolean isAdaptiveVideoEncode() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, false);
    }

	public void setPushCall(boolean value) {
		editor.putBoolean(SHARED_KEY_SETTING_OFFLINE_PUSH_CALL, value);
		editor.apply();
	}

	public boolean isPushCall() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_OFFLINE_PUSH_CALL, true);
	}

	public void setRecordOnServer(boolean value) {
		editor.putBoolean(SHARED_KEY_SETTING_RECORD_ON_SERVER, value);
		editor.apply();
	}

	public boolean isRecordOnServer() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_RECORD_ON_SERVER, false);
	}

	public void setMergeStream(boolean value) {
		editor.putBoolean(SHARED_KEY_SETTING_MERGE_STREAM, value);
		editor.apply();
	}

	public boolean isMergeStream() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_MERGE_STREAM, false);
	}

	public void setGroupsSynced(boolean synced){
	    editor.putBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, synced);
        editor.apply();
	}

	public boolean isGroupsSynced(){
	    return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, false);
	}

	public void setContactSynced(boolean synced){
        editor.putBoolean(SHARED_KEY_SETTING_CONTACT_SYNCED, synced);
        editor.apply();
    }

    public boolean isContactSynced(){
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_CONTACT_SYNCED, false);
    }

    public void setBlacklistSynced(boolean synced){
        editor.putBoolean(SHARED_KEY_SETTING_BALCKLIST_SYNCED, synced);
        editor.apply();
    }

    public boolean isBacklistSynced(){
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_BALCKLIST_SYNCED, false);
    }

	public void setCurrentUserNick(String nick) {
		editor.putString(SHARED_KEY_CURRENTUSER_NICK, nick);
		editor.apply();
	}

	public void setCurrentUserAvatar(String avatar) {
		editor.putString(SHARED_KEY_CURRENTUSER_AVATAR, avatar);
		editor.apply();
	}

	public String getCurrentUserNick() {
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_NICK, null);
	}

	public String getCurrentUserAvatar() {
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_AVATAR, null);
	}

	public void setCurrentUserName(String username){
		editor.putString(SHARED_KEY_CURRENTUSER_USERNAME, username);
		editor.apply();
	}

	public String getCurrentUsername(){
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USERNAME, null);
	}

	public void setCurrentUserPwd(String pwd) {
		editor.putString(SHARED_KEY_CURRENTUSER_USER_PASSWORD, pwd);
		editor.apply();
	}

	public String getCurrentUserPwd(){
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USER_PASSWORD, null);
	}

	public void setRestServer(String restServer){
		editor.putString(SHARED_KEY_REST_SERVER, restServer).commit();
		editor.commit();
	}

	public String getRestServer(){
		return mSharedPreferences.getString(SHARED_KEY_REST_SERVER, null);
	}

	public void setIMServer(String imServer){
		editor.putString(SHARED_KEY_IM_SERVER, imServer);
		editor.commit();
	}

	public String getIMServer(){
		return mSharedPreferences.getString(SHARED_KEY_IM_SERVER, null);
	}

	public void enableCustomServer(boolean enable){
		editor.putBoolean(SHARED_KEY_ENABLE_CUSTOM_SERVER, enable);
		editor.commit();
	}

	public boolean isCustomServerEnable(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_CUSTOM_SERVER, false);
	}

	public boolean isCustomSetEnable(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_CUSTOM_SET, false);
	}

	public void enableCustomSet(boolean enable){
		editor.putBoolean(SHARED_KEY_ENABLE_CUSTOM_SET, enable);
		editor.commit();
	}

	public void enableCustomAppkey(boolean enable) {
		editor.putBoolean(SHARED_KEY_ENABLE_CUSTOM_APPKEY, enable);
		editor.commit();
	}

	public boolean isCustomAppkeyEnabled() {
		return mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_CUSTOM_APPKEY, true);
	}

	public String getCustomAppkey() {
		return mSharedPreferences.getString(SHARED_KEY_CUSTOM_APPKEY, "");
	}

	public void setCustomAppkey(String appkey) {
		editor.putString(SHARED_KEY_CUSTOM_APPKEY, appkey);
		editor.commit();
	}

	public void removeCurrentUserInfo() {
		editor.remove(SHARED_KEY_CURRENTUSER_NICK);
		editor.remove(SHARED_KEY_CURRENTUSER_AVATAR);
		editor.apply();
	}

	public boolean isMsgRoaming() {
		return mSharedPreferences.getBoolean(SHARED_KEY_MSG_ROAMING, false);
	}

	public void setMsgRoaming(boolean isRoaming) {
		editor.putBoolean(SHARED_KEY_MSG_ROAMING, isRoaming);
		editor.apply();
	}

	public boolean isShowMsgTyping() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SHOW_MSG_TYPING, false);
	}

	public void showMsgTyping(boolean show) {
		editor.putBoolean(SHARED_KEY_SHOW_MSG_TYPING, show);
		editor.apply();
	}

	/**
	 * 设置是否自动登录,只有登录成功后，此值才能设置为true
	 * @param autoLogin
	 */
	public void setAutoLogin(boolean autoLogin) {
		editor.putBoolean(SHARED_KEY_AUTO_LOGIN, autoLogin);
		editor.commit();
	}

	/**
	 * 获取是否是自动登录
	 * @return
	 */
	public boolean getAutoLogin() {
		return mSharedPreferences.getBoolean(SHARED_KEY_AUTO_LOGIN, false);
	}

	/**
	 * using Https only
	 * @param usingHttpsOnly
	 */
	public void setUsingHttpsOnly(boolean usingHttpsOnly) {
		editor.putBoolean(SHARED_KEY_HTTPS_ONLY, usingHttpsOnly);
		editor.commit();
	}

	/**
	 * get if using Https only
	 * @return
	 */
	public boolean getUsingHttpsOnly() {
		return mSharedPreferences.getBoolean(SHARED_KEY_HTTPS_ONLY, false);
	}

	/**
	 * ----------------------------------------- Call Option -----------------------------------------
	 */

	/**
	 * Min Video kbps
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMinVideoKbps() {
		return mSharedPreferences.getInt(SHARED_KEY_CALL_MIN_VIDEO_KBPS, -1);
	}

	public void setCallMinVideoKbps(int minBitRate) {
		editor.putInt(SHARED_KEY_CALL_MIN_VIDEO_KBPS, minBitRate);
		editor.apply();
	}

	/**
	 * Max Video kbps
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMaxVideoKbps() {
		return mSharedPreferences.getInt(SHARED_KEY_CALL_MAX_VIDEO_KBPS, -1);
	}

	public void setCallMaxVideoKbps(int maxBitRate) {
		editor.putInt(SHARED_KEY_CALL_MAX_VIDEO_KBPS, maxBitRate);
		editor.apply();
	}

	/**
	 * Max frame rate
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMaxFrameRate() {
		return mSharedPreferences.getInt(SHARED_KEY_CALL_MAX_FRAME_RATE, -1);
	}

	public void setCallMaxFrameRate(int maxFrameRate) {
		editor.putInt(SHARED_KEY_CALL_MAX_FRAME_RATE, maxFrameRate);
		editor.apply();
	}

	/**
	 * audio sample rate
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallAudioSampleRate() {
		return mSharedPreferences.getInt(SHARED_KEY_CALL_AUDIO_SAMPLE_RATE, -1);
	}

	public void setCallAudioSampleRate(int audioSampleRate) {
		editor.putInt(SHARED_KEY_CALL_AUDIO_SAMPLE_RATE, audioSampleRate);
		editor.apply();
	}

	/**
	 * back camera resolution
	 * format: 320x240
	 * if no value was set, return ""
	 */
	public String getCallBackCameraResolution() {
		return mSharedPreferences.getString(SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION, "");
	}

	public void setCallBackCameraResolution(String resolution) {
		editor.putString(SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION, resolution);
		editor.apply();
	}

	/**
	 * front camera resolution
	 * format: 320x240
	 * if no value was set, return ""
	 */
	public String getCallFrontCameraResolution() {
		return mSharedPreferences.getString(SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION, "");
	}

	public void setCallFrontCameraResolution(String resolution) {
		editor.putString(SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION, resolution);
		editor.apply();
	}

	/**
	 * fixed video sample rate
	 *  if no value was set, return false
	 * @return
     */
	public boolean isCallFixedVideoResolution() {
		return mSharedPreferences.getBoolean(SHARED_KEY_CALL_FIX_SAMPLE_RATE, false);
	}

	public void setCallFixedVideoResolution(boolean enable) {
		editor.putBoolean(SHARED_KEY_CALL_FIX_SAMPLE_RATE, enable);
		editor.apply();
	}

	public void setExternalAudioInputResolution(boolean enable) {
		editor.putBoolean(SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION, enable);
		editor.apply();
	}

	public boolean isExternalAudioInputResolution(){
		return mSharedPreferences.getBoolean(SHARED_KEY_EXTERNAL_INPUT_AUDIO_RESOLUTION,false);
	}

	public void setWatermarkResolution(boolean enable) {
		editor.putBoolean(SHARED_KEY_WATER_MARK_RESOLUTION, enable);
		editor.apply();
	}

	public boolean isWatermarkResolution(){
		return mSharedPreferences.getBoolean(SHARED_KEY_WATER_MARK_RESOLUTION,false);
	}

	public void setUseFCM(boolean useFCM) {
		editor.putBoolean(SHARED_KEY_PUSH_USE_FCM, useFCM);
		editor.apply();
	}

	public boolean isUseFCM() {
		return mSharedPreferences.getBoolean(SHARED_KEY_PUSH_USE_FCM, false);
	}

	/**
	 * set the server port
	 * @param port
	 */
	public void setIMServerPort(int port) {
		editor.putInt(SHARED_KEY_IM_SERVER_PORT, port);
	}

	public int getIMServerPort() {
		return mSharedPreferences.getInt(SHARED_KEY_IM_SERVER_PORT, 0);
	}

	public void setSortMessageByServerTime(boolean sortByServerTime) {
		editor.putBoolean(SHARED_KEY_SORT_MESSAGE_BY_SERVER_TIME, sortByServerTime);
		editor.apply();
	}

	public boolean isSortMessageByServerTime() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SORT_MESSAGE_BY_SERVER_TIME, true);
	}

	/**
	 * 是否允许token登录
	 * @param isChecked
	 */
	public void setEnableTokenLogin(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_ENABLE_TOKEN_LOGIN, isChecked);
		editor.apply();
	}

	public boolean isEnableTokenLogin() {
		return mSharedPreferences.getBoolean(SHARED_KEY_ENABLE_TOKEN_LOGIN, false);
	}

	/**
	 * 翻译目标语言
	 * @param languageCode
	 */
	public void setTargetLanguage(String languageCode) {
		editor.putString(SHARED_KEY_TARGET_LANGUAGE, languageCode);
		editor.apply();
	}

	public String getTargetLanguage() {
		return mSharedPreferences.getString(SHARED_KEY_TARGET_LANGUAGE, "en");
	}

}
