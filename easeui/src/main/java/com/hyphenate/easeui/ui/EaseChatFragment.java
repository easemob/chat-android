package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;

public class EaseChatFragment extends EaseBaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, EaseChatInputMenu.ChatInputMenuListener, EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {
    private TextView tvErrorMsg;
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView messageList;
    private EaseChatInputMenu inputMenu;
    private boolean isRoaming;
    private int chatType = EaseConstant.CHATTYPE_SINGLE;
    private String toChatUsername;
    private Activity context;
    private EaseMessageAdapter messageAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            isRoaming = bundle.getBoolean("isRoaming", false);
            chatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            toChatUsername = bundle.getString(EaseConstant.EXTRA_USER_ID);
        }
    }

    private void initView(View view) {
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        srlRefresh = findViewById(R.id.srl_refresh);
        messageList = findViewById(R.id.message_list);
        inputMenu = findViewById(R.id.input_menu);

        initInputMenu();
    }

    private void initListener() {
        tvErrorMsg.setOnClickListener(this);
        srlRefresh.setOnRefreshListener(this);
        inputMenu.setChatInputMenuListener(this);
    }

    private void initData() {
        messageList.setLayoutManager(provideLayoutManager());
        messageList.setAdapter(provideMessageAdapter());
        if(messageAdapter != null) {
            messageAdapter.setConversationMessages();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_error_msg) {
            onChatRoomViewCreation();
        }
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {

    }

    @Override
    public void onRefresh() {

    }

    /**
     * input menu listener
     * when typing on the edit-text layout.
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTyping(CharSequence s, int start, int before, int count) {

    }

    /**
     * input menu listener
     * when send message button pressed
     * @param content
     */
    @Override
    public void onSendMessage(String content) {

    }

    /**
     * input menu listener
     * when big icon pressed
     * @param emojicon
     */
    @Override
    public void onBigExpressionClicked(EaseEmojicon emojicon) {

    }

    /**
     * input menu listener
     * when speak button is touched
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
        return false;
    }

    protected void initInputMenu() {
        inputMenu.registerDefaultMenuItems(this);
    }

    /**
     * provide recyclerView LayoutManager
     * @return
     */
    protected RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    /**
     * provide message adapter
     * @return
     */
    protected RecyclerView.Adapter provideMessageAdapter() {
        messageAdapter = new EaseMessageAdapter(toChatUsername, chatType);
        return messageAdapter;
    }

    /**
     * join chat room
     */
    private void onChatRoomViewCreation() {
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom value) {
                if(context == null) {
                    return;
                }
                context.runOnUiThread(()-> {
                    if(context.isFinishing() || !TextUtils.equals(toChatUsername, value.getId())) {
                        return;
                    }

                });
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    /**
     * 通过id获取当前view控件，需要在onViewCreated()之后的生命周期调用
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return requireView().findViewById(id);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
