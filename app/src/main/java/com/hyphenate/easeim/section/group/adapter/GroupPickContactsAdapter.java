package com.hyphenate.easeim.section.group.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class GroupPickContactsAdapter extends EaseBaseRecyclerViewAdapter<EaseUser> {
    private List<String> existMembers;
    private List<String> selectedMembers;
    private boolean isCreateGroup;
    private OnSelectListener listener;

    public GroupPickContactsAdapter() {
        this.isCreateGroup = false;
        selectedMembers = new ArrayList<>();
    }

    public GroupPickContactsAdapter(boolean isCreateGroup) {
        this.isCreateGroup = isCreateGroup;
        selectedMembers = new ArrayList<>();
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_layout_item_pick_contact_with_checkbox, parent, false));
    }

    /**
     * 不设置条目点击事件
     * @return
     */
    @Override
    public boolean isItemClickEnable() {
        return false;
    }

    public void setExistMember(List<String> existMembers) {
        this.existMembers = existMembers;
        if(isCreateGroup) {
            selectedMembers.clear();
            selectedMembers.addAll(existMembers);
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedMembers() {
        return selectedMembers;
    }

    public class ContactViewHolder extends ViewHolder<EaseUser> {
        private TextView headerView;
        private CheckBox checkbox;
        private EaseImageView avatar;
        private TextView name;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            headerView = findViewById(R.id.header);
            checkbox = findViewById(R.id.checkbox);
            avatar = findViewById(R.id.avatar);
            name = findViewById(R.id.name);
            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(EaseUser item, int position) {
            String username = getRealUsername(item.getUsername());
            name.setText(item.getNickname());
            //Glide.with(mContext).load(R.drawable.ease_default_avatar).into(avatar);
            avatar.setImageResource(R.drawable.ease_default_avatar);
            String header = item.getInitialLetter();

            if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
                if (TextUtils.isEmpty(header)) {
                    headerView.setVisibility(View.GONE);
                } else {
                    headerView.setVisibility(View.VISIBLE);
                    headerView.setText(header);
                }
            } else {
                headerView.setVisibility(View.GONE);
            }
            if(checkIfContains(username) || (!selectedMembers.isEmpty() && selectedMembers.contains(username))){
                checkbox.setChecked(true);
                if(isCreateGroup) {
                    checkbox.setBackgroundResource(R.drawable.demo_selector_bg_check);
                    itemView.setEnabled(true);
                }else {
                    checkbox.setBackgroundResource(R.drawable.demo_selector_bg_gray_check);
                    itemView.setEnabled(false);
                }
            }else{
                checkbox.setBackgroundResource(R.drawable.demo_selector_bg_check);
                checkbox.setChecked(false);
                itemView.setEnabled(true);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.setChecked(!checkbox.isChecked());
                    boolean checked = checkbox.isChecked();
                    if(isCreateGroup || !checkIfContains(username)) {
                        if(checked) {
                            if(!selectedMembers.contains(username)) {
                                selectedMembers.add(username);
                            }
                        }else {
                            if(selectedMembers.contains(username)) {
                                selectedMembers.remove(username);
                            }
                        }
                    }
                    if(listener != null) {
                        listener.onSelected(v, selectedMembers);
                    }
                }
            });
        }
    }

    /**
     * 检查是否已存在
     * @param username
     * @return
     */
    private boolean checkIfContains(String username) {
        if(existMembers == null) {
            return false;
        }
        return existMembers.contains(username);
    }

    /**
     * 因为环信id只能由字母和数字组成，如果含有“/”就可以认为是多端登录用户
     * @param username
     * @return
     */
    private String getRealUsername(String username) {
        if(!username.contains("/")) {
            return username;
        }
        String[] multipleUser = username.split("/");
        return multipleUser[0];
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onSelected(View v, List<String> selectedMembers);
    }
}
