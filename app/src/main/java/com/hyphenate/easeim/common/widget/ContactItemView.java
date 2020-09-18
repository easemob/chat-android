package com.hyphenate.easeim.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hyphenate.easeim.R;

public class ContactItemView extends RelativeLayout {

    private TextView unreadMsgView;
    private String mName;
    private Drawable mImage;
    private ImageView mAvatar;
    private TextView mNameView;
    private float mMarginLeft;
    private float mMarginRight;
    private ConstraintLayout mClUser;

    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactItemView(Context context) {
        super(context);
        init(context, null);
    }
    
    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.demo_widget_contact_item, this);
        mClUser = findViewById(R.id.cl_user);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        unreadMsgView = (TextView) findViewById(R.id.unread_msg_number);
        mNameView = (TextView) findViewById(R.id.name);
        View bottomLine = findViewById(R.id.bottom_line);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ContactItemView);
        if(ta != null) {
            int nameId = ta.getResourceId(R.styleable.ContactItemView_contactItemName, -1);
            if(nameId != -1) {
                mNameView.setText(nameId);
            }else {
                mName = ta.getString(R.styleable.ContactItemView_contactItemName);
                mNameView.setText(mName);
            }
            mImage = ta.getDrawable(R.styleable.ContactItemView_contactItemImage);
            mMarginLeft = ta.getDimension(R.styleable.ContactItemView_contactItemBottomLineMarginLeft, 0);
            mMarginRight = ta.getDimension(R.styleable.ContactItemView_contactItemBottomLineMarginRight, 0);
            ta.recycle();
        }
        if(mImage != null){
            mAvatar.setImageDrawable(mImage);
        }
        setBottomLine(bottomLine);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private void setBottomLine(View bottomLine) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bottomLine.getLayoutParams();
        if(mMarginLeft != 0) {
            params.leftMargin = (int)mMarginLeft;
        }
        if(mMarginRight != 0) {
            params.rightMargin = (int)mMarginRight;
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

    public void setImage(@DrawableRes int image) {
        mAvatar.setImageResource(image);
    }

    public void setName(@StringRes int name) {
        mNameView.setText(name);
    }

    public void setName(String name) {
        if(name != null) {
            mNameView.setText(name);
        }
    }
}
