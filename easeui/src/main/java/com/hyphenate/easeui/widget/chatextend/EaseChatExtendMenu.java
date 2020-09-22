package com.hyphenate.easeui.widget.chatextend;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseChatExtendMenuAdapter;
import com.hyphenate.easeui.adapter.EaseChatExtendMenuIndicatorAdapter;
import com.hyphenate.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Extend menu when user want send image, voice clip, etc
 *
 */
public class EaseChatExtendMenu extends FrameLayout implements PagingScrollHelper.onPageChangeListener {
    protected Context context;
    private RecyclerView rvExtendMenu;
    private RecyclerView rvIndicator;
    private List<ChatMenuItemModel> itemModels = new ArrayList<ChatMenuItemModel>();
    private EaseChatExtendMenuAdapter adapter;
    private int numColumns;
    private int numRows;
    private PagingScrollHelper helper;
    private EaseChatExtendMenuItemClickListener listener;
    private EaseChatExtendMenuIndicatorAdapter indicatorAdapter;

    public EaseChatExtendMenu(Context context) {
        this(context, null);
    }

    public EaseChatExtendMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatExtendMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        initLayout();
    }

    private void init(Context context, AttributeSet attrs){
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatExtendMenu);
        numColumns = ta.getInt(R.styleable.EaseChatExtendMenu_numColumns, 4);
        numRows = ta.getInt(R.styleable.EaseChatExtendMenu_numRows, 2);
        ta.recycle();
    }

    private void initLayout() {
        LayoutInflater.from(context).inflate(R.layout.ease_layout_chat_extend_menu, this);
        rvExtendMenu = findViewById(R.id.rv_extend_menu);
        rvIndicator = findViewById(R.id.rv_indicator);
    }

    
    /**
     * init
     */
    public void init(){
        initChatExtendMenu();
        initChatExtendMenuIndicator();
    }

    private void initChatExtendMenu() {
        HorizontalPageLayoutManager manager = new HorizontalPageLayoutManager(numRows, numColumns);
        manager.setItemHeight(DensityUtil.dip2px(context, 90));
        rvExtendMenu.setLayoutManager(manager);
        rvExtendMenu.setHasFixedSize(true);
        ConcatAdapter concatAdapter = new ConcatAdapter();
        adapter = new EaseChatExtendMenuAdapter();
        concatAdapter.addAdapter(adapter);
        rvExtendMenu.setAdapter(concatAdapter);
        adapter.setData(itemModels);

        helper = new PagingScrollHelper();
        helper.setUpRecycleView(rvExtendMenu);
        helper.updateLayoutManger();
        helper.scrollToPosition(0);
        setHorizontalFadingEdgeEnabled(true);
        helper.setOnPageChangeListener(this);
    }

    private void initChatExtendMenuIndicator() {
        indicatorAdapter = new EaseChatExtendMenuIndicatorAdapter();
        rvIndicator.setAdapter(indicatorAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.ease_chat_extend_menu_indicator_divider));
        rvIndicator.addItemDecoration(itemDecoration);
    }

    /**
     * register menu item
     * 
     * @param name
     *            item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerMenuItem(String name, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        ChatMenuItemModel item = new ChatMenuItemModel();
        item.name = name;
        item.image = drawableRes;
        item.id = itemId;
        item.clickListener = listener;
        itemModels.add(item);
        adapter.notifyItemInserted(itemModels.size() - 1);
        //设置需要显示的indicator的个数
        indicatorAdapter.setPageCount((int) Math.ceil(itemModels.size() / (numColumns * numRows)));
    }

    /**
     * register menu item
     * 
     * @param nameRes
     *            resource id of item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *             on click event of item
     */
    public void registerMenuItem(int nameRes, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        registerMenuItem(context.getString(nameRes), drawableRes, itemId, listener);
    }

    @Override
    public void onPageChange(int index) {
        //设置选中的indicator
        indicatorAdapter.setSelectedPosition(index);
    }

    /**
     * extend menu item click listener
     */
    public interface EaseChatExtendMenuItemClickListener{
        /**
         * item click
         * @param itemId
         * @param view
         */
        void onChatExtendMenuItemClick(int itemId, View view);
    }
    
    public static class ChatMenuItemModel{
        public String name;
        public int image;
        public int id;
        public EaseChatExtendMenuItemClickListener clickListener;
    }
    
    class ChatMenuItem extends LinearLayout {
        private ImageView imageView;
        private TextView textView;

        public ChatMenuItem(Context context, AttributeSet attrs, int defStyle) {
            this(context, attrs);
        }

        public ChatMenuItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        public ChatMenuItem(Context context) {
            super(context);
            init(context, null);
        }

        private void init(Context context, AttributeSet attrs) {
            LayoutInflater.from(context).inflate(R.layout.ease_chat_menu_item, this);
            imageView = (ImageView) findViewById(R.id.image);
            textView = (TextView) findViewById(R.id.text);
        }

        public void setImage(int resid) {
            imageView.setBackgroundResource(resid);
        }

        public void setText(int resid) {
            textView.setText(resid);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }
}
