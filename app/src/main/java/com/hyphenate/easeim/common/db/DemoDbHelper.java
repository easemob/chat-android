package com.hyphenate.easeim.common.db;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.hyphenate.easeim.common.db.dao.AppKeyDao;
import com.hyphenate.easeim.common.db.dao.EmUserDao;
import com.hyphenate.easeim.common.db.dao.InviteMessageDao;
import com.hyphenate.easeim.common.db.dao.MsgTypeManageDao;
import com.hyphenate.easeim.common.utils.MD5;
import com.hyphenate.util.EMLog;

public class DemoDbHelper {
    private static final String TAG = "DemoDbHelper";
    private static DemoDbHelper instance;
    private Context mContext;
    private String currentUser;
    private AppDatabase mDatabase;
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    private DemoDbHelper(Context context){
        this.mContext = context.getApplicationContext();
    }

    public static DemoDbHelper getInstance(Context context) {
        if(instance == null) {
            synchronized (DemoDbHelper.class) {
                if(instance == null) {
                    instance = new DemoDbHelper(context);
                }
            }
        }
        return instance;
    }

    /**
     * 初始化数据库
     * @param user
     */
    public void initDb(String user) {
        if(currentUser != null) {
            if(TextUtils.equals(currentUser, user)) {
                EMLog.i(TAG, "you have opened the db");
                return;
            }
            closeDb();
        }
        this.currentUser = user;
        String userMd5 = MD5.encrypt2MD5(user);
        // 以下数据库升级设置，为升级数据库将清掉之前的数据，如果要保留数据，慎重采用此种方式
        // 可以采用addMigrations()的方式，进行数据库的升级
        String dbName = String.format("em_%1$s.db", userMd5);
        EMLog.i(TAG, "db name = "+dbName);
        mDatabase = Room.databaseBuilder(mContext, AppDatabase.class, dbName)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreatedObservable() {
        return mIsDatabaseCreated;
    }

    /**
     * 关闭数据库
     */
    public void closeDb() {
        if(mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        currentUser = null;
    }

    public EmUserDao getUserDao() {
        if(mDatabase != null) {
            return mDatabase.userDao();
        }
        EMLog.i(TAG, "get userDao failed, should init db first");
        return null;
    }

    public InviteMessageDao getInviteMessageDao() {
        if(mDatabase != null) {
            return mDatabase.inviteMessageDao();
        }
        EMLog.i(TAG, "get inviteMessageDao failed, should init db first");
        return null;
    }

    public MsgTypeManageDao getMsgTypeManageDao() {
        if(mDatabase != null) {
            return mDatabase.msgTypeManageDao();
        }
        EMLog.i(TAG, "get msgTypeManageDao failed, should init db first");
        return null;
    }

    public AppKeyDao getAppKeyDao() {
        if(mDatabase != null) {
            return mDatabase.appKeyDao();
        }
        EMLog.i(TAG, "get appKeyDao failed, should init db first");
        return null;
    }
}
