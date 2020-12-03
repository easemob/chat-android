package com.hyphenate.easeim.section.message.delegates;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeim.common.db.entity.InviteMessageStatus;
import com.hyphenate.easeui.adapter.EaseBaseDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.utils.EaseDateUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;

import java.util.Date;

import androidx.annotation.NonNull;

public class InviteMsgDelegate extends EaseBaseDelegate<EMMessage, InviteMsgDelegate.ViewHolder> {
    private OnInviteListener listener;

    @Override
    public boolean isForViewType(EMMessage msg, int position) {
        String statusParams = null;
        try {
            statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
        return status == InviteMessageStatus.BEINVITEED ||
                status == InviteMessageStatus.BEAPPLYED ||
                status == InviteMessageStatus.GROUPINVITATION;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_invite;
    }

    @Override
    protected InviteMsgDelegate.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<EMMessage> {
        private TextView name;
        private TextView message;
        private Button agree;
        private Button refuse;
        private EaseImageView avatar;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            name = findViewById(R.id.name);
            message = findViewById(R.id.message);
            agree = findViewById(R.id.agree);
            refuse = findViewById(R.id.refuse);
            time = findViewById(R.id.time);
            avatar = findViewById(R.id.avatar);
            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(EMMessage msg, int position) {
            String reason = null;
            try {
                name.setText(msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                reason = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_REASON);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            if(TextUtils.isEmpty(reason)) {
                String statusParams = null;
                try {
                    statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
                    InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
                    if(status == InviteMessageStatus.BEINVITEED){
                        reason = name.getContext().getString(InviteMessageStatus.BEINVITEED.getMsgContent(), msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                    }else if (status == InviteMessageStatus.BEAPPLYED) { //application to join group
                        reason = name.getContext().getString(InviteMessageStatus.BEAPPLYED.getMsgContent()
                                , msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM), msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_NAME));
                    } else if (status == InviteMessageStatus.GROUPINVITATION) {
                        reason = name.getContext().getString(InviteMessageStatus.GROUPINVITATION.getMsgContent()
                                , msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_INVITER),  msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_NAME));
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
            message.setText(reason);
            time.setText(EaseDateUtils.getTimestampString(itemView.getContext(), new Date(msg.getMsgTime())));

            agree.setOnClickListener(view -> {
                if(listener != null) {
                    listener.onInviteAgree(view, msg);
                }
            });

            refuse.setOnClickListener(view -> {
                if(listener != null) {
                    listener.onInviteRefuse(view, msg);
                }
            });
        }
    }

    public void setOnInviteListener(OnInviteListener listener) {
        this.listener = listener;
    }

    public interface OnInviteListener {
        void onInviteAgree(View view, EMMessage msg);
        void onInviteRefuse(View view, EMMessage msg);
    }
}
