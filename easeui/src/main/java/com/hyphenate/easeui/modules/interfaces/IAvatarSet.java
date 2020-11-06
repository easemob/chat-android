package com.hyphenate.easeui.modules.interfaces;

import android.graphics.drawable.Drawable;

public interface IAvatarSet {
    /**
     * 设置默认头像
     * @param src
     */
    void setAvatarDefaultSrc(Drawable src);

    /**
     * 设置头像大小，长和宽是相同的
     * @param avatarSize
     */
    void setAvatarSize(float avatarSize);

    /**
     * 设置头像样式
     * @param shapeType
     */
    void setAvatarShapeType(int shapeType);

    /**
     * 设置头像半径
     * @param radius
     */
    void setAvatarRadius(int radius);

    /**
     * 设置外边框宽度
     * @param borderWidth
     */
    void setAvatarBorderWidth(int borderWidth);

    /**
     * 设置外边框颜色
     * @param borderColor
     */
    void setAvatarBorderColor(int borderColor);
}

