package com.hyphenate.chatuidemo.section.chat.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.chatuidemo.section.chat.ChatVideoCallActivity;
import com.hyphenate.chatuidemo.section.chat.ChatVoiceCallActivity;
import com.hyphenate.chatuidemo.section.chat.ConferenceActivity;
import com.hyphenate.chatuidemo.section.chat.ImageGridActivity;
import com.hyphenate.chatuidemo.section.chat.LiveActivity;
import com.hyphenate.chatuidemo.section.chat.viewholder.ChatConferenceInviteViewHolder;
import com.hyphenate.chatuidemo.section.chat.viewholder.ChatLiveInviteViewHolder;
import com.hyphenate.chatuidemo.section.chat.viewholder.ChatRecallViewHolder;
import com.hyphenate.chatuidemo.section.chat.viewholder.ChatVideoCallViewHolder;
import com.hyphenate.chatuidemo.section.chat.viewholder.ChatVoiceCallViewHolder;
import com.hyphenate.chatuidemo.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.interfaces.IChatAdapterProvider;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.viewholder.EaseViewHolderProvider;
import com.hyphenate.easeui.ui.chat.EaseChatFragment;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseViewHolderHelper;
import com.hyphenate.easeui.widget.EaseChatInputMenu;

import java.util.Iterator;
import java.util.Map;

public class ChatFragment extends EaseChatFragment implements EaseChatFragment.OnMessageChangeListener {

    private MessageViewModel viewModel;

    @Override
    protected void initChildListener() {
        super.initChildListener();
        setOnMessageChangeListener(this);
    }

