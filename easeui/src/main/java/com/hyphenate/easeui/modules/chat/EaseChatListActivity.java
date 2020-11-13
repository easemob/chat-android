package com.hyphenate.easeui.modules.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.ui.base.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class EaseChatListActivity extends EaseBaseActivity {

    private FrameLayout flFragment;
    private EaseTitleBar titleBar;

    public static void actionStart(Context context, String username, int chatType) {
        Intent intent = new Intent(context, EaseChatListActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("chatType", chatType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.ease_activity_chat_list);
        titleBar = findViewById(R.id.title_bar);
        flFragment = findViewById(R.id.fl_fragment);
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
        EaseChatListFragment fragment = new EaseChatListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", getIntent().getStringExtra("username"));
        bundle.putInt("chatType", getIntent().getIntExtra("chatType", 0));
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }
}

