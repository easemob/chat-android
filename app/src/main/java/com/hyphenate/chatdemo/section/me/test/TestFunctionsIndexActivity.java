package com.hyphenate.chatdemo.section.me.test;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.databinding.ActivityTestFunctionsIndexBinding;
import com.hyphenate.chatdemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.List;

public class TestFunctionsIndexActivity extends BaseInitActivity {

    private ActivityTestFunctionsIndexBinding viewBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TestFunctionsIndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected View getContentView() {
        viewBinding = ActivityTestFunctionsIndexBinding.inflate(getLayoutInflater());
        return viewBinding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        viewBinding.titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        viewBinding.rvList.setLayoutManager(new LinearLayoutManager(mContext));
        IndexItemAdapter adapter = new IndexItemAdapter();
        viewBinding.rvList.setAdapter(adapter);
        viewBinding.rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        List<String> items = new ArrayList<>();
        items.add("group");
        items.add("presence");
        items.add("statistics");
        items.add("typingListener");
        items.add("database");

        adapter.setData(items);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String item = items.get(position);
                TestFunctionsActivity.actionStart(mContext, item);
            }
        });
    }

    private static class IndexItemAdapter extends EaseBaseRecyclerViewAdapter<String> {

        @Override
        public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_item_test_index, parent, false);
            return new IndexItemViewHolder(view);
        }

        private class IndexItemViewHolder extends ViewHolder<String> {

            private TextView tv_content;

            public IndexItemViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                tv_content = findViewById(R.id.tv_content);
            }

            @Override
            public void setData(String item, int position) {
                tv_content.setText(item);
            }
        }
    }
}
