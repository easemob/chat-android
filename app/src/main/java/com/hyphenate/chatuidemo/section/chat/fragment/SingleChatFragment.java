package com.hyphenate.chatuidemo.section.chat.fragment;

import android.content.Intent;
import android.view.View;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.chat.ImageGridActivity;
import com.hyphenate.easeui.ui.chat.EaseSingleChatFragment;

public class SingleChatFragment extends EaseSingleChatFragment {
    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;

    @Override
    protected void addExtendInputMenu() {
        super.addExtendInputMenu();
        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, this);
        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, this);
        inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, this);
        inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, this);
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {
        super.onChatExtendMenuItemClick(itemId, view);
        switch (itemId) {
            case ITEM_VIDEO :
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            case ITEM_FILE :
                //selectFileFromLocal();
                break;
            case ITEM_VOICE_CALL :
                //startVoiceCall();
                break;
            case ITEM_VIDEO_CALL :
                //startVideoCall();
                break;
        }
    }
}
