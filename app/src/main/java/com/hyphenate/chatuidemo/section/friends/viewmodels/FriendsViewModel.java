package com.hyphenate.chatuidemo.section.friends.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.hyphenate.chatuidemo.common.repositories.EMContactManagerRepository;

public class FriendsViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
    }



}
