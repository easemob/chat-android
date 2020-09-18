package com.hyphenate.easeim.common.model;

import android.text.TextUtils;

/**
 * 服务器设置model
 */
public class DemoServerSetBean {
    private String appkey;
    private String imServer;
    private int imPort;
    private String restServer;
    private boolean isCustomServerEnable;//是否使用自定义服务器
    private boolean isHttpsOnly;//是否只使用https

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getImServer() {
        return imServer;
    }

    public void setImServer(String imServer) {
        this.imServer = imServer;
        if(TextUtils.isEmpty(imServer) && imServer.contains(":")) {
            this.imServer = imServer.split(":")[0];
            try {
                this.imPort = Integer.valueOf(imServer.split(":")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public int getImPort() {
        return imPort;
    }

    public void setImPort(int imPort) {
        this.imPort = imPort;
    }

    public void setImPort() {
        this.imPort = imPort;
    }

    public String getRestServer() {
        return restServer;
    }

    public void setRestServer(String restServer) {
        this.restServer = restServer;
    }

    public boolean isCustomServerEnable() {
        return isCustomServerEnable;
    }

    public void setCustomServerEnable(boolean customServerEnable) {
        isCustomServerEnable = customServerEnable;
    }

    public boolean isHttpsOnly() {
        return isHttpsOnly;
    }

    public void setHttpsOnly(boolean httpsOnly) {
        isHttpsOnly = httpsOnly;
    }

}
