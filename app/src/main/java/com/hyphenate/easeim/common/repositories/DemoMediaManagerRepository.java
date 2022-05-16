package com.hyphenate.easeim.common.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.model.VideoEntity;
import com.hyphenate.easeui.utils.EaseCompat;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VersionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DemoMediaManagerRepository extends BaseEMRepository {
    /**
     * 从多媒体库和私有目录下的视频存放文件夹中获取视频文件
     * @param context
     * @return
     */
    public LiveData<Resource<List<VideoEntity>>> getVideoListFromMediaAndSelfFolder(Context context) {
        return new NetworkOnlyResource<List<VideoEntity>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<VideoEntity>>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    List<VideoEntity> mList = new ArrayList<>();
                    ContentResolver mContentResolver = context.getContentResolver();
                    Cursor cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            , null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            // ID:MediaStore.Audio.Media._ID
                            int id = cursor.getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID));

                            // title：MediaStore.Audio.Media.TITLE
                            String title = cursor.getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                            // path：MediaStore.Audio.Media.DATA
                            String url = null;
                            if(!VersionUtils.isTargetQ(context)) {
                                url = cursor.getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                            }

                            // duration：MediaStore.Audio.Media.DURATION
                            int duration = cursor
                                    .getInt(cursor
                                            .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                            // 大小：MediaStore.Audio.Media.SIZE
                            int size = (int) cursor.getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                            // 最近一次修改时间：MediaStore.Audio.DATE_MODIFIED
                            long lastModified = cursor.getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));

                            if(size <= 0) {
                                continue;
                            }
                            Uri uri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);

                            VideoEntity entty = new VideoEntity();
                            entty.ID = id;
                            entty.title = title;
                            entty.filePath = url;
                            entty.duration = duration;
                            entty.size = size;
                            entty.uri = uri;
                            entty.lastModified = lastModified;
                            mList.add(entty);
                        } while (cursor.moveToNext());

                    }
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                    getSelfVideoFiles(context, mList);

                    if(!mList.isEmpty()) {
                        sortVideoEntities(mList);
                    }
                    if(callBack != null) {
                        callBack.onSuccess(createLiveData(mList));
                    }
                });
            }
        }.asLiveData();
    }

    private void getSelfVideoFiles(Context context, List<VideoEntity> mList) {
        File videoFolder = PathUtil.getInstance().getVideoPath();
        if(videoFolder.exists() && videoFolder.isDirectory()) {
            File[] files = videoFolder.listFiles();
            if(files != null && files.length > 0) {
                VideoEntity entty;
                for(int i = 0; i < files.length; i++) {
                    entty = new VideoEntity();
                    File file = files[i];
                    if(!EaseCompat.isVideoFile(context, file.getName()) || file.length() <= 0) {
                        continue;
                    }
                    entty.filePath = file.getAbsolutePath();
                    entty.size = (int) file.length();
                    entty.title = file.getName();
                    entty.lastModified = file.lastModified();
                    MediaPlayer player = new MediaPlayer();
                    try {
                        player.setDataSource(file.getPath());
                        player.prepare();
                        entty.duration = player.getDuration();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(entty.size <= 0 || entty.duration <= 0) {
                        continue;
                    }
                    mList.add(entty);
                }
            }
        }

    }

    private void sortVideoEntities(List<VideoEntity> mList) {
        Collections.sort(mList, new Comparator<VideoEntity>() {
            @Override
            public int compare(VideoEntity o1, VideoEntity o2) {
                if(o1 == null && o2 == null) {
                    return 0;
                }
                if(o1 == null) {
                    return 1;
                }
                if(o2 == null) {
                    return -1;
                }
                long result = o2.lastModified - o1.lastModified;
                return result == 0 ? 0 : (result > 0 ? 1 : -1) ;
            }
        });
    }
}

