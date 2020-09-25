package com.hyphenate.easeim.section.message.delegates;

import android.view.View;
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

public class AgreeMsgDelegate extends EaseBaseDelegate<InviteMessage, AgreeMsgDelegate.ViewHolder> {

    @Override
    public boolean isForViewType(InviteMessage msg, int position) {
        return msg.getStatusEnum() == InviteMessageStatus.BEAGREED;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_agree;
    }

    @Override
    protected AgreeMsgDelegate.ViewHolder createViewHolder(View view) {
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
            message.setText(R.string.demo_contact_listener_onFriendRequestAccepted);
            time.setText(DateUtils.getTimestampString(new Date(msg.getTime())));
        }
    }
}
