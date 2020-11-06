package com.hyphenate.easeui.modules.conversation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseAdapterDelegate;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.EaseBaseLayout;
import com.hyphenate.easeui.modules.conversation.delegate.EaseBaseConversationDelegate;
import com.hyphenate.easeui.modules.conversation.delegate.EaseConversationDelegate;
import com.hyphenate.easeui.modules.conversation.interfaces.IConversationListLayout;
import com.hyphenate.easeui.modules.conversation.interfaces.IConversationStyle;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetModel;
import com.hyphenate.easeui.widget.EaseRecyclerView;

import java.util.List;


/**
 * 会话列表
 */
public class EaseConversationListLayout extends EaseBaseLayout implements IConversationListLayout, IConversationStyle, IEaseConversationListView {
    private EaseRecyclerView rvConversationList;

    private ConcatAdapter adapter;
    private EaseConversationListAdapter listAdapter;
    private OnItemClickListener itemListener;
    private OnItemLongClickListener itemLongListener;
    private EaseConversationSetModel setModel;

    private EaseConversationPresenterImpl presenter;

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

            Drawable avatarDefaultDrawable = a.getDrawable(R.styleable.EaseConversationListLayout_ease_con_item_avatar_default_src);
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
            setModel.setAvatarDefaultSrc(avatarDefaultDrawable);
            setModel.setAvatarSize(avatarSize);
            setModel.setShapeType(shapeType);
            setModel.setAvatarRadius(avatarRadius);
            setModel.setBorderWidth(borderWidth);
            setModel.setBorderColor(borderColor);

            float itemHeight = a.getDimension(R.styleable.EaseConversationListLayout_ease_con_item_height, dip2px(context, 75));
            Drawable bgDrawable = a.getDrawable(R.styleable.EaseConversationListLayout_ease_con_item_background);
            setModel.setItemHeight(itemHeight);
            setModel.setBgDrawable(bgDrawable);
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

        initListener();
    }

    private void initListener() {
        listAdapter.setOnItemClickListener(new com.hyphenate.easeui.interfaces.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(itemListener != null) {
                    itemListener.onItemClick(view, position, listAdapter.getItem(position));
                }
            }
        });

        listAdapter.setOnItemLongClickListener(new com.hyphenate.easeui.interfaces.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                if(itemLongListener != null) {
                    itemLongListener.onItemLongClick(view, position, listAdapter.getItem(position));
                    return true;
                }
                return false;
            }
        });
    }

    public void init() {
        listAdapter.addDelegate(new EaseConversationDelegate(setModel));
        rvConversationList.setAdapter(adapter);
    }

    public void loadDefaultData() {
        presenter.loadDefaultData();
    }

    public void setData(List<EaseConversationInfo> data) {
        presenter.loadData(data);
    }

    public void addData(List<EaseConversationInfo> data) {
        if(data != null) {
            List<EaseConversationInfo> infos = listAdapter.getData();
            infos.addAll(data);
            presenter.loadData(infos);
        }
    }

    private void notifyDataSetChanged() {
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

    @Override
    public void addTopView(View top) {
        if(top != null) {
            this.addView(top, 0);
        }
    }

    @Override
    public void addBottomView(View bottom) {
        if(bottom != null) {
            this.addView(bottom);
        }
    }

    @Override
    public void addDelegate(EaseBaseConversationDelegate delegate) {
        delegate.setSetModel(setModel);
        listAdapter.addDelegate(delegate);
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
        setModel.setAvatarDefaultSrc(src);
        notifyDataSetChanged();
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
        listAdapter.setData(data);
    }

    @Override
    public void loadConversationListNoData() {

    }

    @Override
    public void loadConversationListFail(String message) {

    }

    public EaseConversationListAdapter getListAdapter() {
        return listAdapter;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, EaseConversationInfo bean);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position, EaseConversationInfo bean);
    }
}

