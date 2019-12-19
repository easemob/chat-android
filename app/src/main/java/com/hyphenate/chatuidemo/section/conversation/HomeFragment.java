package com.hyphenate.chatuidemo.section.conversation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("TAG", "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("TAG", "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("TAG", "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("TAG", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TAG", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("TAG", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("TAG", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("TAG", "onDetach");
    }
}
