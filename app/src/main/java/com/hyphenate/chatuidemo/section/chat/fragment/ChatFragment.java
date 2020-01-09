package com.hyphenate.chatuidemo.section.chat.fragment;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chatuidemo.common.utils.ToastUtils;
import com.hyphenate.chatuidemo.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeui.interfaces.IChatAdapterProvider;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.ui.EaseViewHolderProvider;
import com.hyphenate.easeui.ui.chat.EaseChatFragment;

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
        return new EaseViewHolderProvider();
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
}
