package com.hyphenate.chatuidemo.section.chat.fragment;

import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.utils.ToastUtils;
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
import com.hyphenate.easeui.ui.EaseViewHolderProvider;
import com.hyphenate.easeui.ui.chat.EaseChatFragment;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseViewHolderHelper;

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
    protected void initChildData() {
        super.initChildData();
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
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

        private final Map<String, int[]> viewTypeMap;

        public ViewHolderProvider() {
            //添加相应的消息类型，并返回相应的map
            EaseViewHolderHelper helper = EaseViewHolderHelper.getInstance();
            helper.addViewType(DemoConstant.MESSAGE_TYPE_RECALL);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_VOICE_CALL);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_VIDEO_CALL);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_CONFERENCE_INVITE);
            helper.addViewType(DemoConstant.MESSAGE_TYPE_LIVE_INVITE);
            viewTypeMap = helper.getViewTypeMap();
        }

        @Override
        public int provideViewType(EMMessage message) {
            if(message.getType() == EMMessage.Type.TXT) {
                boolean isSender = message.direct() == EMMessage.Direct.SEND;
                if(message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return getViewType(isSender, DemoConstant.MESSAGE_TYPE_VOICE_CALL);
                }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    return getViewType(isSender, DemoConstant.MESSAGE_TYPE_VIDEO_CALL);
                }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_TYPE_RECALL, false)) {
                    return getViewType(isSender, DemoConstant.MESSAGE_TYPE_RECALL);
                }else if(!message.getStringAttribute(DemoConstant.MSG_ATTR_CONF_ID, "").equals("")) {
                    return getViewType(isSender, EaseConstant.MESSAGE_TYPE_CONFERENCE_INVITE);
                }else if(!message.getStringAttribute(DemoConstant.EM_CONFERENCE_OP, "").equals("")) {
                    return getViewType(isSender, DemoConstant.MESSAGE_TYPE_LIVE_INVITE);
                }
            }
            return super.provideViewType(message);
        }

        private int getViewType(boolean isSender, String type) {
            int[] viewTypes = viewTypeMap.get(type);
            return viewTypes == null ? 0 : isSender ? viewTypes[0] : viewTypes[1];
        }

        @Override
        public EaseChatRowViewHolder provideViewHolder(ViewGroup parent, int viewType, MessageListItemClickListener listener, EaseMessageListItemStyle itemStyle) {
            Iterator<String> iterator = viewTypeMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                int[] viewTypes = viewTypeMap.get(key);
                if(viewTypes == null) {
                    break;
                }
                switch (key) {
                    case DemoConstant.MESSAGE_TYPE_RECALL :
                        if(viewTypes[0] == viewType || viewTypes[1] == viewType) {
                            return ChatRecallViewHolder.create(parent, true, listener, itemStyle);
                        }
                    case DemoConstant.MESSAGE_TYPE_VOICE_CALL :
                        if(viewTypes[0] == viewType) {
                            return ChatVoiceCallViewHolder.create(parent, true, listener, itemStyle);
                        }else if(viewTypes[1] == viewType) {
                            return ChatVoiceCallViewHolder.create(parent, true, listener, itemStyle);
                        }

                    case DemoConstant.MESSAGE_TYPE_VIDEO_CALL :
                        if(viewTypes[0] == viewType) {
                            return ChatVideoCallViewHolder.create(parent, true, listener, itemStyle);
                        }else if(viewTypes[1] == viewType) {
                            return ChatVideoCallViewHolder.create(parent, true, listener, itemStyle);
                        }
                    case DemoConstant.MESSAGE_TYPE_CONFERENCE_INVITE :
                        if(viewTypes[0] == viewType) {
                            return ChatConferenceInviteViewHolder.create(parent, true, listener, itemStyle);
                        }else if(viewTypes[1] == viewType) {
                            return ChatConferenceInviteViewHolder.create(parent, true, listener, itemStyle);
                        }
                    case DemoConstant.MESSAGE_TYPE_LIVE_INVITE :
                        if(viewTypes[0] == viewType) {
                            return ChatLiveInviteViewHolder.create(parent, true, listener, itemStyle);
                        }else if(viewTypes[1] == viewType) {
                            return ChatLiveInviteViewHolder.create(parent, true, listener, itemStyle);
                        }
                }
            }
            return super.provideViewHolder(parent, viewType, listener, itemStyle);
        }
    }
}