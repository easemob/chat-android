package com.hyphenate.easeim.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.section.chat.views.chatRowUserCard;
import com.hyphenate.easeim.section.contact.activity.ContactDetailActivity;
import com.hyphenate.easeim.section.me.activity.UserDetailActivity;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.util.EMLog;

import java.util.Map;

import androidx.annotation.NonNull;

public class ChatUserCardViewHolder extends EaseChatRowViewHolder {

    public ChatUserCardViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatUserCardViewHolder create(ViewGroup parent, boolean isSender,
                                                 MessageListItemClickListener itemClickListener) {
        return new ChatUserCardViewHolder(new chatRowUserCard(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.getType() == EMMessage.Type.CUSTOM){
            EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
            String event = messageBody.event();
            if(event.equals(DemoConstant.USER_CARD_EVENT)){
                Map<String,String> params = messageBody.getParams();
                String uId = params.get(DemoConstant.USER_CARD_ID);
                String avatar = params.get(DemoConstant.USER_CARD_AVATAR);
                String nickname = params.get(DemoConstant.USER_CARD_NICK);
                if(uId != null && uId.length() > 0){
                    if(uId.equals(EMClient.getInstance().getCurrentUser())){
                        UserDetailActivity.actionStart(getContext(),nickname,avatar);
                    }else{
                        EaseUser user = DemoHelper.getInstance().getUserInfo(uId);
                        if(user == null){
                            user = new EaseUser(uId);
                            user.setAvatar(avatar);
                            user.setNickname(nickname);
                        }
                        boolean isFriend =  DemoHelper.getInstance().getModel().isContact(uId);
                        if(isFriend){
                            user.setContact(0);
                        }else{
                            user.setContact(3);
                        }
                        ContactDetailActivity.actionStart(getContext(),user);
                    }
                }else{
                    EMLog.e("ChatUserCardViewHolder","onBubbleClick uId is empty");
                }
            }
        }
    }
}
