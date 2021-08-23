package com.hyphenate.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo.EMUserInfoType;
import com.hyphenate.cloud.EMHttpClient;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.activity.SelectUserCardActivity;
import com.hyphenate.easeim.section.me.headImage.HeadImageAdapter;
import com.hyphenate.easeim.section.me.headImage.HeadImageInfo;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/16/2021
 */

public class ChooseHeadImageActivity extends BaseInitActivity implements View.OnClickListener {
    private static final String TAG = SelectUserCardActivity.class.getSimpleName();

    private EaseTitleBar titleBar;
    private EaseRecyclerView headImageListView;
    private TextView save_btn;
    List<HeadImageInfo> imageList = new ArrayList<>();
    private HeadImageAdapter avatarAdapter;
    private String baseUrl = "https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/IMDemo/avatar/";
    private String selectHeadUrl = null;
    private String imageUrl = null;

    public static void actionStart(Context context,String headUrl) {
        Intent intent = new Intent(context, ChooseHeadImageActivity.class);
        intent.putExtra("headUrl",headUrl);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        imageUrl = intent.getStringExtra("headUrl");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_choose_headimage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        headImageListView = findViewById(R.id.headImage_ListView);
        save_btn = findViewById(R.id.btn_headImage_save);
        save_btn.setOnClickListener(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_headImage_save){
            if(selectHeadUrl != null){
                EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfoType.AVATAR_URL, selectHeadUrl, new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        EMLog.d(TAG, "updateOwnInfoByAttribute :" + value);
                        showToast(R.string.demo_head_image_update_success);
                        PreferenceManager.getInstance().setCurrentUserAvatar(selectHeadUrl);
                        DemoHelper.getInstance().getUserProfileManager().updateUserAvatar(selectHeadUrl);
                        EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                        //发送联系人更新事件
                        event.message = selectHeadUrl;
                        LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
                        getIntent().putExtra("headImage", selectHeadUrl);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        EMLog.d(TAG, "updateOwnInfoByAttribute  error:" + error + " errorMsg:" + errorMsg);
                        showToast(R.string.demo_head_image_update_failed);
                    }
                });
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        String srcUrl  = baseUrl + "headImage.conf";
        getHeadImageSrc(srcUrl);
    }

    private void initImageHeadList(){
        if (imageList.isEmpty() || imageList.isEmpty()) {
            headImageListView.setVisibility(View.GONE);
        }else{
            GridLayoutManager layoutManager = new GridLayoutManager(mContext,3);
            headImageListView.setLayoutManager(layoutManager);
            avatarAdapter = new HeadImageAdapter();
            avatarAdapter.setData(imageList);
            headImageListView.setAdapter(avatarAdapter);
            avatarAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    avatarAdapter.chooseIndex = position;
                    selectHeadUrl = imageList.get(position).getUrl();
                    avatarAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    /**
     * 获取头像资源
     *
     */
    private void getHeadImageSrc(String srcUrl){
        new AsyncTask<String, Void, Pair<Integer, String>>(){
            @Override
            protected Pair<Integer, String> doInBackground(String... str) {
                try {
                    Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(srcUrl, null,EMHttpClient.GET);
                    return response;
                }catch (HyphenateException exception) {
                    exception.printStackTrace();
                }
                return  null;
            }
            @Override
            protected void onPostExecute(Pair<Integer, String> response) {
                if(response != null) {
                    EMLog.e(TAG,response.toString());
                    try {
                        int resCode = response.first;
                        if(resCode == 200){
                            String ImageStr = response.second.replace(" ","");
                            JSONObject object = new JSONObject(ImageStr);
                            JSONObject headImageObject = object.optJSONObject("headImageList");
                            Iterator it = headImageObject.keys();
                            while(it.hasNext()){
                                String key = it.next().toString();

                                String url = baseUrl + headImageObject.optString(key);
                                imageList.add(new HeadImageInfo(url,key));
                                if(imageUrl != null &&imageUrl.equals(url)){
                                    avatarAdapter.chooseIndex = imageList.size()-1;
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        initImageHeadList();
                                    }
                                });
                            }
                        }else{
                          EMLog.e(TAG,"get headImageInfo failed resCode:"+resCode);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    EMLog.e(TAG,"get headImageInfo response is null");
              }
            }
        }.execute(srcUrl);
    }
}
