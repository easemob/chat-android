package com.hyphenate.easeui.modules.chat;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.manager.EaseConTypeSetManager;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.modules.chat.interfaces.IChatMessageItemSet;
import com.hyphenate.easeui.modules.chat.interfaces.IChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.interfaces.IRecyclerViewHandle;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.List;


public class EaseChatMessageListLayout extends RelativeLayout implements IChatMessageListView, IRecyclerViewHandle
                                                                        , IChatMessageItemSet, IChatMessageListLayout {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private EaseChatMessagePresenter presenter;
    private EaseMessageAdapter messageAdapter;
    private ConcatAdapter baseAdapter;
    /**
     * 加载数据的方式，目前有三种，常规模式（从本地加载），漫游模式，查询历史消息模式（通过数据库搜索）
     */
    private LoadDataType loadDataType;
    /**
     * 消息id，一般是搜索历史消息时会用到这个参数
     */
    private String msgId;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private RecyclerView rvList;
    private SwipeRefreshLayout srlRefresh;
    private LinearLayoutManager layoutManager;
    private EMConversation conversation;
    /**
     * 会话类型，包含单聊，群聊和聊天室
     */
    private EMConversation.EMConversationType conType;
    /**
     * 另一侧的环信id
     */
    private String username;
    private boolean canUseRefresh;
    private LoadMoreStatus loadMoreStatus;
    private OnMessageTouchListener messageTouchListener;
    private OnChatErrorListener errorListener;
    /**
     * 上一次控件的高度
     */
    private int recyclerViewLastHeight;
    /**
     * 条目具体控件的点击事件
     */
    private MessageListItemClickListener messageListItemClickListener;
    private EaseChatItemStyleHelper chatSetHelper;

    public EaseChatMessageListLayout(@NonNull Context context) {
        this(context, null);
    }

    public EaseChatMessageListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatMessageListLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);
        presenter = new EaseChatMessagePresenterImpl();
        if(context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getLifecycle().addObserver(presenter);
        }
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.attachView(this);
        chatSetHelper = EaseChatItemStyleHelper.getInstance();

        rvList = findViewById(R.id.message_list);
        srlRefresh = findViewById(R.id.srl_refresh);

        srlRefresh.setEnabled(canUseRefresh);

        layoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(layoutManager);

        baseAdapter = new ConcatAdapter();
        messageAdapter = new EaseMessageAdapter();
        baseAdapter.addAdapter(messageAdapter);
        rvList.setAdapter(baseAdapter);

        registerChatType();

        initListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EaseChatItemStyleHelper.getInstance().clear();
    }

    private void registerChatType() {
        try {
            EaseConTypeSetManager.getInstance().registerConversationType(messageAdapter);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void init(LoadDataType loadDataType, String username, int chatType) {
        this.username = username;
        this.loadDataType = loadDataType;
        this.conType = EaseCommonUtils.getConversationType(chatType);
        conversation = EMClient.getInstance().chatManager().getConversation(username, conType, true);
        presenter.setupWithConversation(conversation);
    }

    public void loadDefaultData(String msgId) {
        loadDefaultData(pageSize, msgId);
    }

    public void loadDefaultData(int pageSize, String msgId) {
        this.pageSize = pageSize;
        this.msgId = msgId;
        checkConType();
    }

    private void checkConType() {
        if(isChatRoomCon()) {
            presenter.joinChatRoom(username);
        }else {
            loadData();
        }
    }

    private void loadData() {
        conversation.markAllMessagesAsRead();
        if(loadDataType == LoadDataType.ROAM) {
            presenter.loadServerMessages(pageSize);
        }else if(loadDataType == LoadDataType.HISTORY) {
            presenter.loadMoreLocalHistoryMessages(msgId, pageSize, EMConversation.EMSearchDirection.DOWN);
        }else {
            presenter.loadLocalMessages(pageSize);
        }
    }

    /**
     * 加载更多的更早一些的数据，下拉加载更多
     */
    public void loadMorePreviousData() {
        String msgId = getListFirstMessageId();
        if(loadDataType == LoadDataType.ROAM) {
            presenter.loadMoreServerMessages(msgId, pageSize);
        }else if(loadDataType == LoadDataType.HISTORY) {
            presenter.loadMoreLocalHistoryMessages(msgId, pageSize, EMConversation.EMSearchDirection.UP);
        }else {
            presenter.loadMoreLocalMessages(msgId, pageSize);
        }
    }

    /**
     * 专用于加载更多的更新一些的数据，上拉加载更多时使用
     */
    public void loadMoreHistoryData() {
        String msgId = getListLastMessageId();
        if(loadDataType == LoadDataType.HISTORY) {
            loadMoreStatus = LoadMoreStatus.HAS_MORE;
            presenter.loadMoreLocalHistoryMessages(msgId, pageSize, EMConversation.EMSearchDirection.DOWN);
        }
    }

    /**
     * 获取列表最下面的一条消息的id
     * @return
     */
    private String getListFirstMessageId() {
        EMMessage message = null;
        try {
            message = messageAdapter.getData().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message == null ? null : message.getMsgId();
    }

    /**
     * 获取列表最下面的一条消息的id
     * @return
     */
    private String getListLastMessageId() {
        EMMessage message = null;
        try {
            message = messageAdapter.getData().get(messageAdapter.getData().size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message == null ? null : message.getMsgId();
    }

    private boolean isChatRoomCon() {
        return conType == EMConversation.EMConversationType.ChatRoom;
    }

    private void initListener() {
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMorePreviousData();
            }
        });
        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //判断状态及是否还有更多数据
                   if(loadDataType == LoadDataType.HISTORY
                           && loadMoreStatus == LoadMoreStatus.HAS_MORE
                           && layoutManager.findLastVisibleItemPosition() != 0
                           && layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() -1) {
                       //加载更多
                       loadMoreHistoryData();
                   }
                }else {
                    //if recyclerView not idle should hide keyboard
                    if(messageTouchListener != null) {
                        messageTouchListener.onViewDragging();
                    }
                }
            }
        });

        //用于监听RecyclerView高度的变化，从而刷新列表
        rvList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = rvList.getHeight();
                if(recyclerViewLastHeight == 0) {
                    recyclerViewLastHeight = height;
                }
                if(recyclerViewLastHeight != height) {
                    //RecyclerView高度发生变化，刷新页面

                    if(messageAdapter.getData() != null && !messageAdapter.getData().isEmpty()) {
                        seekToPosition(messageAdapter.getData().size() - 1);
                    }
                }
                recyclerViewLastHeight = height;
            }
        });

        messageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(messageTouchListener != null) {
                    messageTouchListener.onTouchItemOutside(view, position);
                }
            }
        });
        messageAdapter.setListItemClickListener(messageListItemClickListener);
    }

    /**
     * 停止下拉动画
     */
    private void finishRefresh() {
        if(presenter.isActive()) {
            runOnUi(() -> {
                if(srlRefresh != null) {
                    srlRefresh.setRefreshing(false);
                }
            });
        }
    }

    private void notifyDataSetChanged() {
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * 设置数据
     * @param data
     */
    public void setData(List<EMMessage> data) {
        messageAdapter.setData(data);
    }

    /**
     * 添加数据
     * @param data
     */
    public void addData(List<EMMessage> data) {
        messageAdapter.addData(data);
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void joinChatRoomSuccess(EMChatRoom value) {
        loadData();
    }

    @Override
    public void joinChatRoomFail(int error, String errorMsg) {
        if(presenter.isActive()) {
            runOnUi(() -> {
                if(errorListener != null) {
                    errorListener.onChatError(error, errorMsg);
                }
            });
        }
    }

    @Override
    public void loadMsgFail(int error, String message) {
        finishRefresh();
        if(errorListener != null) {
            errorListener.onChatError(error, message);
        }
    }

    @Override
    public void loadLocalMsgSuccess(List<EMMessage> data) {
        messageAdapter.setData(data);
    }

    @Override
    public void loadNoLocalMsg() {

    }

    @Override
    public void loadMoreLocalMsgSuccess(List<EMMessage> data) {
        finishRefresh();
        messageAdapter.addData(data);
    }

    @Override
    public void loadNoMoreLocalMsg() {
        finishRefresh();
    }

    @Override
    public void loadMoreLocalHistoryMsgSuccess(List<EMMessage> data, EMConversation.EMSearchDirection direction) {
        if(direction == EMConversation.EMSearchDirection.UP) {
            finishRefresh();
            messageAdapter.getData().addAll(0, data);
        }else {
            messageAdapter.addData(data);
            if(data.size() >= pageSize) {
                loadMoreStatus = LoadMoreStatus.HAS_MORE;
            }else {
                loadMoreStatus = LoadMoreStatus.NO_MORE_DATA;
            }
        }
    }

    @Override
    public void loadNoMoreLocalHistoryMsg() {
        finishRefresh();
    }

    @Override
    public void loadServerMsgSuccess(List<EMMessage> data) {
        messageAdapter.setData(data);
    }

    @Override
    public void loadMoreServerMsgSuccess(List<EMMessage> data) {
        finishRefresh();
        messageAdapter.addData(data);
    }

    @Override
    public void canUseDefaultRefresh(boolean canUseRefresh) {
        srlRefresh.setEnabled(canUseRefresh);
    }

    @Override
    public void refreshMessages() {
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshToLatest() {
        messageAdapter.notifyDataSetChanged();
        seekToPosition(messageAdapter.getData().size() - 1);
    }

    @Override
    public void refreshMessage(EMMessage message) {
        int position = messageAdapter.getData().lastIndexOf(message);
        if(position != -1) {
            messageAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void removeMessage(EMMessage message) {
        if(message == null || messageAdapter.getData() == null) {
            return;
        }
        conversation.removeMessage(message.getMsgId());
        runOnUi(()-> {
            if(presenter.isActive()) {
                List<EMMessage> messages = messageAdapter.getData();
                int position = messages.lastIndexOf(message);
                if(position != -1) {
                    //需要保证条目从集合中删除
                    messages.remove(position);
                    //通知适配器删除条目
                    messageAdapter.notifyItemRemoved(position);
                    //通知刷新下一条消息
                    messageAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public void moveToPosition(int position) {
        seekToPosition(position);
    }

    @Override
    public void showNickname(boolean showNickname) {
        chatSetHelper.setShowNickname(showNickname);
        notifyDataSetChanged();
    }

    @Override
    public void setItemBackground(Drawable bgDrawable) {
        chatSetHelper.setBgDrawable(bgDrawable);
        notifyDataSetChanged();
    }

    @Override
    public void setItemTextSize(int textSize) {
        chatSetHelper.setTextSize(textSize);
        notifyDataSetChanged();
    }

    @Override
    public void setItemTextColor(int textColor) {
        chatSetHelper.setTextColor(textColor);
        notifyDataSetChanged();
    }

    @Override
    public void setItemMinHeight(int height) {
        chatSetHelper.setItemMinHeight(height);
        notifyDataSetChanged();
    }

    @Override
    public void setTimeTextSize(int textSize) {
        chatSetHelper.setTimeTextSize(textSize);
        notifyDataSetChanged();
    }

    @Override
    public void setTimeTextColor(int textColor) {
        chatSetHelper.setTimeTextColor(textColor);
        notifyDataSetChanged();
    }

    @Override
    public void setTimeBackground(Drawable bgDrawable) {
        chatSetHelper.setTimeBgDrawable(bgDrawable);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarDefaultSrc(Drawable src) {
        chatSetHelper.setAvatarDefaultSrc(src);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarSize(float avatarSize) {
        chatSetHelper.setAvatarSize(avatarSize);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarShapeType(int shapeType) {
        chatSetHelper.setShapeType(shapeType);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarRadius(int radius) {
        chatSetHelper.setAvatarRadius(radius);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarBorderWidth(int borderWidth) {
        chatSetHelper.setBorderWidth(borderWidth);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarBorderColor(int borderColor) {
        chatSetHelper.setBorderColor(borderColor);
        notifyDataSetChanged();
    }

    @Override
    public void addHeaderAdapter(RecyclerView.Adapter adapter) {
        baseAdapter.addAdapter(0, adapter);
    }

    @Override
    public void addFooterAdapter(RecyclerView.Adapter adapter) {
        baseAdapter.addAdapter(adapter);
    }

    @Override
    public void removeAdapter(RecyclerView.Adapter adapter) {
        baseAdapter.removeAdapter(adapter);
    }

    @Override
    public void addRVItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        rvList.addItemDecoration(decor);
    }

    @Override
    public void removeRVItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        rvList.removeItemDecoration(decor);
    }

    /**
     * 是否有新的消息
     * 判断依据为：数据库中最新的一条数据的时间戳是否大于页面上的最新一条数据的时间戳
     * @return
     */
    public boolean haveNewMessages() {
        if(messageAdapter == null || messageAdapter.getData() == null || messageAdapter.getData().isEmpty()
                || conversation == null || conversation.getLastMessage() == null) {
            return false;
        }
        return conversation.getLastMessage().getMsgTime() > messageAdapter.getData().get(messageAdapter.getData().size() - 1).getMsgTime();
    }

    /**
     * 移动到指定位置
     * @param position
     */
    private void seekToPosition(int position) {
        if(presenter.isDestroy() || rvList == null) {
            return;
        }
        if(position < 0) {
            position = 0;
        }
        int finalPosition = position;
        rvList.post(()-> {
            setMoveAnimation(layoutManager, finalPosition);
        });
    }

    private void setMoveAnimation(RecyclerView.LayoutManager manager, int position) {
        ValueAnimator animator = ValueAnimator.ofInt(-200, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ((LinearLayoutManager)manager).scrollToPositionWithOffset(position, value);
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void setPresenter(EaseChatMessagePresenter presenter) {
        this.presenter = presenter;
        if(getContext() instanceof AppCompatActivity) {
            ((AppCompatActivity) getContext()).getLifecycle().addObserver(presenter);
        }
        this.presenter.attachView(this);
        this.presenter.setupWithConversation(conversation);
    }

    @Override
    public EaseMessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOnMessageTouchListener(OnMessageTouchListener listener) {
        this.messageTouchListener = listener;
    }

    @Override
    public void setOnChatErrorListener(OnChatErrorListener listener) {
        this.errorListener = listener;
    }

    @Override
    public void setMessageListItemClickListener(MessageListItemClickListener listener) {
        this.messageListItemClickListener = listener;
    }

    public void runOnUi(Runnable runnable) {
        EaseThreadManager.getInstance().runOnMainThread(runnable);
    }

    /**
     * 消息列表接口
     */
    public interface OnMessageTouchListener {
        /**
         * touch事件
         * @param v
         * @param position
         */
        void onTouchItemOutside(View v, int position);

        /**
         * 控件正在被拖拽
         */
        void onViewDragging();
    }

    public interface OnChatErrorListener {
        /**
         * 聊天中错误信息
         * @param code
         * @param errorMsg
         */
        void onChatError(int code, String errorMsg);
    }

    public enum LoadDataType {
        LOCAL, ROAM, HISTORY
    }

    /**
     * 加载更多的状态
     */
    public enum LoadMoreStatus {
        IS_LOADING, HAS_MORE, NO_MORE_DATA
    }
}

