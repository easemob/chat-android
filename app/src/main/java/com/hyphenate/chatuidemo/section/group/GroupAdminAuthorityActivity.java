package com.hyphenate.chatuidemo.section.group;

public class GroupAdminAuthorityActivity extends GroupMemberAuthorityActivity {
    @Override
    public void getData() {
        viewModel.getAdminObservable(groupId).observe(this, response -> {
            if(response == null) {
                return;
            }
            adapter.setData(response);
        });
    }
}
