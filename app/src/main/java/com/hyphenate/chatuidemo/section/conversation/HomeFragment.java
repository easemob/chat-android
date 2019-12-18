package com.hyphenate.chatuidemo.section.conversation;

import android.view.View;
import android.widget.TextView;

import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.section.base.BaseInitFragment;

public class HomeFragment extends BaseInitFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.em_fragment_home;
    }

    @Override
    protected void initData() {
        super.initData();
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = findViewById(R.id.text_home);
                tv.setText("good");
            }
        });
    }
}
