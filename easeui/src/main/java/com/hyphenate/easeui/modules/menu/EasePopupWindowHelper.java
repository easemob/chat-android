package com.hyphenate.easeui.modules.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EasePopupWindowHelper {
    public static final int ACTION_COPY = 11;
    public static final int ACTION_FORWARD = 12;
    public static final int ACTION_DELETE = 13;
    public static final int ACTION_RECALL = 14;
    public static final int ACTION_MULTI_SELECT = 15;
    private static final int[] itemIds = {ACTION_COPY, ACTION_FORWARD, ACTION_DELETE, ACTION_RECALL};
    private static final int[] titles = {R.string.action_copy, R.string.action_forward, R.string.action_delete, R.string.action_recall, R.string.action_multi_select};
    private static final int[] icons = {R.drawable.ease_delete_expression, R.drawable.ease_delete_expression, R.drawable.ease_delete_expression
                                        , R.drawable.ease_delete_expression, R.drawable.ease_delete_expression};
    private static final int SPAN_COUNT = 5;
    private EasePopupWindow pMenu;
    private List<MenuItemBean> menuItems = new ArrayList<>();
    private Map<Integer, MenuItemBean> menuItemMap = new HashMap<>();
    private TextView tvTitle;
    private RecyclerView rvMenuList;
    private Context context;
    private MenuAdapter adapter;
    private EasePopupWindow.OnPopupWindowItemClickListener itemClickListener;
    private EasePopupWindow.OnPopupWindowDismissListener dismissListener;
    private boolean touchable;
    private Drawable background;
    private View layout;

    public EasePopupWindowHelper() {
        if(pMenu != null) {
            pMenu.dismiss();
        }
        menuItems.clear();
        menuItemMap.clear();
    }

    /**
     * @param context
     */
    public void initMenu(@NonNull Context context) {
        this.context = context;
        pMenu = new EasePopupWindow(context, true);
        layout = LayoutInflater.from(context).inflate(R.layout.ease_layout_menu_popupwindow, null);
        pMenu.setContentView(layout);
        tvTitle = layout.findViewById(R.id.tv_title);
        rvMenuList = layout.findViewById(R.id.rv_menu_list);
        adapter = new MenuAdapter();
        rvMenuList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                if(itemClickListener != null) {
                    itemClickListener.onMenuItemClick(adapter.getItem(position));
                }
            }
        });
    }

    public void clear() {
        menuItems.clear();
        menuItemMap.clear();
    }

    public void setDefaultMenus() {
        MenuItemBean bean;
        for(int i = 0; i < itemIds.length; i++) {
            bean = new MenuItemBean(0, itemIds[i], i, context.getString(titles[i]));
            bean.setResourceId(icons[i]);
            addItemMenu(bean);
        }
    }

    public void addItemMenu(MenuItemBean item) {
        if(!menuItemMap.containsKey(item.getItemId())) {
            menuItemMap.put(item.getItemId(), item);
        }
    }

    public void addItemMenu(int groupId, int itemId, int order, String title) {
        MenuItemBean item = new MenuItemBean(groupId, itemId, order, title);
        addItemMenu(item);
    }

    public MenuItemBean findItem(int id) {
        if(menuItemMap.containsKey(id)) {
            return menuItemMap.get(id);
        }
        return null;
    }

    public void findItemVisible(int id, boolean visible) {
        if(menuItemMap.containsKey(id)) {
            menuItemMap.get(id).setVisible(visible);
        }
    }

    public void setOutsideTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.background = background;
    }

    private void showPre() {
        pMenu.setOutsideTouchable(touchable);
        pMenu.setBackgroundDrawable(background);
        checkIfShowItems();
        adapter.setData(menuItems);
    }

    private void checkIfShowItems() {
        if(menuItemMap.size() > 0) {
            menuItems.clear();
            Iterator<MenuItemBean> iterator = menuItemMap.values().iterator();
            while (iterator.hasNext()) {
                MenuItemBean item = iterator.next();
                if(item.isVisible()) {
                    menuItems.add(item);
                }
            }
        }
    }

    public void showTitle(@NonNull String title) {
        if(pMenu == null) {
            throw new NullPointerException("please must init first!");
        }
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void show(View parent, View v) {
        showPre();
        //根据条目选择spanCount
        if(menuItems.size() < SPAN_COUNT) {
            rvMenuList.setLayoutManager(new GridLayoutManager(context, menuItems.size(), RecyclerView.VERTICAL, false));
        }else {
            rvMenuList.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT, RecyclerView.VERTICAL, false));
        }


        getView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = getView().getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = getView().getMeasuredHeight();  //获取测量后的高度

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        int[] location2 = new int[2];
        parent.getLocationOnScreen(location2);
        int margin = (int) EaseCommonUtils.dip2px(context, 5);

        int yOffset = 0;
        if(location[1] - popupHeight - margin < location2[1]) {
            yOffset = location[1] + v.getHeight() + margin;
        }else {
            yOffset = location[1] - popupHeight - margin;

        }
        int xOffset = 0;
        if(location[0] + v.getWidth() / 2 + popupWidth / 2 + EaseCommonUtils.dip2px(context, 10) > parent.getWidth()) {
            xOffset = (int) (parent.getWidth() - EaseCommonUtils.dip2px(context, 10) - popupWidth);
        }else {
            xOffset = location[0] + v.getWidth() / 2 - popupWidth / 2;
        }
        pMenu.showAtLocation(v, Gravity.NO_GRAVITY, xOffset, yOffset);
    }

    public void dismiss() {
        if(pMenu == null) {
            throw new NullPointerException("please must init first!");
        }
        pMenu.dismiss();
        if(dismissListener != null) {
            dismissListener.onDismiss(pMenu);
        }
    }


    /**
     * 设置条目点击事件
     * @param listener
     */
    public void setOnPopupMenuItemClickListener(EasePopupWindow.OnPopupWindowItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * 监听PopupMenu dismiss事件
     * @param listener
     */
    public void setOnPopupMenuDismissListener(EasePopupWindow.OnPopupWindowDismissListener listener) {
        this.dismissListener = listener;
    }

    public PopupWindow getPopupWindow() {
        return pMenu;
    }

    public View getView() {
        return layout;
    }

    private class MenuAdapter extends EaseBaseRecyclerViewAdapter<MenuItemBean> {

        @Override
        public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.ease_layout_item_menu_popupwindow, parent, false);
            return new MenuViewHolder(view);
        }

        private class MenuViewHolder extends ViewHolder<MenuItemBean> {
            private ImageView ivActionIcon;
            private TextView tvActionName;

            public MenuViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                ivActionIcon = findViewById(R.id.iv_action_icon);
                tvActionName = findViewById(R.id.tv_action_name);
            }

            @Override
            public void setData(MenuItemBean item, int position) {
                String title = item.getTitle();
                if(!TextUtils.isEmpty(title)) {
                    tvActionName.setText(title);
                }
                if(item.getResourceId() != 0) {
                    ivActionIcon.setImageResource(item.getResourceId());
                }
            }
        }
    }
}

