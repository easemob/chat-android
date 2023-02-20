package com.hyphenate.chatdemo.common.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.hyphenate.util.EMLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartHeartManager {
    private static final String TAG = "Handler";
    private Context context;
    private static final int INTERVAL = 30;
    private static final int SMART_HEART = 1;
    private final Handler SMART_HEART_HANDLER = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SMART_HEART:
                    EMLog.d(TAG, "receive handler what: SMART_HEART");
                    sendSmartHeart();
                    break;
            }
        }
    };
    // manager msg count thread pool
    ExecutorService threadPool;

    private void sendSmartHeart() {
        synchronized(this) {
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1000);
                        start();
                    }
                });
            }
        }
    }

    public void init(Context context) {
        this.context = context;
        threadPool = Executors.newSingleThreadExecutor();
    }

    public void start() {
        EMLog.d(TAG, "start next interval: SMART_HEART");
        SMART_HEART_HANDLER.removeMessages(SMART_HEART);
        SMART_HEART_HANDLER.sendEmptyMessageDelayed(SMART_HEART, INTERVAL * 1000);
    }

}
