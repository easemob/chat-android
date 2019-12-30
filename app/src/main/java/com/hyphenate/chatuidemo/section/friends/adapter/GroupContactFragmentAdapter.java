package com.hyphenate.chatuidemo.section.friends.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.friends.fragment.GroupContactManageFragment;

public class GroupContactFragmentAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public GroupContactFragmentAdapter(Context context, @NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return GroupContactManageFragment.create(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(position == 0 ? R.string.em_friends_group_contact_manage : R.string.em_friends_group_contact_participate);
    }
}
