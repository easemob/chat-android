package com.hyphenate.chatdemo.section;


import static com.hyphenate.cloud.HttpClientManager.Method_GET;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMError;
import com.hyphenate.chatdemo.common.constant.DemoConstant;
import com.hyphenate.chatdemo.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.chatdemo.common.net.Resource;
import com.hyphenate.chatdemo.common.repositories.NetworkOnlyResource;
import com.hyphenate.cloud.HttpClientManager;
import com.hyphenate.cloud.HttpResponse;
import com.hyphenate.easeui.manager.EaseThreadManager;

import org.json.JSONObject;

import java.util.HashMap;

public class MainRepository {


    public LiveData<Resource<String>> fetchRobotInfo() {
        return new NetworkOnlyResource<String>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpResponse response = HttpClientManager.httpExecute(DemoConstant.FETCH_ROBOTINFO_URL, new HashMap<>(), null, Method_GET);
                            int code = response.code;
                            String responseInfo = response.content;
                            if (code == 200) {
                                String robotName = null;
                                if (responseInfo != null && responseInfo.length() > 0) {
                                    JSONObject object = new JSONObject(responseInfo);
                                    robotName = object.getString("robotName");
                                    callBack.onSuccess(new MutableLiveData<>(robotName));
                                } else {
                                    callBack.onError(code, responseInfo);
                                }
                            } else {
                                callBack.onError(code, responseInfo);
                            }
                        } catch (Exception e) {
                            callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
                        }
                    }
                });
            }
        }.asLiveData();
    }
}