    @Override
    protected void addExtendInputMenu() {
        super.addExtendInputMenu();
        if(chatType == EaseConstant.CHATTYPE_SINGLE){
            inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, EaseChatInputMenu.ITEM_VOICE_CALL, this);
            inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, EaseChatInputMenu.ITEM_VIDEO_CALL, this);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) { // 音视频会议
            inputMenu.registerExtendMenuItem(R.string.voice_and_video_conference, R.drawable.em_chat_video_call_selector, EaseChatInputMenu.ITEM_CONFERENCE_CALL, this);
            inputMenu.registerExtendMenuItem(R.string.title_live, R.drawable.em_chat_video_call_selector, EaseChatInputMenu.ITEM_LIVE, this);
        }
    }

    @Override
    protected void initChildData() {
        super.initChildData();
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {
        super.onChatExtendMenuItemClick(itemId, view);
        switch (itemId) {
            case EaseChatInputMenu.ITEM_VIDEO_CALL:
                startVideoCall();
                break;
            case EaseChatInputMenu.ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case EaseChatInputMenu.ITEM_CONFERENCE_CALL:
                ConferenceActivity.startConferenceCall(getActivity(), toChatUsername);
                break;
            case EaseChatInputMenu.ITEM_LIVE:
                LiveActivity.startLive(getContext(), toChatUsername);
                break;
        }
    }

    @Override
    protected void selectVideoFromLocal() {
        super.selectVideoFromLocal();
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
    }

    @Override
    protected void startChatVideoCall() {
        ChatVideoCallActivity.actionStart(mContext, toChatUsername);
    }

    @Override
    protected void startChatVoiceCall() {
        ChatVoiceCallActivity.actionStart(mContext, toChatUsername);
    }

    /**
     * 可以通过此方法提供自定义的ViewHolder
     * @return
     */
    @Override
    public IViewHolderProvider setViewHolderProvider() {
        return new ViewHolderProvider();
    }

    /**
     * 也可以提供自定义的adapter
     * 没有特殊需求，通过{@link #setViewHolderProvider()}提供自定义ViewHolder即可
     * @return
     */
    @Override
    protected IChatAdapterProvider setChatAdapterProvider() {
        return super.setChatAdapterProvider();
    }

    @Override
    protected void showMsgToast(String message) {
        super.showMsgToast(message);
        ToastUtils.showToast(message);
    }

    @Override
    public void onMessageChange(String change) {
        viewModel.setMessageChange(change);
    }

    private class ViewHolderProvider extends EaseViewHolderProvider {
        private Map<String, Integer> viewTypeMap;

        public ViewHolderProvider() {
            //添加相应的消息类型，并返回相应的map
            EaseViewHolderHelper helper = EaseViewHolderHelper.getInstance();
            helper.addViewType(DemoConstant.MESSAGE_TYPE_RECALL);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_VOICE_CALL);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_VIDEO_CALL);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_CONFERENCE_INVITE);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_LIVE_INVITE);
            viewTypeMap = helper.getViewTypeMap();
            Log.e("TAG", "viewTypeMap = "+viewTypeMap.toString());
        }

        @Override
        public int provideViewType(EMMessage message) {
            return EaseViewHolderHelper.getInstance().getAdapterViewType(message, new EaseViewHolderHelper.addMoreMessageTypeProvider() {
                @Override
                public int addMoreMessageType(EMMessage message, Map<String, Integer> viewTypeMap) {
                    if(message.getType() == EMMessage.Type.TXT) {
                        boolean isSender = message.direct() == EMMessage.Direct.SEND;
                        if(message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                            return getViewType(viewTypeMap, isSender, DemoConstant.MESSAGE_TYPE_VOICE_CALL);
                        }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                            return getViewType(viewTypeMap, isSender, DemoConstant.MESSAGE_TYPE_VIDEO_CALL);
                        }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_TYPE_RECALL, false)) {
                            return getViewType(viewTypeMap, isSender, DemoConstant.MESSAGE_TYPE_RECALL);
                        }else if(!message.getStringAttribute(DemoConstant.MSG_ATTR_CONF_ID, "").equals("")) {
                            return getViewType(viewTypeMap, isSender, EaseConstant.MESSAGE_TYPE_CONFERENCE_INVITE);
                        }else if(!message.getStringAttribute(DemoConstant.EM_CONFERENCE_OP, "").equals("")) {
                            return getViewType(viewTypeMap, isSender, DemoConstant.MESSAGE_TYPE_LIVE_INVITE);
                        }
                    }
                    return 0;
                }
            });
        }

        private int getViewType(Map<String, Integer> viewTypeMap, boolean isSender, String type) {
            int sendType = 0;
            try {
                sendType = viewTypeMap.get(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sendType == 0 ? 0 : isSender ? sendType : EaseViewHolderHelper.getInstance().getReceiveType(sendType);
        }

        @Override
        public EaseChatRowViewHolder provideViewHolder(ViewGroup parent, int viewType, MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
            return EaseViewHolderHelper.getInstance().getChatRowViewHolder(parent, viewType, listener, itemStyle, new EaseViewHolderHelper.AddMoreViewHolderProvider() {
                @Override
                public EaseChatRowViewHolder addMoreViewHolder(ViewGroup parent, String type, boolean isSender, MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
                    switch (type) {
                        case DemoConstant.MESSAGE_TYPE_RECALL :
                            return ChatRecallViewHolder.create(parent, isSender, listener, itemStyle);
                        case DemoConstant.MESSAGE_TYPE_VOICE_CALL :
                            return ChatVoiceCallViewHolder.create(parent, isSender, listener, itemStyle);
                        case DemoConstant.MESSAGE_TYPE_VIDEO_CALL :
                            return ChatVideoCallViewHolder.create(parent, isSender, listener, itemStyle);
                        case DemoConstant.MESSAGE_TYPE_CONFERENCE_INVITE :
                            return ChatConferenceInviteViewHolder.create(parent, isSender, listener, itemStyle);
                        case DemoConstant.MESSAGE_TYPE_LIVE_INVITE :
                            return ChatLiveInviteViewHolder.create(parent, isSender, listener, itemStyle);
                    }
                    return null;
                }
            });
        }
    }
}