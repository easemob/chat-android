package com.hyphenate.chatuidemo.section.friends.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.enums.Status;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;
import com.hyphenate.chatuidemo.section.friends.adapter.GroupContactAdapter;
import com.hyphenate.chatuidemo.section.friends.viewmodels.GroupContactViewModel;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseSidebar;


public class GroupContactManageFragment extends ContactManageFragment implements EaseSidebar.OnTouchEventListener {
    private int mType;
    private GroupContactViewModel mViewModel;

    public static GroupContactManageFragment create(int position) {
        GroupContactManageFragment fragment = new GroupContactManageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initArgument() {
        super.initArgument();
        Bundle bundle = getArguments();
        if(bundle != null) {
            mType = bundle.getInt("type", 0);
        }
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        mViewModel = new ViewModelProvider(mContext).get(GroupContactViewModel.class);
        mViewModel.getAllGroups().observe(this, response -> {
            if(response == null) {
                return;
            }
            if(response.status == Status.SUCCESS) {
                mAdapter.setData(mType == 0 ? mViewModel.getManageGroups(response.data) : mViewModel.getJoinGroups(response.data));
            }else if(response.status == Status.ERROR) {
                showToast(response.getMessage());
            }else if(response.status == Status.LOADING) {

            }
        });
    }

}
