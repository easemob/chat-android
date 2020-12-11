package com.hyphenate.easeim.section.search.adapter;

import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.utils.EaseDateUtils;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.DateUtils;

import java.util.Date;

public class SearchMessageAdapter extends EaseBaseRecyclerViewAdapter<EMMessage> {
    private String keyword;

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.demo_item_row_chat_history, parent, false);
        return new MessageViewHolder(view);
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private class MessageViewHolder extends ViewHolder<EMMessage> {
        private EaseImageView avatar;
        private TextView name;
        private TextView time;
        private ImageView msg_state;
        private TextView mentioned;
        private TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            avatar = findViewById(R.id.avatar);
            name = findViewById(R.id.name);
            time = findViewById(R.id.time);
            msg_state = findViewById(R.id.msg_state);
            mentioned = findViewById(R.id.mentioned);
            message = findViewById(R.id.message);
        }

        @Override
        public void setData(EMMessage item, int position) {
            EMMessage.ChatType chatType = item.getChatType();
            time.setText(EaseDateUtils.getTimestampString(mContext, new Date(item.getMsgTime())));
            if(chatType == EMMessage.ChatType.GroupChat || chatType == EMMessage.ChatType.ChatRoom) {
                name.setText(item.getFrom());
            }else {
                if(item.direct() == EMMessage.Direct.SEND) {
                    name.setText(item.getFrom());
                }else {
                    name.setText(item.getTo());
                }
            }
            if (item.direct() == EMMessage.Direct.SEND && item.status() == EMMessage.Status.FAIL) {
                msg_state.setVisibility(View.VISIBLE);
            } else {
                msg_state.setVisibility(View.GONE);
            }
            String content = EaseSmileUtils.getSmiledText(mContext, EaseCommonUtils.getMessageDigest(item, mContext)).toString();
            message.post(()-> {
                String subContent = EaseEditTextUtils.ellipsizeString(message, content, keyword, message.getWidth());
                SpannableStringBuilder builder = EaseEditTextUtils.highLightKeyword(mContext, subContent, keyword);
                if(builder != null) {
                    message.setText(builder);
                }else {
                    message.setText(content);
                }
            });
        }
    }
}
