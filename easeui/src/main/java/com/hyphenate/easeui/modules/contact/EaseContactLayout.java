package com.hyphenate.easeui.modules.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.manager.SidebarPresenter;
import com.hyphenate.easeui.modules.contact.interfaces.IContactLayout;
import com.hyphenate.easeui.widget.EaseSidebar;

public class EaseContactLayout extends RelativeLayout implements IContactLayout {
    private EaseContactListLayout contactList;
    private EaseSidebar sideBarContact;
    private TextView floatingHeader;

    private SidebarPresenter sidebarPresenter;

    public EaseContactLayout(Context context) {
        this(context, null);
    }

    public EaseContactLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseContactLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_layout_contact, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contactList = findViewById(R.id.contact_list);
        sideBarContact = findViewById(R.id.side_bar_contact);
        floatingHeader = findViewById(R.id.floating_header);
    }

    public void init() {
        sidebarPresenter = new SidebarPresenter();
        sidebarPresenter.setupWithRecyclerView(contactList, contactList.getListAdapter(), floatingHeader);
        sideBarContact.setOnTouchEventListener(sidebarPresenter);
        contactList.loadDefaultData();
    }

    /**
     * 展示简洁模式
     */
    public void showSimple() {
        contactList.showItemHeader(false);
        sideBarContact.setVisibility(GONE);
    }

    /**
     * 展示常规模式
     */
    public void showNormal() {
        contactList.showItemHeader(true);
        sideBarContact.setVisibility(VISIBLE);
    }

    @Override
    public EaseContactListLayout getContactList() {
        return contactList;
    }
}

