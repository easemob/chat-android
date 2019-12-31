package com.hyphenate.chatuidemo.section.friends.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMChatRoomManagerRepository;

import java.util.List;

public class ChatRoomContactViewModel extends AndroidViewModel {
    private EMChatRoomManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EMChatRoom>>> loadObservable;
    private SingleSourceLiveData<Resource<List<EMChatRoom>>> loadMoreObservable;

    public ChatRoomContactViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMChatRoomManagerRepository();
        loadObservable = new SingleSourceLiveData<>();
        loadMoreObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EMChatRoom>>> getLoadObservable() {
        return loadObservable;
    }

    public void loadChatRooms(int pageNum, int pageSize) {
        loadObservable.setSource(mRepository.loadChatRoomsFromServer(pageNum, pageSize));
    }

    public SingleSourceLiveData<Resource<List<EMChatRoom>>> getLoadMoreObservable() {
        return loadMoreObservable;
    }

    public void setLoadMoreChatRooms(int pageNum, int pageSize) {
        loadMoreObservable.setSource(mRepository.loadChatRoomsFromServer(pageNum, pageSize));
    }

}
