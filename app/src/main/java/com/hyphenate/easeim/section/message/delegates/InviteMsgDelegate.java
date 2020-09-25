package com.hyphenate.easeim.section.message.delegates;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeui.adapter.EaseBaseDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeim.common.db.entity.InviteMessage.InviteMessageStatus;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.DateUtils;

import java.util.Date;

import androidx.annotation.NonNull;

public class InviteMsgDelegate extends EaseBaseDelegate<InviteMessage, InviteMsgDelegate.ViewHolder> {
    private OnInviteListener listener;

    @Override
    public boolean isForViewType(InviteMessage msg, int position) {
        return msg.getStatusEnum() == InviteMessageStatus.BEINVITEED ||
                msg.getStatusEnum() == InviteMessageStatus.BEAPPLYED ||
                msg.getStatusEnum() == InviteMessageStatus.GROUPINVITATION;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_invite;
    }

    @Override
    protected InviteMsgDelegate.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<InviteMessage> {
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
        public void setData(InviteMessage msg, int position) {
            name.setText(msg.getFrom());
            String reason = "";
            if(msg.getStatusEnum() == InviteMessageStatus.BEINVITEED){
                reason = name.getContext().getString(R.string.demo_contact_listener_onContactInvited, msg.getFrom());
            }else if (msg.getStatusEnum() == InviteMessageStatus.BEAPPLYED) { //application to join group
                reason = name.getContext().getString(R.string.demo_group_listener_onRequestToJoinReceived, msg.getFrom(), msg.getGroupName());
            } else if (msg.getStatusEnum() == InviteMessageStatus.GROUPINVITATION) {
                reason = name.getContext().getString(R.string.demo_group_listener_onInvitationReceived, msg.getGroupInviter(), msg.getGroupName());
            }
            if(TextUtils.isEmpty(reason)) {
                reason = msg.getReason();
            }else {
                if(!TextUtils.isEmpty(msg.getReason())) {
                    reason = reason + ":" + msg.getReason();
                }
            }
            message.setText(reason);
            time.setText(DateUtils.getTimestampString(new Date(msg.getTime())));

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
        void onInviteAgree(View view, InviteMessage msg);
        void onInviteRefuse(View view, InviteMessage msg);
    }
}
