package com.hyphenate.easeui.modules.conversation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.interfaces.OnItemLongClickListener;
import com.hyphenate.easeui.modules.conversation.interfaces.OnConversationChangeListener;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.EaseBaseLayout;
import com.hyphenate.easeui.modules.conversation.delegate.EaseBaseConversationDelegate;
import com.hyphenate.easeui.modules.conversation.delegate.EaseConversationDelegate;
import com.hyphenate.easeui.modules.conversation.interfaces.IConversationListLayout;
import com.hyphenate.easeui.modules.conversation.interfaces.IConversationStyle;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetModel;
import com.hyphenate.easeui.modules.interfaces.IPopupMenu;
import com.hyphenate.easeui.modules.menu.OnPopupMenuDismissListener;
import com.hyphenate.easeui.modules.menu.OnPopupMenuItemClickListener;
import com.hyphenate.easeui.modules.menu.PopupMenuHelper;
import com.hyphenate.easeui.widget.EaseRecyclerView;

import java.util.List;


/**
 * 会话列表
 */
public class EaseConversationListLayout extends EaseBaseLayout implements IConversationListLayout, IConversationStyle
                                                                        , IEaseConversationListView, IPopupMenu {
    private static final int MENU_MAKE_READ = 0;
    private static final int MENU_MAKE_TOP = 1;
    private static final int MENU_MAKE_CANCEL_TOP = 2;
    private static final int MENU_DELETE = 3;
    private EaseRecyclerView rvConversationList;

    private ConcatAdapter adapter;
    private EaseConversationListAdapter listAdapter;
    private OnItemClickListener itemListener;
    private OnItemLongClickListener itemLongListener;
    private OnPopupMenuItemClickListener popupMenuItemClickListener;
    private OnPopupMenuDismissListener dismissListener;
    private EaseConversationSetModel setModel;

    private EaseConversationPresenter presenter;
    private float touchX;
    private float touchY;
    private PopupMenuHelper menuHelper;
    private boolean showDefaultMenu = true;
    private OnConversationChangeListener conversationChangeListener;

    public EaseConversationListLayout(Context context) {
        this(context, null);
    }

    public EaseConversationListLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseConversationListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setModel = new EaseConversationSetModel();
        LayoutInflater.from(context).inflate(R.layout.ease_conversation_list, this);
        presenter = new EaseConversationPresenterImpl();
        if(context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getLifecycle().addObserver(presenter);
        }
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EaseConversationListLayout);
            float titleTextSize = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_title_text_size
                    , sp2px(context, 16));
            setModel.setTitleTextSize(titleTextSize);
            int titleTextColorRes = a.getResourceId(R.styleable.EaseConversationListLayout_ease_con_item_title_text_color, -1);
            int titleTextColor;
            if(titleTextColorRes != -1) {
                titleTextColor = ContextCompat.getColor(context, titleTextColorRes);
            }else {
                titleTextColor = a.getColor(R.styleable.EaseConversationListLayout_ease_con_item_title_text_color
                        , ContextCompat.getColor(context, R.color.ease_conversation_color_item_name));
            }
            setModel.setTitleTextColor(titleTextColor);

            float contentTextSize = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_content_text_size
                    , sp2px(context, 14));
            setModel.setContentTextSize(contentTextSize);
            int contentTextColorRes = a.getResourceId(R.styleable.EaseConversationListLayout_ease_con_item_content_text_color, -1);
            int contentTextColor;
            if(contentTextColorRes != -1) {
                contentTextColor = ContextCompat.getColor(context, contentTextColorRes);
            }else {
                contentTextColor = a.getColor(R.styleable.EaseConversationListLayout_ease_con_item_content_text_color
                        , ContextCompat.getColor(context, R.color.ease_conversation_color_item_message));
            }
            setModel.setContentTextColor(contentTextColor);

            float dateTextSize = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_date_text_size
                    , sp2px(context, 13));
            setModel.setDateTextSize(dateTextSize);
            int dateTextColorRes = a.getResourceId(R.styleable.EaseConversationListLayout_ease_con_item_date_text_color, -1);
            int dateTextColor;
            if(dateTextColorRes != -1) {
                dateTextColor = ContextCompat.getColor(context, dateTextColorRes);
            }else {
                dateTextColor = a.getColor(R.styleable.EaseConversationListLayout_ease_con_item_date_text_color
                        , ContextCompat.getColor(context, R.color.ease_conversation_color_item_time));
            }
            setModel.setDateTextColor(dateTextColor);

            float mentionTextSize = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_mention_text_size
                    , sp2px(context, 14));
            setModel.setMentionTextSize(mentionTextSize);
            int mentionTextColorRes = a.getResourceId(R.styleable.EaseConversationListLayout_ease_con_item_mention_text_color, -1);
            int mentionTextColor;
            if(mentionTextColorRes != -1) {
                mentionTextColor = ContextCompat.getColor(context, mentionTextColorRes);
            }else {
                mentionTextColor = a.getColor(R.styleable.EaseConversationListLayout_ease_con_item_mention_text_color
                        , ContextCompat.getColor(context, R.color.ease_conversation_color_item_mention));
            }
            setModel.setMentionTextColor(mentionTextColor);

            float avatarSize = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_avatar_size, 0);
            int shapeType = a.getInteger(R.styleable.EaseConversationListLayout_ease_con_item_avatar_shape_type, 0);
            float avatarRadius = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_avatar_radius, dip2px(context, 50));
            float borderWidth = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_avatar_border_width, 0);
            int borderColorRes = a.getResourceId(R.styleable.EaseConversationListLayout_ease_con_item_avatar_border_color, -1);
            int borderColor;
            if(borderColorRes != -1) {
                borderColor = ContextCompat.getColor(context, borderColorRes);
            }else {
                borderColor = a.getColor(R.styleable.EaseConversationListLayout_ease_con_item_avatar_border_color, Color.TRANSPARENT);
            }
            setModel.setAvatarSize(avatarSize);
            setModel.setShapeType(shapeType);
            setModel.setAvatarRadius(avatarRadius);
            setModel.setBorderWidth(borderWidth);
            setModel.setBorderColor(borderColor);

            float itemHeight = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_height, dip2px(context, 75));
            Drawable bgDrawable = a.getDrawable(R.styleable.EaseConversationListLayout_ease_con_item_background);
            setModel.setItemHeight(itemHeight);
            setModel.setBgDrawable(bgDrawable);

            int unreadDotPosition = a.getInteger(R.styleable.EaseConversationListLayout_ease_con_item_unread_dot_position, 0);
            setModel.setUnreadDotPosition(unreadDotPosition == 0 ? EaseConversationSetModel.UnreadDotPosition.LEFT
                                                                    : EaseConversationSetModel.UnreadDotPosition.RIGHT);

            boolean showSystemMessage = a.getBoolean(R.styleable.EaseConversationListLayout_ease_con_item_show_system_message, true);
            setModel.setShowSystemMessage(showSystemMessage);
            presenter.setShowSystemMessage(showSystemMessage);

            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.attachView(this);

        rvConversationList = findViewById(R.id.rv_conversation_list);

        rvConversationList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ConcatAdapter();
        listAdapter = new EaseConversationListAdapter();
        adapter.addAdapter(listAdapter);

        menuHelper = new PopupMenuHelper();

        initListener();
    }

    private void initListener() {
        listAdapter.setOnItemClickListener(new com.hyphenate.easeui.interfaces.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(itemListener != null) {
                    itemListener.onItemClick(view, position);
                }
            }
        });

        listAdapter.setOnItemLongClickListener(new com.hyphenate.easeui.interfaces.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                listAdapter.getItem(position).setSelected(true);
                if(itemLongListener != null) {
                    if(showDefaultMenu) {
                        showDefaultMenu(view, position, listAdapter.getItem(position));
                    }
                    itemLongListener.onItemLongClick(view, position);
                    return true;
                }
                if(showDefaultMenu) {
                    showDefaultMenu(view, position, listAdapter.getItem(position));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchX = ev.getX();
        touchY = ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    public void init() {
        listAdapter.addDelegate(new EaseConversationDelegate(setModel));
        rvConversationList.setAdapter(adapter);
    }

    public void loadDefaultData() {
        presenter.loadData();
    }

    /**
     * 设置数据
     * @param data
     */
    public void setData(List<EaseConversationInfo> data) {
        presenter.sortData(data);
    }

    /**
     * 添加数据
     * @param data
     */
    public void addData(List<EaseConversationInfo> data) {
        if(data != null) {
            List<EaseConversationInfo> infos = listAdapter.getData();
            infos.addAll(data);
            presenter.sortData(infos);
        }
    }

    /**
     * 刷新数据
     */
    public void notifyDataSetChanged() {
        if(listAdapter != null) {
            List<EaseAdapterDelegate<Object, EaseBaseRecyclerViewAdapter.ViewHolder>> delegates = listAdapter.getAllDelegate();
            if (delegates != null && !delegates.isEmpty()) {
                for(int i = 0; i < delegates.size(); i++) {
                    EaseBaseConversationDelegate delegate = (EaseBaseConversationDelegate) delegates.get(i);
                    delegate.setSetModel(setModel);
                }
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 返回触摸点的x坐标
     * @return
     */
    public float getTouchX() {
        return touchX;
    }

    /**
     * 返回触摸点的y坐标
     * @return
     */
    public float getTouchY() {
        return touchY;
    }

    private void showDefaultMenu(View view, int position, EaseConversationInfo info) {
        menuHelper.addItemMenu(Menu.NONE, MENU_MAKE_READ, 0, getContext().getString(R.string.ease_conversation_menu_make_read));
        menuHelper.addItemMenu(Menu.NONE, MENU_MAKE_TOP, 1, getContext().getString(R.string.ease_conversation_menu_make_top));
        menuHelper.addItemMenu(Menu.NONE, MENU_MAKE_CANCEL_TOP, 2, getContext().getString(R.string.ease_conversation_menu_cancel_top));
        menuHelper.addItemMenu(Menu.NONE, MENU_DELETE, 3, getContext().getString(R.string.ease_conversation_menu_delete));

        menuHelper.initMenu(view);

        //检查置顶配置
        menuHelper.findItemVisible(MENU_MAKE_TOP, !info.isTop());
        menuHelper.findItemVisible(MENU_MAKE_CANCEL_TOP, info.isTop());
        //检查已读配置
        if(info.getInfo() instanceof EMConversation) {
            menuHelper.findItemVisible(MENU_MAKE_READ, ((EMConversation) info.getInfo()).getUnreadMsgCount() > 0);
        }

        menuHelper.setOnPopupMenuItemClickListener(new OnPopupMenuItemClickListener() {
            @Override
            public void onMenuItemClick(MenuItem item) {
                if(showDefaultMenu) {
                    switch (item.getItemId()) {
                        case MENU_MAKE_READ :
                            presenter.makeConversionRead(position, info);
                            break;
                        case MENU_MAKE_TOP :
                            presenter.makeConversationTop(position, info);
                            break;
                        case MENU_MAKE_CANCEL_TOP :
                            presenter.cancelConversationTop(position, info);
                            break;
                        case MENU_DELETE :
                            presenter.deleteConversation(position, info);
                            break;
                    }
                }
                if(popupMenuItemClickListener != null) {
                    popupMenuItemClickListener.onMenuItemClick(item);
                }
            }
        });

        menuHelper.setOnPopupMenuDismissListener(new OnPopupMenuDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                info.setSelected(false);
                if(dismissListener != null) {
                    dismissListener.onDismiss(menu);
                }
            }
        });

        menuHelper.show((int) getTouchX(), 0);
    }

    @Override
    public void addHeaderAdapter(RecyclerView.Adapter adapter) {
        this.adapter.addAdapter(0, adapter);
    }

    @Override
    public void addFooterAdapter(RecyclerView.Adapter adapter) {
        this.adapter.addAdapter(adapter);
    }

    @Override
    public void removeAdapter(RecyclerView.Adapter adapter) {
        this.adapter.removeAdapter(adapter);
    }

//    @Override
//    public void addDelegate(EaseBaseConversationDelegate delegate) {
//        delegate.setSetModel(setModel);
//        listAdapter.addDelegate(delegate);
//    }

    @Override
    public void setPresenter(EaseConversationPresenter presenter) {
        this.presenter = presenter;
        if(getContext() instanceof AppCompatActivity) {
            ((AppCompatActivity) getContext()).getLifecycle().addObserver(presenter);
        }
        this.presenter.setShowSystemMessage(setModel.isShowSystemMessage());
        this.presenter.attachView(this);
    }

    @Override
    public void showItemDefaultMenu(boolean showDefault) {
        showDefaultMenu = showDefault;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongListener = listener;
    }

    @Override
    public void setItemBackGround(Drawable backGround) {
        setModel.setBgDrawable(backGround);
        notifyDataSetChanged();
    }

    @Override
    public void setItemHeight(int height) {
        setModel.setItemHeight(height);
        notifyDataSetChanged();
    }

    @Override
    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        rvConversationList.addItemDecoration(decor);
    }

    @Override
    public void removeItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        rvConversationList.removeItemDecoration(decor);
    }

    @Override
    public void hideUnreadDot(boolean hide) {
        setModel.setHideUnreadDot(hide);
        notifyDataSetChanged();
    }

    @Override
    public void showUnreadDotPosition(EaseConversationSetModel.UnreadDotPosition position) {
        setModel.setUnreadDotPosition(position);
        notifyDataSetChanged();
    }

    @Override
    public void setTitleTextSize(int textSize) {
        setModel.setTitleTextSize(textSize);
        notifyDataSetChanged();
    }

    @Override
    public void setTitleTextColor(int textColor) {
        setModel.setTitleTextColor(textColor);
        notifyDataSetChanged();
    }

    @Override
    public void setContentTextSize(int textSize) {
        setModel.setContentTextSize(textSize);
        notifyDataSetChanged();
    }

    @Override
    public void setContentTextColor(int textColor) {
        setModel.setContentTextColor(textColor);
        notifyDataSetChanged();
    }

    @Override
    public void setDateTextSize(int textSize) {
        setModel.setDateTextSize(textSize);
        notifyDataSetChanged();
    }

    @Override
    public void setDateTextColor(int textColor) {
        setModel.setDateTextColor(textColor);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarDefaultSrc(Drawable src) {

    }

    @Override
    public void setAvatarSize(float avatarSize) {
        setModel.setAvatarSize(avatarSize);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarShapeType(int shapeType) {
        setModel.setShapeType(shapeType);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarRadius(int radius) {
        setModel.setAvatarRadius(radius);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarBorderWidth(int borderWidth) {
        setModel.setBorderWidth(borderWidth);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarBorderColor(int borderColor) {
        setModel.setBorderColor(borderColor);
        notifyDataSetChanged();
    }

    @Override
    public void loadConversationListSuccess(List<EaseConversationInfo> data) {
        presenter.sortData(data);
    }

    @Override
    public void loadConversationListNoData() {

    }

    @Override
    public void loadConversationListFail(String message) {

    }

    @Override
    public void sortConversationListSuccess(List<EaseConversationInfo> data) {
        listAdapter.setData(data);
    }

    @Override
    public void refreshList() {
        if(conversationChangeListener != null) {
            conversationChangeListener.notifyAllChange();
        }
        presenter.sortData(listAdapter.getData());
    }

    @Override
    public void refreshList(int position) {
        if(conversationChangeListener != null) {
            conversationChangeListener.notifyItemChange(position);
        }
        listAdapter.notifyItemChanged(position);
    }

    @Override
    public void deleteItem(int position) {
        if(conversationChangeListener != null) {
            conversationChangeListener.notifyItemRemove(position);
        }
        listAdapter.notifyItemRemoved(position);
    }

    @Override
    public void deleteItemFail(int position, String message) {
        Toast.makeText(getContext(), R.string.ease_conversation_delete_item_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public EaseConversationListAdapter getListAdapter() {
        return listAdapter;
    }

    @Override
    public EaseConversationInfo getItem(int position) {
        if(position >= listAdapter.getData().size()) {
            throw new ArrayIndexOutOfBoundsException(position);
        }
        return listAdapter.getItem(position);
    }

    @Override
    public void makeConversionRead(int position, EaseConversationInfo info) {
        presenter.makeConversionRead(position, info);
    }

    @Override
    public void makeConversationTop(int position, EaseConversationInfo info) {
        presenter.makeConversationTop(position, info);
    }

    @Override
    public void cancelConversationTop(int position, EaseConversationInfo info) {
        presenter.cancelConversationTop(position, info);
    }

    @Override
    public void deleteConversation(int position, EaseConversationInfo info) {
        presenter.deleteConversation(position, info);
    }

    @Override
    public void setOnConversationChangeListener(OnConversationChangeListener listener) {
        this.conversationChangeListener = listener;
    }

    @Override
    public void clearMenu() {
        menuHelper.clear();
    }

    @Override
    public void addItemMenu(int groupId, int itemId, int order, String title) {
        menuHelper.addItemMenu(groupId, itemId, order, title);
    }

    @Override
    public void findItemVisible(int id, boolean visible) {
        menuHelper.findItemVisible(id, visible);
    }

    @Override
    public void setOnPopupMenuItemClickListener(OnPopupMenuItemClickListener listener) {
        popupMenuItemClickListener = listener;
    }

    @Override
    public void setOnPopupMenuDismissListener(OnPopupMenuDismissListener listener) {
        dismissListener = listener;
    }

    @Override
    public PopupMenuHelper getMenuHelper() {
        return menuHelper;
    }

    @Override
    public Context context() {
        return getContext();
    }
}

