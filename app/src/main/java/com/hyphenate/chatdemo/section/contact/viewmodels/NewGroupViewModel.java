package com.hyphenate.chatdemo.section.contact.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chatdemo.common.enums.Status;
import com.hyphenate.chatdemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.repositories.EMGroupManagerRepository;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class NewGroupViewModel extends AndroidViewModel {
    private EMGroupManagerRepository repository;
    private SingleSourceLiveData<Resource<EMGroup>> groupObservable;

    public NewGroupViewModel(@NonNull Application application) {
        super(application);
        repository = new EMGroupManagerRepository();
        groupObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<EMGroup>> groupObservable() {
        return groupObservable;
    }

    public void createGroup(String groupName, String desc, String[] allMembers, String reason, EMGroupOptions option) {
        LiveData<Resource<EMGroup>> liveData = Transformations.switchMap(repository.createGroup(groupName, desc, allMembers, reason, option), new Function<Resource<EMGroup>, LiveData<Resource<EMGroup>>>() {
            @Override
            public LiveData<Resource<EMGroup>> apply(Resource<EMGroup> input) {
                if(input.status == Status.SUCCESS) {
                    return repository.reportGroupIdToServer(input.data);
                }else if(input.status == Status.LOADING) {
                    return new MutableLiveData<>(Resource.loading(null));
                }else {
                    return new MutableLiveData<>(Resource.error(input.errorCode, input.getMessage(), null));
                }
            }
        });
        groupObservable.setSource(liveData);
    }
}
