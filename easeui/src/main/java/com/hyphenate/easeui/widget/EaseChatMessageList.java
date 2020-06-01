package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.ui.chat.delegates.EaseExpressionAdapterDelegate;
import com.hyphenate.easeui.ui.chat.delegates.EaseFileAdapterDelegate;
import com.hyphenate.easeui.ui.chat.delegates.EaseImageAdapterDelegate;
import com.hyphenate.easeui.ui.chat.delegates.EaseLocationAdapterDelegate;
import com.hyphenate.easeui.ui.chat.delegates.EaseVideoAdapterDelegate;
import com.hyphenate.easeui.ui.chat.delegates.EaseVoiceAdapterDelegate;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class EaseChatMessageList extends RelativeLayout {
    private Context context;
    private SwipeRefreshLayout srlRefresh;
    private EaseRecyclerView messageList;
    private EaseMessageListItemStyle itemStyle;
    private int chatType;
    private String toChatUsername;
    private EMConversation conversation;
    private EaseMessageAdapter messageAdapter;

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
        addDefaultDelegates();
    }

    /**
     * 设置默认的消息类型
     */
    private void addDefaultDelegates() {
        messageAdapter.addDelegate(new EaseExpressionAdapterDelegate())
                .addDelegate(new EaseFileAdapterDelegate())
                .addDelegate(new EaseImageAdapterDelegate())
                .addDelegate(new EaseLocationAdapterDelegate())
                .addDelegate(new EaseVideoAdapterDelegate())
                .addDelegate(new EaseVoiceAdapterDelegate());
    }
}
