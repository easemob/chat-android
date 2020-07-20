package com.hyphenate.chatuidemo.section.message.delegates;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;
import com.hyphenate.easeui.adapter.EaseBaseDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage.InviteMessageStatus;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.DateUtils;

import java.util.Date;

import androidx.annotation.NonNull;

public class OtherMsgDelegate extends EaseBaseDelegate<InviteMessage, OtherMsgDelegate.ViewHolder> {

    @Override
    public boolean isForViewType(InviteMessage msg, int position) {
        return msg.getStatusEnum() != InviteMessageStatus.BEINVITEED &&
                msg.getStatusEnum() != InviteMessageStatus.BEAPPLYED &&
                msg.getStatusEnum() != InviteMessageStatus.GROUPINVITATION &&
                msg.getStatusEnum() != InviteMessageStatus.BEAGREED;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_agree;
    }

    @Override
    protected OtherMsgDelegate.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<InviteMessage> {
        private TextView name;
        private TextView message;
        private EaseImageView avatar;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            name = findViewById(R.id.name);
            message = findViewById(R.id.message);
            avatar = findViewById(R.id.avatar);
            time = findViewById(R.id.time);
            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(InviteMessage msg, int position) {
            name.setText(msg.getFrom());
            time.setText(DateUtils.getTimestampString(new Date(msg.getTime())));
            Context context = name.getContext();
            String str = "";
            InviteMessage.InviteMessageStatus status = msg.getStatusEnum();
            switch (status) {
                case AGREED:
                    str = context.getResources().getString(R.string.Has_agreed_to);
                    break;
                case REFUSED:
                    str = context.getResources().getString(R.string.Has_refused_to);
                    break;
                case GROUPINVITATION_ACCEPTED:
                    str = context.getResources().getString(R.string.accept_join_group);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case GROUPINVITATION_DECLINED:
                    str = context.getResources().getString(R.string.refuse_join_group);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_CONTACT_ADD:
                    str = context.getResources().getString(R.string.multi_device_contact_add);
                    str = String.format(str, msg.getFrom());
                    break;
                case MULTI_DEVICE_CONTACT_BAN:
                    str = context.getResources().getString(R.string.multi_device_contact_ban);
                    str = String.format(str, msg.getFrom());
                    break;
                case MULTI_DEVICE_CONTACT_ALLOW:
                    str = context.getResources().getString(R.string.multi_device_contact_allow);
                    str = String.format(str, msg.getFrom());
                    break;
                case MULTI_DEVICE_CONTACT_ACCEPT:
                    str = context.getResources().getString(R.string.multi_device_contact_accept);
                    str = String.format(str, msg.getFrom());
                    break;
                case MULTI_DEVICE_CONTACT_DECLINE:
                    str = context.getResources().getString(R.string.multi_device_contact_decline);
                    str = String.format(str, msg.getFrom());
                    break;
                case MULTI_DEVICE_GROUP_CREATE:
                    str = context.getResources().getString(R.string.multi_device_group_create);
                    break;
                case MULTI_DEVICE_GROUP_DESTROY:
                    str = context.getResources().getString(R.string.multi_device_group_destroy);
                    break;
                case MULTI_DEVICE_GROUP_JOIN:
                    str = context.getResources().getString(R.string.multi_device_group_join);
                    break;
                case MULTI_DEVICE_GROUP_LEAVE:
                    str = context.getResources().getString(R.string.multi_device_group_leave);
                    break;
                case MULTI_DEVICE_GROUP_APPLY:
                    str = context.getResources().getString(R.string.multi_device_group_apply);
                    break;
                case MULTI_DEVICE_GROUP_APPLY_ACCEPT:
                    str = context.getResources().getString(R.string.multi_device_group_apply_accept);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_APPLY_DECLINE:
                    str = context.getResources().getString(R.string.multi_device_group_apply_decline);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_INVITE:
                    str = context.getResources().getString(R.string.multi_device_group_invite);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_INVITE_ACCEPT:
                    str = context.getResources().getString(R.string.multi_device_group_invite_accept);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_INVITE_DECLINE:
                    str = context.getResources().getString(R.string.multi_device_group_invite_decline);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_KICK:
                    str = context.getResources().getString(R.string.multi_device_group_kick);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_BAN:
                    str = context.getResources().getString(R.string.multi_device_group_ban);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_ALLOW:
                    str = context.getResources().getString(R.string.multi_device_group_allow);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_BLOCK:
                    str = context.getResources().getString(R.string.multi_device_group_block);
                    break;
                case MULTI_DEVICE_GROUP_UNBLOCK:
                    str = context.getResources().getString(R.string.multi_device_group_unblock);
                    break;
                case MULTI_DEVICE_GROUP_ASSIGN_OWNER:
                    str = context.getResources().getString(R.string.multi_device_group_assign_owner);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_ADD_ADMIN:
                    str = context.getResources().getString(R.string.multi_device_group_add_admin);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_REMOVE_ADMIN:
                    str = context.getResources().getString(R.string.multi_device_group_remove_admin);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_ADD_MUTE:
                    str = context.getResources().getString(R.string.multi_device_group_add_mute);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                case MULTI_DEVICE_GROUP_REMOVE_MUTE:
                    str = context.getResources().getString(R.string.multi_device_group_remove_mute);
                    str = String.format(str, msg.getGroupInviter());
                    break;
                default:
                    break;
            }
            message.setText(str);
        }
    }
}
