package com.hyphenate.chatuidemo.section.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.chatuidemo.section.base.BaseActivity;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.chat.fragment.ImageGridFragment;

public class ImageGridActivity extends BaseActivity {

	private static final String TAG = "ImageGridActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTextColor(false);
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new ImageGridFragment(), TAG);
            ft.commit();
        }
    }

}
