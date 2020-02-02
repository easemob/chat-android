package com.hyphenate.easeui.adapter;

import java.io.Serializable;

public class EaseItemModel implements Serializable {
    private Object data;
    private String tag;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
