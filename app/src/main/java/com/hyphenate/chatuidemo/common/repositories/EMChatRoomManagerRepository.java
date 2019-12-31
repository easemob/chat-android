package com.hyphenate.chatuidemo.common.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMPageResult;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;

import java.util.List;

public class EMChatRoomManagerRepository extends BaseEMRepository{

    public LiveData<Resource<List<EMChatRoom>>> loadChatRoomsFromServer(int pageNum, int pageSize) {
        return new NetworkOnlyResource<List<EMChatRoom>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EMChatRoom>>> callBack) {
                getChatRoomManager().asyncFetchPublicChatRoomsFromServer(pageNum, pageSize, new EMValueCallBack<EMPageResult<EMChatRoom>>() {
                    @Override
                    public void onSuccess(EMPageResult<EMChatRoom> value) {
                        if(value != null && value.getData() != null) {
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

}
