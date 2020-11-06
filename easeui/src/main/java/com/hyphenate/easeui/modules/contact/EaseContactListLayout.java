package com.hyphenate.easeui.modules.contact;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.hyphenate.easeui.modules.conversation.EaseConversationListLayout;
import com.hyphenate.easeui.modules.interfaces.IPopupMenu;
import com.hyphenate.easeui.modules.menu.OnPopupMenuDismissListener;
import com.hyphenate.easeui.modules.menu.OnPopupMenuItemClickListener;
import com.hyphenate.easeui.modules.menu.PopupMenuHelper;
import com.hyphenate.easeui.widget.EaseRecyclerView;

public class EaseContactListLayout extends EaseRecyclerView implements IContactListLayout, IContactListStyle, IPopupMenu {

    public EaseContactListLayout(Context context) {
        this(context, null);
    }

    public EaseContactListLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseContactListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void showItemDefaultMenu(boolean showDefault) {

    }

    @Override
    public void setItemBackGround(Drawable backGround) {

    }

    @Override
    public void setItemHeight(int height) {

    }

    @Override
    public void setTitleTextSize(int textSize) {

    }

    @Override
    public void setTitleTextColor(int textColor) {

    }

    @Override
    public void setAvatarDefaultSrc(Drawable src) {

    }

    @Override
    public void setAvatarSize(float avatarSize) {

    }

    @Override
    public void setAvatarShapeType(int shapeType) {

    }

    @Override
    public void setAvatarRadius(int radius) {

    }

    @Override
    public void setAvatarBorderWidth(int borderWidth) {

    }

    @Override
    public void setAvatarBorderColor(int borderColor) {

    }

    @Override
    public void clearMenu() {

    }

    @Override
    public void addItemMenu(int groupId, int itemId, int order, String title) {

    }

    @Override
    public void findItemVisible(int id, boolean visible) {

    }

    @Override
    public void setOnPopupMenuItemClickListener(OnPopupMenuItemClickListener listener) {

    }

    @Override
    public void setOnPopupMenuDismissListener(OnPopupMenuDismissListener listener) {

    }

    @Override
    public PopupMenuHelper getMenuHelper() {
        return null;
    }

    @Override
    public void addHeaderAdapter(Adapter adapter) {

    }

    @Override
    public void addFooterAdapter(Adapter adapter) {

    }

    @Override
    public void removeAdapter(Adapter adapter) {

    }

    @Override
    public void setOnItemClickListener(EaseConversationListLayout.OnItemClickListener listener) {

    }

    @Override
    public void setOnItemLongClickListener(EaseConversationListLayout.OnItemLongClickListener listener) {

    }
}

