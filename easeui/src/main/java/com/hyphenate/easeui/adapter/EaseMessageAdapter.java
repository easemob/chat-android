package com.hyphenate.easeui.adapter;

import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.interfaces.IViewHolderProvider;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.viewholder.EaseViewHolderHelper;

/**
 * 开发者可在实现{@link IViewHolderProvider}提供相应的ViewHolder及ViewType
 * ViewHolder的提供主要通过{@link EaseViewHolderHelper}
 */
public class EaseMessageAdapter extends EaseBaseMessageAdapter<EMMessage> {

    public EaseMessageAdapter(IViewHolderProvider provider) {
        this.viewHolderProvider = provider;
        itemStyle = createDefaultItemStyle();
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_empty_list_invisible;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return createItemViewHolder(parent, viewType);
    }

    private ViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        if(viewHolderProvider != null) {
            EaseChatRowViewHolder viewHolder = viewHolderProvider.provideViewHolder(parent, viewType, itemClickListener, itemStyle);
            if(viewHolder != null) {
                return viewHolder;
            }
        }
        return EaseViewHolderHelper.getInstance().getChatRowViewHolder(parent, viewType, itemClickListener, itemStyle);
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItemMessage(position);
        if(message == null) {
            return super.getItemViewType(position);
        }
        if(viewHolderProvider != null) {
            int type = viewHolderProvider.provideViewType(message);
            if(type != 0) {
                return type;
            }
        }
        int viewType = EaseViewHolderHelper.getInstance().getAdapterViewType(message);
        return viewType == 0 ? super.getItemViewType(position) : viewType;
    }


    /**
     * get item message
     * @param position
     * @return
     */
    private EMMessage getItemMessage(int position) {
        if(mData != null) {
            return mData.get(position);
        }
        return null;
    }

}
