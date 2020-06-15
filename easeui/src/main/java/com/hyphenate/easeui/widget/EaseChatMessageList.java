package com.hyphenate.easeui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.manager.EaseConTypeSetManager;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

import java.util.List;

public class EaseChatMessageList extends RelativeLayout implements View.OnTouchListener, SwipeRefreshLayout.OnRefreshListener {
    private Context context;
    private SwipeRefreshLayout srlRefresh;
    private EaseRecyclerView messageList;
    private EaseMessageListItemStyle itemStyle;
    private int chatType;
    private String toChatUsername;
    private EMConversation conversation;
    private EaseMessageAdapter messageAdapter;
    private MessageListItemClickListener itemClickListener;
    private OnMessageListListener listener;
    private List<EMMessage> currentMessages;
    private boolean showUserNick;

    public EaseChatMessageList(Context context) {
        this(context, null);
    }

    public EaseChatMessageList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatMessageList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseStyle(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //加载布局
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);
        srlRefresh = findViewById(R.id.srl_refresh);
        messageList = findViewById(R.id.message_list);
    }

    private void parseStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList);
        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true))
                .showUserNick(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false))
                .myBubbleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground))
                .otherBuddleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground));

        itemStyle = builder.build();
        ta.recycle();
    }


    public void init(String toChatUsername, int chatType) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        messageList.setLayoutManager(new LinearLayoutManager(context));
        messageAdapter = new EaseMessageAdapter();
        registerDelegates();
        messageList.setAdapter(messageAdapter);

        messageAdapter.setListItemClickListener(itemClickListener);
        messageAdapter.showUserNick(showUserNick);

        initListener();
    }

    private void initListener() {
        srlRefresh.setOnRefreshListener(this);
        messageList.setOnTouchListener(this);
    }

    /**
     * 设置默认的消息类型
     */
    private void registerDelegates() {
        try {
            EaseConTypeSetManager.getInstance().registerConversationType(messageAdapter);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.listener != null) {
            this.listener.onTouch(v, event);
        }
        return false;
    }

    @Override
    public void onRefresh() {
        if(this.listener != null) {
            listener.onRefresh();
        }
    }

    /**
     * 刷新对话列表
     */
    public void refreshMessages() {
        if(isActivityDisable() || messageList == null || conversation == null) {
            return;
        }
        messageList.post(()-> {
            List<EMMessage> messages = conversation.getAllMessages();
            conversation.markAllMessagesAsRead();
            if(messageAdapter != null) {
                messageAdapter.setData(messages);
            }
            currentMessages = messages;
            finishRefresh();
        });
    }

    public void refreshToLatest() {
        if(isActivityDisable() || conversation == null) {
            return;
        }
        List<EMMessage> messages = conversation.getAllMessages();
        boolean refresh = currentMessages != null && messages.size() > currentMessages.size();
        refreshMessages();
        if(refresh) {
            seekToPosition(messages.size() - 1);
        }
    }

    private void finishRefresh() {
        if(srlRefresh != null) {
            srlRefresh.setRefreshing(false);
        }
    }

    /**
     * 移动到指定位置
     * @param position
     */
    private void seekToPosition(int position) {
        if(isActivityDisable() || messageList == null) {
            return;
        }
        if(position < 0) {
            position = 0;
        }
        RecyclerView.LayoutManager manager = messageList.getLayoutManager();
        if(manager instanceof LinearLayoutManager) {
            if(isActivityDisable()) {
                return;
            }
            int finalPosition = position;
            messageList.post(()-> {
                ((LinearLayoutManager)manager).scrollToPositionWithOffset(finalPosition, 0);
            });
        }
    }

    /**
     * load more messages
     */
    public void loadMoreMessages(int pageSize, boolean loadFromServer) {
        if(loadFromServer) {
            loadMoreServerMessages(pageSize);
            return;
        }
        loadMoreLocalMessages(pageSize);
    }

    /**
     * 从本地数据库拉取数据
     */
    public void loadMessagesFromLocal(int pageSize) {
        int msgCount = getCacheMessageCount();
        if(msgCount < getAllMsgCountFromDb() && msgCount < pageSize) {
            loadMoreMessages(pageSize - msgCount, false);
        }else {
            seekToPosition(msgCount - 1);
        }
    }

    /**
     * 获取数据库中消息总数目
     * @return
     */
    protected int getAllMsgCountFromDb() {
        return conversation == null ? 0 : conversation.getAllMsgCount();
    }

    public void loadMoreServerMessages(int pageSize) {
        int count = getCacheMessageCount();
        EMClient.getInstance().chatManager().asyncFetchHistoryMessage(toChatUsername,
                EaseCommonUtils.getConversationType(chatType), pageSize, count > 0 ? conversation.getAllMessages().get(0).getMsgId() : null,
                new EMValueCallBack<EMCursorResult<EMMessage>>() {
                    @Override
                    public void onSuccess(EMCursorResult<EMMessage> value) {
                        if(isActivityDisable()) {
                            return;
                        }
                        if(messageList == null) {
                            return;
                        }
                        messageList.post(()->{
                            loadMoreLocalMessages(pageSize);
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if(isActivityDisable()) {
                            return;
                        }
                        if(messageList == null) {
                            return;
                        }
                        messageList.post(()-> {
                            if(listener != null) {
                                listener.onMessageListError(errorMsg);
                            }
                            loadMoreLocalMessages(pageSize);
                        });
                    }
                });
    }

    /**
     * 获取内存中消息数目
     * @return
     */
    protected int getCacheMessageCount() {
        if(conversation == null) {
            return 0;
        }
        List<EMMessage> messageList = conversation.getAllMessages();
        return messageList != null ? messageList.size() : 0;
    }

    private void loadMoreLocalMessages(int pageSize) {
        List<EMMessage> messageList = conversation.getAllMessages();
        int msgCount = messageList != null ? messageList.size() : 0;
        int allMsgCount = conversation.getAllMsgCount();
        if(msgCount < allMsgCount) {
            String msgId = null;
            if(msgCount > 0) {
                msgId = messageList.get(0).getMsgId();
            }
            List<EMMessage> moreMsgs = null;
            String errorMsg = null;
            try {
                moreMsgs = conversation.loadMoreMsgFromDB(msgId, pageSize);
            } catch (Exception e) {
                errorMsg = e.getMessage();
                e.printStackTrace();
            }
            // 刷新数据，一则刷新数据，二则需要消息进行定位
            if(moreMsgs == null || moreMsgs.isEmpty()) {
                if(listener != null) {
                    listener.onMessageListError(errorMsg);
                }
                return;
            }
            refreshMessages();
            // 对消息进行定位
            seekToPosition(moreMsgs.size() - 1);
        }else {
            finishRefresh();
            Toast.makeText(context, getResources().getString(R.string.no_more_messages), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断当前activity是否不可用
     * @return
     */
    public boolean isActivityDisable() {
        return context == null || (context instanceof Activity && ((Activity) context).isFinishing());
    }

    /**
     * 设置条目点击监听
     * @param listener
     */
    public void setItemClickListener(MessageListItemClickListener listener) {
        this.itemClickListener = listener;
        if(messageAdapter != null) {
            messageAdapter.setListItemClickListener(listener);
            messageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置对话列表监听事件
     * @param listener
     */
    public void setOnMessageListListener(OnMessageListListener listener) {
        this.listener = listener;
    }

    /**
     * 是否展示昵称
     * @param showUserNick
     */
    public void showUserNick(boolean showUserNick) {
        this.showUserNick = showUserNick;
        if(messageAdapter != null) {
            messageAdapter.showUserNick(showUserNick);
            messageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 消息列表接口
     */
    public interface OnMessageListListener {
        /**
         * touch事件
         * @param v
         * @param event
         */
        void onTouch(View v, MotionEvent event);

        /**
         * 下拉刷新
         */
        void onRefresh();

        /**
         * 错误
         * @param message
         */
        void onMessageListError(String message);
    }
}
