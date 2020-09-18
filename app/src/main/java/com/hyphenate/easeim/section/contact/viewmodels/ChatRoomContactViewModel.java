package com.hyphenate.easeim.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.repositories.EMChatRoomManagerRepository;

import java.util.List;

public class ChatRoomContactViewModel extends AndroidViewModel {
    private EMChatRoomManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EMChatRoom>>> loadObservable;
    private SingleSourceLiveData<Resource<List<EMChatRoom>>> loadMoreObservable;
    private LiveDataBus messageChangeObservable = LiveDataBus.get();

    public ChatRoomContactViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMChatRoomManagerRepository();
        loadObservable = new SingleSourceLiveData<>();
        loadMoreObservable = new SingleSourceLiveData<>();
    }

    public LiveDataBus getMessageChangeObservable() {
        return messageChangeObservable;
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
