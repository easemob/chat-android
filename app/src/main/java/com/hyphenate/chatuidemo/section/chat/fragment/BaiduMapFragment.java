package com.hyphenate.chatuidemo.section.chat.fragment;

import com.baidu.location.BDLocation;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.easeui.ui.EaseBaiduMapFragment;

public class BaiduMapFragment extends EaseBaiduMapFragment {
    private OnBDLocationListener listener;

    @Override
    protected void showMapWithLocationClient() {
        showProgressBar();
        super.showMapWithLocationClient();

    }

    private void showProgressBar() {

    }

    @Override
    protected void showErrorToast(String message) {
        super.showErrorToast(message);
        ToastUtils.showFailToast(message);
    }

    @Override
    protected void onReceiveBDLocation(BDLocation bdLocation) {
        hideProgressBar();
        super.onReceiveBDLocation(bdLocation);
        if(this.listener != null) {
            this.listener.onReceiveBDLocation(lastLocation);
        }
    }

    private void hideProgressBar() {
        if(!isActivityDisable()) {
            return;
        }
        mContext.runOnUiThread(()-> {

        });
    }

    public void setOnBDLocationListener(OnBDLocationListener listener) {
        this.listener = listener;
    }

    public interface OnBDLocationListener {
        /**
         * 获取到定位信息
         * @param bdLocation
         */
        void onReceiveBDLocation(BDLocation bdLocation);
    }
}
