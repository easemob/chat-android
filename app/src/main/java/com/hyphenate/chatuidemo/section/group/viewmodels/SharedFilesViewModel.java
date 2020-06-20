package com.hyphenate.chatuidemo.section.group.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chatuidemo.DemoApplication;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.livedatas.SingleSourceLiveData;
import com.hyphenate.chatuidemo.common.net.ErrorCode;
import com.hyphenate.chatuidemo.common.net.Resource;
import com.hyphenate.chatuidemo.common.repositories.EMGroupManagerRepository;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.UriUtils;

import java.io.File;
import java.util.List;

public class SharedFilesViewModel extends AndroidViewModel {
    private Application application;
    private EMGroupManagerRepository repository;
    private SingleSourceLiveData<Resource<List<EMMucSharedFile>>> filesObservable;
    private SingleSourceLiveData<Resource<File>> showFileObservable;
    private SingleSourceLiveData<Resource<Boolean>> refreshFiles;

    public SharedFilesViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        repository = new EMGroupManagerRepository();
        filesObservable = new SingleSourceLiveData<>();
        showFileObservable = new SingleSourceLiveData<>();
        refreshFiles = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EMMucSharedFile>>> getFilesObservable() {
        return filesObservable;
    }

    public void getSharedFiles(String groupId, int pageNum, int pageSize) {
        filesObservable.setSource(repository.getSharedFiles(groupId, pageNum, pageSize));
    }

    public LiveData<Resource<File>> getShowFileObservable() {
        return showFileObservable;
    }

    /**
     * 展示文件
     * @param groupId
     * @param file
     */
    public void showFile(String groupId, EMMucSharedFile file) {
        File localFile = new File(PathUtil.getInstance().getFilePath(), file.getFileName());
        if(localFile.exists()){
            showFileObservable.postValue(Resource.success(localFile));
            return;
        }
        showFileObservable.setSource(repository.downloadFile(groupId, file.getFileId(), localFile));
    }

    public LiveData<Resource<Boolean>> getDeleteObservable() {
        return refreshFiles;
    }

    /**
     * 删除文件
     * @param groupId
     * @param file
     */
    public void deleteFile(String groupId, EMMucSharedFile file) {
        //先判断是否有本地文件，如果有的话，先进行删除
        File local = new File(PathUtil.getInstance().getFilePath(), file.getFileName());
        if(local.exists()) {
            try {
                local.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        refreshFiles.setSource(repository.deleteFile(groupId, file.getFileId()));
    }

    /**
     * 上传文件
     * @param groupId
     * @param uri
     */
    public void uploadFileByUri(String groupId, Uri uri) {
        if(!UriUtils.isFileExistByUri(application, uri)) {
            refreshFiles.postValue(Resource.error(ErrorCode.EM_ERR_FILE_NOT_EXIST, null));
            return;
        }
        refreshFiles.setSource(repository.uploadFile(groupId, uri.toString()));
    }

}
