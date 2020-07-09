package com.hyphenate.chatuidemo.section.message.delegates;

import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage;
import com.hyphenate.easeui.adapter.EaseBaseDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.chatuidemo.common.db.entity.InviteMessage.InviteMessageStatus;
import com.hyphenate.easeui.widget.EaseImageView;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            name = findViewById(R.id.name);
            message = findViewById(R.id.message);
            avatar = findViewById(R.id.avatar);
            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(InviteMessage msg, int position) {
            name.setText(msg.getFrom());
            message.setText(name.getContext().getResources().getString(R.string.Has_agreed_to_your_friend_request));
        }
    }
}
