package com.hyphenate.chatdemo.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.chat.EMContact;
import com.hyphenate.chatdemo.common.livedatas.LiveDataBus;
import com.hyphenate.chatdemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.repositories.EMContactManagerRepository;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

public class ContactsViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;
    private SingleSourceLiveData<Resource<List<EaseUser>>> contactObservable;
    private MediatorLiveData<Resource<List<EaseUser>>> blackObservable;
    private SingleSourceLiveData<Resource<Boolean>> blackResultObservable;
    private SingleSourceLiveData<Resource<Boolean>> deleteObservable;
    private SingleSourceLiveData<Resource<List<EMContact>>> fetchContactsObservable;
    private SingleSourceLiveData<Resource<Boolean>> setRemarkObservable;
    private SingleSourceLiveData<Resource<EMContact>> fetchContactObservable;


    public ContactsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        contactObservable = new SingleSourceLiveData<>();
        blackObservable = new MediatorLiveData<>();
        blackResultObservable = new SingleSourceLiveData<>();
        deleteObservable = new SingleSourceLiveData<>();
        fetchContactsObservable =new SingleSourceLiveData<>();
        setRemarkObservable = new SingleSourceLiveData<>();
        fetchContactObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EaseUser>>> blackObservable() {
        return blackObservable;
    }

    public LiveDataBus messageChangeObservable() {
        return LiveDataBus.get();
    }

    public void getBlackList() {
        blackObservable.addSource(mRepository.getBlackContactList(), result -> blackObservable.postValue(result));
    }

    public void loadContactList(boolean fetchServer) {
        contactObservable.setSource(mRepository.getContactList(fetchServer));
    }


    public LiveData<Resource<List<EaseUser>>> getContactObservable() {
        return contactObservable;
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return blackResultObservable;
    }

    public LiveData<Resource<Boolean>> deleteObservable() {
        return deleteObservable;
    }

    public void deleteContact(String username) {
        deleteObservable.setSource(mRepository.deleteContact(username));
    }

    public void addUserToBlackList(String username, boolean both) {
        blackResultObservable.setSource(mRepository.addUserToBlackList(username, both));
    }

    public LiveData<Resource<List<EMContact>>> getFetchContactsObservable(){
        return fetchContactsObservable;
    }
    public void fetchContactsFromServer(){
        fetchContactsObservable.setSource(mRepository.fetchContactsFromServer());
    }

    public void setContactRemark(String userId, String remark) {
        setRemarkObservable.setSource(mRepository.setContactRemark(userId, remark));
    }

    public SingleSourceLiveData<Resource<Boolean>> setRemarkObservable() {
        return setRemarkObservable;
    }

    public LiveData<Resource<EMContact>> fetchContactObservable(){
        return fetchContactObservable;
    }
    public void fetchContact(String username){
        fetchContactObservable.setSource(mRepository.fetchContact(username));
    }
}
