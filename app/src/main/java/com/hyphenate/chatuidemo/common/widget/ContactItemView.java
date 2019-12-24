package com.hyphenate.chatuidemo.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;

public class ContactItemView extends LinearLayout{

    private TextView unreadMsgView;
    private String mName;
    private Drawable mImage;

    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactItemView(Context context) {
        super(context);
        init(context, null);
    }
    
    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.em_widget_contact_item, this);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        unreadMsgView = (TextView) findViewById(R.id.unread_msg_number);
        TextView nameView = (TextView) findViewById(R.id.name);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ContactItemView);
        if(ta != null) {
            int nameId = ta.getResourceId(R.styleable.ContactItemView_contactItemName, -1);
            if(nameId != -1) {
                nameView.setText(nameId);
            }else {
                mName = ta.getString(R.styleable.ContactItemView_contactItemName);
                nameView.setText(mName);
            }
            mImage = ta.getDrawable(R.styleable.ContactItemView_contactItemImage);
            ta.recycle();
        }
        if(mImage != null){
            avatar.setImageDrawable(mImage);
        }

    }
    
    public void setUnreadCount(int unreadCount){
        unreadMsgView.setText(String.valueOf(unreadCount));
    }
    
    public void showUnreadMsgView(){
        unreadMsgView.setVisibility(View.VISIBLE);
    }
    public void hideUnreadMsgView(){
        unreadMsgView.setVisibility(View.INVISIBLE);
    }
    
}
