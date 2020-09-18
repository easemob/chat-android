package com.hyphenate.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

public class GroupPrePickActivity extends GroupPickContactsActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GroupPrePickActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRightClick(View view) {
        List<String> selectedMembers = adapter.getSelectedMembers();
        String[] newMembers = null;
        if(selectedMembers != null && !selectedMembers.isEmpty()) {
            newMembers = selectedMembers.toArray(new String[0]);
        }
        NewGroupActivity.actionStart(mContext, newMembers);
        finish();
    }
}

