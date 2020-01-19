package com.hyphenate.chatuidemo.section.group.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;

import java.util.List;

public class SharedFilesViewModel extends AndroidViewModel {
    private EMGroupManagerRepository repository;
    private SingleSourceLiveData<Resource<List<EMMucSharedFile>>> filesObservable;

    public SharedFilesViewModel(@NonNull Application application) {
        super(application);
        repository = new EMGroupManagerRepository();
        filesObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EMMucSharedFile>>> getFilesObservable() {
        return filesObservable;
    }

    public void getSharedFiles(String groupId, int pageNum, int pageSize) {
        filesObservable.setSource(repository.getSharedFiles(groupId, pageNum, pageSize));
    }


}
