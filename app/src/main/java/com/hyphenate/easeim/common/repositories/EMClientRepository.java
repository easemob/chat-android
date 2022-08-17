package com.hyphenate.easeim.common.repositories;

import static com.hyphenate.cloud.HttpClientManager.Method_GET;
import static com.hyphenate.cloud.HttpClientManager.Method_POST;
import static com.hyphenate.cloud.HttpClientManager.Method_PUT;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.cloud.HttpClientManager;
import com.hyphenate.cloud.HttpResponse;
import com.hyphenate.easeim.BuildConfig;
import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeim.common.interfaceOrImplement.DemoEmCallBack;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.manager.OptionsHelper;
import com.hyphenate.easeim.common.net.ErrorCode;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作为EMClient的repository,处理EMClient相关的逻辑
 */
public class EMClientRepository extends BaseEMRepository{

    private static final String TAG = EMClientRepository.class.getSimpleName();

    /**
     * 登录过后需要加载的数据
     * @return
     */
    public LiveData<Resource<Boolean>> loadAllInfoFromHX() {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(ResultCallBack<LiveData<Boolean>> callBack) {
                if(isAutoLogin()) {
                    runOnIOThread(() -> {
                        if(isLoggedIn()) {
                            loadAllConversationsAndGroups();
                            callBack.onSuccess(createLiveData(true));
                        }else {
                            callBack.onError(ErrorCode.EM_NOT_LOGIN);
                        }

                    });
                }else {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                }

            }
        }.asLiveData();
    }

    /**
     * 从本地数据库加载所有的对话及群组
     */
    private void loadAllConversationsAndGroups() {
        // 初始化数据库
        initDb();
        // 从本地数据库加载所有的对话及群组
        getChatManager().loadAllConversations();
        getGroupManager().loadAllGroups();
    }

    /**
     * 注册
     * @param userName
     * @param pwd
     * @return
     */
    public LiveData<Resource<String>> registerToHx(String userName, String pwd) {
        return new NetworkOnlyResource<String>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                //注册之前先判断SDK是否已经初始化，如果没有先进行SDK的初始化
                if(!DemoHelper.getInstance().isSDKInit) {
                    DemoHelper.getInstance().init(DemoApplication.getInstance());
                    DemoHelper.getInstance().getModel().setCurrentUserName(userName);
                }
                runOnIOThread(() -> {
                    try {
                        EMClient.getInstance().createAccount(userName, pwd);
                        callBack.onSuccess(createLiveData(userName));
                    } catch (HyphenateException e) {
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }

        }.asLiveData();
    }

    /**
     * 登录到服务器，可选择密码登录或者token登录
     * 登录之前先初始化数据库，如果登录失败，再关闭数据库;如果登录成功，则再次检查是否初始化数据库
     * @param userName
     * @param pwd
     * @param isTokenFlag
     * @return
     */
    public LiveData<Resource<EaseUser>> loginToServer(String userName, String pwd, boolean isTokenFlag) {
        return new NetworkOnlyResource<EaseUser>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
                DemoHelper.getInstance().init(DemoApplication.getInstance());
                DemoHelper.getInstance().getModel().setCurrentUserName(userName);
                DemoHelper.getInstance().getModel().setCurrentUserPwd(pwd);
                OptionsHelper.getInstance().checkChangeServe();
                if(isTokenFlag) {
                    EMClient.getInstance().loginWithToken(userName, pwd, new DemoEmCallBack() {
                        @Override
                        public void onSuccess() {
                            successForCallBack(callBack);
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                            closeDb();
                        }
                    });
                }else {
                    EMClient.getInstance().login(userName, pwd, new DemoEmCallBack() {
                        @Override
                        public void onSuccess() {
                            successForCallBack(callBack);
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                            closeDb();
                        }
                    });
                }

            }

        }.asLiveData();
    }

    /**
     * 退出登录
     * @param unbindDeviceToken
     * @return
     */
    public LiveData<Resource<Boolean>> logout(boolean unbindDeviceToken) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        DemoHelper.getInstance().getModel().setPhoneNumber("");
                        DemoHelper.getInstance().logoutSuccess();
                        //reset();
                        if (callBack != null) {
                            callBack.onSuccess(createLiveData(true));
                        }

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String error) {
                        //reset();
                        if (callBack != null) {
                            callBack.onError(code, error);
                        }
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 设置本地标记，是否自动登录
     * @param autoLogin
     */
    public void setAutoLogin(boolean autoLogin) {
        PreferenceManager.getInstance().setAutoLogin(autoLogin);
    }

    private void successForCallBack(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
        // ** manually load all local groups and conversation
        loadAllConversationsAndGroups();
        //从服务器拉取加入的群，防止进入会话页面只显示id
        getAllJoinGroup();
        // get contacts from server
        getContactsFromServer();
        // get current user id
        String currentUser = EMClient.getInstance().getCurrentUser();
        EaseUser user = new EaseUser(currentUser);
        callBack.onSuccess(new MutableLiveData<>(user));
    }

    private void getContactsFromServer() {
        new EMContactManagerRepository().getContactList(new ResultCallBack<List<EaseUser>>() {
            @Override
            public void onSuccess(List<EaseUser> value) {
                if(getUserDao() != null) {
                    getUserDao().clearUsers();
                    getUserDao().insert(EmUserEntity.parseList(value));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void getAllJoinGroup() {
        new EMGroupManagerRepository().getAllGroups(new ResultCallBack<List<EMGroup>>() {
            @Override
            public void onSuccess(List<EMGroup> value) {
                //加载完群组信息后，刷新会话列表页面，保证展示群组名称
                EMLog.i("ChatPresenter", "login isGroupsSyncedWithServer success");
                EaseEvent event = EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
                LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(event);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void closeDb() {
        DemoDbHelper.getInstance(DemoApplication.getInstance()).closeDb();
    }


    public LiveData<Resource<Boolean>> getVerificationCode(String phoneNumber,String image_id,String imageCode){
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getVerificationCodeFromServe(phoneNumber,image_id,imageCode, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    private void getVerificationCodeFromServe(String phoneNumber,String image_id,String imageCode ,EMCallBack callBack) {
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                JSONObject request = new JSONObject();
                request.putOpt("phoneNumber", phoneNumber);
                request.putOpt("imageId", image_id);
                request.putOpt("imageCode", imageCode);
                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_SEND_SMS_FROM_SERVER ;
                EMLog.d("getVerificationCodeFromServe url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    EMLog.e("getVerificationCodeFromServe success : ", responseInfo);
                    callBack.onSuccess();
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        callBack.onError(code, object.getString("errorInfo"));
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(2, e.getMessage());
            }
        });
    }


    public LiveData<Resource<String>> getImgVerificationCode(){
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                getImgVerificationCodeFromServe( new ResultCallBack<String>() {
                    @Override
                    public void onSuccess(String image_id) {
                        callBack.onSuccess(createLiveData(image_id));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    private void getImgVerificationCodeFromServe(ResultCallBack<String> callBack) {
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_VERIFICATION_CODE;
                EMLog.d("getImgVerificationCodeFromServe url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, "", Method_GET);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    EMLog.d("getImgVerificationCodeFromServe success : ", responseInfo);
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        JSONObject data = object.getJSONObject("data");
                        String image_id = data.getString("image_id");
                        String image_enabled = data.getString("image_enabled");
                        String image_url = data.getString("image_url");
                        callBack.onSuccess(image_id);
                    }
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        callBack.onError(code, object.getString("errorInfo"));
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }


    public LiveData<Resource<Boolean>> registerFromServe(String userName, String userPassword,String phoneNumber,String smsCode,String imageId,String imageCode){
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                registerFromAppServe(userName, userPassword, phoneNumber, smsCode, imageId, imageCode, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    private void registerFromAppServe(String userName, String userPassword,String phoneNumber,String smsCode,String imageId,String imageCode,EMCallBack callBack) {
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                JSONObject request = new JSONObject();
                request.putOpt("userId", userName);
                request.putOpt("userPassword", userPassword);
                request.putOpt("phoneNumber", phoneNumber);
                request.putOpt("smsCode", smsCode);
                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_BASE_USER + BuildConfig.APP_SERVER_REGISTER ;
                EMLog.d("registerToAppServer url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    EMLog.d("registerToAppServer success : ", responseInfo);
                    callBack.onSuccess();
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        callBack.onError(code, object.getString("errorInfo"));
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }


    public LiveData<Resource<String>> loginFromServe(String userName, String userPassword){
        return new NetworkOnlyResource<String>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                DemoHelper.getInstance().init(DemoApplication.getInstance());
                OptionsHelper.getInstance().checkChangeServe();
                LoginFromAppServe(userName, userPassword, new ResultCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        callBack.onSuccess(createLiveData(value));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    private void LoginFromAppServe(String userName,String userPassword ,ResultCallBack<String> callBack){
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                JSONObject request = new JSONObject();
                request.putOpt("userId", userName);
                request.putOpt("userPassword", userPassword);

                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_BASE_USER + BuildConfig.APP_SERVER_LOGIN ;
                EMLog.d("LoginToAppServer url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    JSONObject object = new JSONObject(responseInfo);
                    EMLog.d("LoginToAppServer success : ", responseInfo);
                    String phoneNumber = object.getString("phoneNumber");
                    DemoHelper.getInstance().getModel().setPhoneNumber(phoneNumber);
                    callBack.onSuccess(object.getString("token"));
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        callBack.onError(code, object.getString("errorInfo"));
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }

    public LiveData<Resource<Boolean>> checkIdentity(String userId,String phoneNumber,String smsCode){
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                check(userId, phoneNumber, smsCode, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }

    private void check(String userName,String phoneNumber,String smsCode,EMCallBack callBack){
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                JSONObject request = new JSONObject();
                request.putOpt("userId", userName);
                request.putOpt("phoneNumber", phoneNumber);
                request.putOpt("smsCode", smsCode);

                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_BASE_USER + BuildConfig.APP_SERVE_CHECK_RESET ;
                EMLog.d("checkIdentity url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_POST);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    JSONObject object = new JSONObject(responseInfo);
                    EMLog.d("checkIdentity success : ", responseInfo);
                    callBack.onSuccess();
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        callBack.onError(code, object.getString("errorInfo"));
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }

    public LiveData<Resource<Boolean>> changePwdFromServe(String userId,String newPassword){
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                changePwd(userId,newPassword,  new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }
                });
            }
        }.asLiveData();
    }


    private void changePwd(String userId,String newPassword,EMCallBack callBack){
        runOnIOThread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                JSONObject request = new JSONObject();
                request.putOpt("newPassword", newPassword);

                String url = BuildConfig.APP_SERVER_PROTOCOL + "://" + BuildConfig.APP_SERVER_DOMAIN + BuildConfig.APP_BASE_USER + userId+ BuildConfig.APP_SERVE_CHANGE_PWD ;
                EMLog.d("changePwdFromServe url : ", url);
                HttpResponse response = HttpClientManager.httpExecute(url, headers, request.toString(), Method_PUT);
                int code = response.code;
                String responseInfo = response.content;
                if (code == 200) {
                    JSONObject object = new JSONObject(responseInfo);
                    EMLog.d("changePwdFromServe success : ", responseInfo);
                    callBack.onSuccess();
                } else {
                    if (responseInfo != null && responseInfo.length() > 0) {
                        JSONObject object = new JSONObject(responseInfo);
                        callBack.onError(code, object.getString("errorInfo"));
                    }else {
                        callBack.onError(code, responseInfo);
                    }
                }
            } catch (Exception e) {
                callBack.onError(EMError.NETWORK_ERROR, e.getMessage());
            }
        });
    }
}
