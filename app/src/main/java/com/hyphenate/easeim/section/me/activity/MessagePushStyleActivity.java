package com.hyphenate.easeim.section.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMPushManager;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.me.viewmodels.PushStyleViewModel;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagePushStyleActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private RecyclerView rvList;
    private int selectedPosition;
    private PushStyleViewModel viewModel;
    private static final int[] names = {R.string.push_message_style_simple, R.string.push_message_show_detail};

    public static void actionStartForResult(BaseActivity context, int position, int requestCode) {
        Intent intent = new Intent(context, MessagePushStyleActivity.class);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_message_push_style;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        selectedPosition = intent.getIntExtra("position", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        rvList = findViewById(R.id.rv_list);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        MessageStyleAdapter adapter = new MessageStyleAdapter();
        rvList.setAdapter(adapter);
        rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        final EMPushManager.DisplayStyle[] values = EMPushManager.DisplayStyle.values();
        List<EMPushManager.DisplayStyle> styles = new ArrayList<>();
        Collections.addAll(styles, values);
        adapter.setData(styles);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedPosition = position;
                adapter.notifyDataSetChanged();
                viewModel.updateStyle(values[selectedPosition]);
            }
        });

        viewModel = new ViewModelProvider(this).get(PushStyleViewModel.class);
        viewModel.getPushStyleObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    Intent intent = getIntent().putExtra("position", selectedPosition);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onLoading(Boolean data) {
                    super.onLoading(data);
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissLoading();
                }
            });
        });
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    private class MessageStyleAdapter extends EaseBaseRecyclerViewAdapter<EMPushManager.DisplayStyle> {

        @Override
        public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.demo_item_message_style, parent, false);
            return new MessageStyleViewHolder(view);
        }

        private class MessageStyleViewHolder extends ViewHolder<EMPushManager.DisplayStyle> {
            private TextView tvName;
            private CheckBox cbStyle;

            public MessageStyleViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                tvName = findViewById(R.id.tv_name);
                cbStyle = findViewById(R.id.cb_style);
            }

            @Override
            public void setData(EMPushManager.DisplayStyle item, int position) {
                tvName.setText(names[item.ordinal()]);
                if(selectedPosition == position) {
                    cbStyle.setChecked(true);
                }else {
                    cbStyle.setChecked(false);
                }
            }
        }
    }
}

