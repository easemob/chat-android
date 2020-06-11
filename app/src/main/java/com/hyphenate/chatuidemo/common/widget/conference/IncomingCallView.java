package com.hyphenate.chatuidemo.common.widget.conference;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IncomingCallView extends FrameLayout {

    private ImageButton mBtnReject;
    private ImageButton mBtnPickup;
    private TextView mInviterName;
    private ImageView mIvCallAnim;
    private OnActionListener mOnActionListener;
    private Drawable mDrawableAnim;

    public IncomingCallView(@NonNull Context context) {
        this(context, null);
    }

    public IncomingCallView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IncomingCallView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.demo_incoming_call_view, this);
        mBtnReject = findViewById(R.id.btn_reject);
        mBtnPickup = findViewById(R.id.btn_pickup);
        mInviterName = findViewById(R.id.tv_inviter_name);
        mIvCallAnim = findViewById(R.id.iv_call_anim);

        float[] screenInfo = EaseCommonUtils.getScreenInfo(getContext());
        float min = Math.min(screenInfo[0], screenInfo[1]);
        ViewGroup.LayoutParams params = mIvCallAnim.getLayoutParams();
        params.width = (int) min;
        params.height = (int) min;

        mBtnReject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnActionListener != null) {
                    mOnActionListener.onRejectClick(v);
                }
            }
        });

        mBtnPickup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnActionListener != null) {
                    mOnActionListener.onPickupClick(v);
                }
            }
        });
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == View.VISIBLE) {
            mIvCallAnim.setBackgroundResource(R.drawable.ring_anim);
            mDrawableAnim = mIvCallAnim.getBackground();
            if(mDrawableAnim instanceof AnimationDrawable) {
                ((AnimationDrawable) mDrawableAnim).setOneShot(false);
                ((AnimationDrawable) mDrawableAnim).start();
            }
        }else {
            if(mDrawableAnim instanceof AnimationDrawable) {
                if(((AnimationDrawable) mDrawableAnim).isRunning()) {
                    ((AnimationDrawable) mDrawableAnim).stop();
                }
                mDrawableAnim = null;
            }
        }
    }

    public void setInviteInfo(String inviteInfo) {
        mInviterName.setText(TextUtils.isEmpty(inviteInfo) ? "" : inviteInfo);
    }

    public void setOnActionListener(OnActionListener listener) {
        this.mOnActionListener = listener;
    }

    public interface OnActionListener {
        void onRejectClick(View v);
        void onPickupClick(View v);
    }
}
