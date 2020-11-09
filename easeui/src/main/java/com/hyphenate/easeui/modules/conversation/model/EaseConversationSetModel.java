package com.hyphenate.easeui.modules.conversation.model;

import android.graphics.drawable.Drawable;

public class EaseConversationSetModel {
    private int titleTextColor;
    private float titleTextSize;
    private int contentTextColor;
    private float contentTextSize;
    private int dateTextColor;
    private float dateTextSize;
    private int mentionTextColor;
    private float mentionTextSize;
    private Drawable avatarDefaultSrc;
    private float avatarSize;
    private int shapeType;
    private float avatarRadius;
    private float borderWidth;
    private int borderColor;
    private float itemHeight;
    private Drawable bgDrawable;
    private boolean hideUnreadDot;
    private UnreadDotPosition unreadDotPosition;

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public float getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(float titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public int getContentTextColor() {
        return contentTextColor;
    }

    public void setContentTextColor(int contentTextColor) {
        this.contentTextColor = contentTextColor;
    }

    public float getContentTextSize() {
        return contentTextSize;
    }

    public void setContentTextSize(float contentTextSize) {
        this.contentTextSize = contentTextSize;
    }

    public int getDateTextColor() {
        return dateTextColor;
    }

    public void setDateTextColor(int dateTextColor) {
        this.dateTextColor = dateTextColor;
    }

    public float getDateTextSize() {
        return dateTextSize;
    }

    public void setDateTextSize(float dateTextSize) {
        this.dateTextSize = dateTextSize;
    }

    public int getMentionTextColor() {
        return mentionTextColor;
    }

    public void setMentionTextColor(int mentionTextColor) {
        this.mentionTextColor = mentionTextColor;
    }

    public float getMentionTextSize() {
        return mentionTextSize;
    }

    public void setMentionTextSize(float mentionTextSize) {
        this.mentionTextSize = mentionTextSize;
    }

    public Drawable getAvatarDefaultSrc() {
        return avatarDefaultSrc;
    }

    public void setAvatarDefaultSrc(Drawable avatarDefaultSrc) {
        this.avatarDefaultSrc = avatarDefaultSrc;
    }

    public float getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(float avatarSize) {
        this.avatarSize = avatarSize;
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public float getAvatarRadius() {
        return avatarRadius;
    }

    public void setAvatarRadius(float avatarRadius) {
        this.avatarRadius = avatarRadius;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public float getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
    }

    public Drawable getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(Drawable bgDrawable) {
        this.bgDrawable = bgDrawable;
    }

    public boolean isHideUnreadDot() {
        return hideUnreadDot;
    }

    public void setHideUnreadDot(boolean hideUnreadDot) {
        this.hideUnreadDot = hideUnreadDot;
    }

    public UnreadDotPosition getUnreadDotPosition() {
        return unreadDotPosition;
    }

    public void setUnreadDotPosition(UnreadDotPosition unreadDotPosition) {
        this.unreadDotPosition = unreadDotPosition;
    }

    public enum UnreadDotPosition {
        LEFT, RIGHT
    }
}

