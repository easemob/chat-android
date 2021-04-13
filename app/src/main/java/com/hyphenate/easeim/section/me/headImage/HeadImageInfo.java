package com.hyphenate.easeim.section.me.headImage;

import android.graphics.Bitmap;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/16/2021
 */

public class HeadImageInfo {
    private String url;
    private String describe;
    private Bitmap bitmap;

    public HeadImageInfo(String url, String describe) {
        this.url = url;
        this.describe = describe;
    }

    public String getUrl() { return url;}

    public void setUrl(String url) { this.url = url;}

    public String getDescribe() { return describe;}

    public void setDescribe(String describe) { this.describe = describe;}

    public Bitmap getBitmap() { return bitmap; }

    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}
}
