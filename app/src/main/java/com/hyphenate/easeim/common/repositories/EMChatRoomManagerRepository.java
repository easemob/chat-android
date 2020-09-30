package com.hyphenate.easeim.common.repositories;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMPageResult;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.net.ErrorCode;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EMChatRoomManagerRepository extends BaseEMRepository{

    public LiveData<Resource<List<EMChatRoom>>> loadChatRoomsFromServer(int pageNum, int pageSize) {
        return new NetworkOnlyResource<List<EMChatRoom>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EMChatRoom>>> callBack) {
                getChatRoomManager().asyncFetchPublicChatRoomsFromServer(pageNum, pageSize, new EMValueCallBack<EMPageResult<EMChatRoom>>() {
                    @Override
                    public void onSuccess(EMPageResult<EMChatRoom> value) {
                        if(value != null && value.getData() != null) {
                            Log.e("TAG", "chatRooms = "+value.getData().toString());
                            callBack.onSuccess(createLiveData(value.getData()));
                        }else {
                            callBack.onError(ErrorCode.EM_ERR_UNKNOWN);
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

        }.asLiveData();
    }

    /**
     * get chat room from server
     * @param roomId
     * @return
     */
    public LiveData<Resource<EMChatRoom>> getChatRoomById(String roomId) {
        return new NetworkBoundResource<EMChatRoom, EMChatRoom>() {
            @Override
            protected boolean shouldFetch(EMChatRoom data) {
                return true;
            }

            @Override
            protected LiveData<EMChatRoom> loadFromDb() {
                return createLiveData(getChatRoomManager().getChatRoom(roomId));
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncFetchChatRoomFromServer(roomId, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

            @Override
            protected void saveCallResult(EMChatRoom item) {

            }
        }.asLiveData();
    }

    public LiveData<Resource<List<String>>> loadMembers(String roomId) {
        return new NetworkOnlyResource<List<String>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(()-> {
                    List<String> memberList = new ArrayList<>();
                    try {
                        EMChatRoom chatRoom = getChatRoomManager().fetchChatRoomFromServer(roomId);
                        // page size set to 20 is convenient for testing, should be applied to big value
                        EMCursorResult<String> result = new EMCursorResult<String>();
                        memberList.clear();
                        do {
                            result = EMClient.getInstance().chatroomManager().fetchChatRoomMembers(roomId, result.getCursor(), 20);
                            memberList.addAll(result.getData());
                        } while (result.getCursor() != null && !result.getCursor().isEmpty());

                        memberList.remove(chatRoom.getOwner());
                        memberList.removeAll(chatRoom.getAdminList());
                        
                        if(isAdmin(chatRoom)) {
                            //Set<String> muteList = getChatRoomManager().fetchChatRoomMuteList(roomId, 0, 500).keySet();
                            List<String> blacks = getChatRoomManager().fetchChatRoomBlackList(roomId, 0, 500);
                            //memberList.removeAll(muteList);
                            memberList.removeAll(blacks);
                        }

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                    callBack.onSuccess(createLiveData(memberList));
                });
            }
        }.asLiveData();
    }

    /**
     * 获取聊天室公告内容
     * @param roomId
     * @return
     */
    public LiveData<Resource<String>> fetchChatRoomAnnouncement(String roomId) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getChatRoomManager().asyncFetchChatRoomAnnouncement(roomId, new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * update chat room announcement
     * @param roomId
     * @param announcement
     * @return
     */
    public LiveData<Resource<String>> updateAnnouncement(String roomId, String announcement) {
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getChatRoomManager().asyncUpdateChatRoomAnnouncement(roomId, announcement, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(announcement));
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
     * change chat room subject
     * @param roomId
     * @param newSubject
     * @return
     */
    public LiveData<Resource<EMChatRoom>> changeChatRoomSubject(String roomId, String newSubject) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncChangeChatRoomSubject(roomId, newSubject, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * change chat room description
     * @param roomId
     * @param newDescription
     * @return
     */
    public LiveData<Resource<EMChatRoom>> changeChatroomDescription(String roomId, String newDescription) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncChangeChatroomDescription(roomId, newDescription, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 判断是否是管理员或者群主
     * @param room
     * @return
     */
    private boolean isAdmin(EMChatRoom room) {
        return TextUtils.equals(room.getOwner(), getCurrentUser()) || room.getAdminList().contains(getCurrentUser());
    }

    /**
     * 移交聊天室群主权限
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<EMChatRoom>> changeOwner(String groupId, String username) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                try {
                    getChatRoomManager().asyncChangeOwner(groupId, username, new EMValueCallBack<EMChatRoom>() {
                        @Override
                        public void onSuccess(EMChatRoom value) {
                            callBack.onSuccess(createLiveData(value));
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            callBack.onError(error, errorMsg);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.asLiveData();
    }

    /**
     * 获取聊天室禁言列表
     * @param groupId
     * @return
     */
    public LiveData<Resource<Map<String, Long>>> getChatRoomMuteMap(String groupId) {
        return new NetworkOnlyResource<Map<String, Long>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Map<String, Long>>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    Map<String, Long> map = null;
                    Map<String, Long> result = new HashMap<>();
                    int pageSize = 200;
                    do{
                        try {
                            map = getChatRoomManager().fetchChatRoomMuteList(groupId, 0, pageSize);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            callBack.onError(e.getErrorCode(), e.getMessage());
                            break;
                        }
                        if(map != null) {
                            result.putAll(map);
                        }
                    }while (map != null && map.size() >= 200);
                    callBack.onSuccess(createLiveData(result));
                });

            }

        }.asLiveData();
    }

    /**
     * 获取聊天室黑名单列表
     * @param groupId
     * @return
     */
    public LiveData<Resource<List<String>>> getChatRoomBlackList(String groupId) {
        return new NetworkOnlyResource<List<String>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<String>>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    List<String> list = null;
                    List<String> result = new ArrayList<>();
                    int pageSize = 200;
                    do{
                        try {
                            list = getChatRoomManager().fetchChatRoomBlackList(groupId, 0, pageSize);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            callBack.onError(e.getErrorCode(), e.getMessage());
                            break;
                        }
                        if(list != null) {
                            result.addAll(list);
                        }
                    }while (list != null && list.size() >= 200);
                    callBack.onSuccess(createLiveData(result));
                });

            }

        }.asLiveData();
    }

    /**
     * 设为聊天室管理员
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<EMChatRoom>> addChatRoomAdmin(String groupId, String username) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncAddChatRoomAdmin(groupId, username, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 移除聊天室管理员
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<EMChatRoom>> removeChatRoomAdmin(String groupId, String username) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncRemoveChatRoomAdmin(groupId, username, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 移出聊天室
     * @param groupId
     * @param usernames
     * @return
     */
    public LiveData<Resource<EMChatRoom>> removeUserFromChatRoom(String groupId, List<String> usernames) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncRemoveChatRoomMembers(groupId, usernames, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 添加到聊天室黑名单
     * 需要拥有者或者管理员权限
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<EMChatRoom>> blockUser(String groupId, List<String> username) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncBlockChatroomMembers(groupId, username, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 移出聊天室黑名单
     * @param groupId
     * @param username
     * @return
     */
    public LiveData<Resource<EMChatRoom>> unblockUser(String groupId, List<String> username) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncUnBlockChatRoomMembers(groupId, username, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 禁言
     * 需要聊天室拥有者或者管理员权限
     * @param groupId
     * @param usernames
     * @return
     */
    public LiveData<Resource<EMChatRoom>> muteChatRoomMembers(String groupId, List<String> usernames, long duration) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncMuteChatRoomMembers(groupId, usernames, duration, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 禁言
     * @param groupId
     * @param usernames
     * @return
     */
    public LiveData<Resource<EMChatRoom>> unMuteChatRoomMembers(String groupId, List<String> usernames) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncUnMuteChatRoomMembers(groupId, usernames, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 退群
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> leaveChatRoom(String groupId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getChatRoomManager().leaveChatRoom(groupId);
                callBack.onSuccess(createLiveData(true));
            }
        }.asLiveData();
    }

    /**
     * 解散群
     * @param groupId
     * @return
     */
    public LiveData<Resource<Boolean>> destroyChatRoom(String groupId) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getChatRoomManager().asyncDestroyChatRoom(groupId, new EMCallBack() {
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
     * create new chat room
     * @param subject
     * @param description
     * @param welcomeMessage
     * @param maxUserCount
     * @param members
     * @return
     */
    public LiveData<Resource<EMChatRoom>> createChatRoom(String subject, String description, String welcomeMessage,
                                                         int maxUserCount, List<String> members) {
        return new NetworkOnlyResource<EMChatRoom>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EMChatRoom>> callBack) {
                getChatRoomManager().asyncCreateChatRoom(subject, description, welcomeMessage, maxUserCount, members, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }
        }.asLiveData();
    }

}
