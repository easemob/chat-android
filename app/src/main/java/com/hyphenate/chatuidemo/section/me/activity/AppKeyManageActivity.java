package com.hyphenate.chatuidemo.section.me.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.db.entity.AppKeyEntity;
import com.hyphenate.chatuidemo.common.model.DemoModel;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.DemoDialogFragment;
import com.hyphenate.chatuidemo.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.interfaces.OnItemLongClickListener;
import com.hyphenate.easeui.widget.EaseRecyclerView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

public class AppKeyManageActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, OnRefreshListener {
    private EaseTitleBar titleBar;
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private RvAdapter adapter;

    private int selectedPosition;
    private DemoModel settingsModel;

    public static void actionStartForResult(Activity activity, int requestCode) {
        Intent starter = new Intent(activity, AppKeyManageActivity.class);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_appkey_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        srlRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        settingsModel = DemoHelper.getInstance().getModel();

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new RvAdapter();
        rvList.setAdapter(adapter);

        View bottom = LayoutInflater.from(mContext).inflate(R.layout.demo_layout_appkey_add, null);
        rvList.addFooterView(bottom);

        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppKeyAddActivity.actionStartForResult(mContext, 100);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showConfirmDialog(position);
            }
        });

        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                if(position != 0) {
                    showDeleteDialog(position);
                }
                return true;
            }
        });

        getData();
    }

    private void showConfirmDialog(int position) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_developer_appkey_warning)
                .setOnConfirmClickListener(R.string.em_developer_appkey_confirm, new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        selectedPosition = position;
                        adapter.notifyDataSetChanged();
                        String appKey = adapter.getItem(position).getAppKey();
                        settingsModel.setCustomAppkey(appKey);
                        Intent intent = new Intent();
                        intent.putExtra("appkey", appKey);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .showCancelButton(true)
                .show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                    .setMessage(R.string.delete)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DemoHelper.getInstance().getModel().deleteAppKey(adapter.mData.get(position).getAppKey());
                            getData();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
    }

    private void getData() {
        List<AppKeyEntity> appKeys = DemoHelper.getInstance().getModel().getAppKeys();
        String appkey = settingsModel.getCutomAppkey();
        if(appKeys != null && !appKeys.isEmpty()) {
            for(int i = 0; i < appKeys.size(); i++) {
                AppKeyEntity entity = appKeys.get(i);
                if(TextUtils.equals(entity.getAppKey(), appkey)) {
                    selectedPosition = i;
                }
            }
            adapter.setData(appKeys);
        }
        if(srlRefresh != null) {
            srlRefresh.finishRefresh();
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            getData();
        }
    }

    private class RvAdapter extends EaseBaseRecyclerViewAdapter<AppKeyEntity> {

        @Override
        public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.demo_item_appkey_manage, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public int getEmptyLayoutId() {
            return R.layout.ease_layout_no_data_show_nothing;
        }

        private class MyViewHolder extends ViewHolder<AppKeyEntity> {
            private RadioButton ivArrow;
            private TextView tvContent;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                ivArrow = findViewById(R.id.iv_arrow);
                tvContent = findViewById(R.id.tv_content);
            }

            @Override
            public void setData(AppKeyEntity item, int position) {
                tvContent.setText(item.getAppKey());
                if(selectedPosition == position) {
                    ivArrow.setVisibility(View.VISIBLE);
                    ivArrow.setChecked(true);
                }else {
                    ivArrow.setVisibility(View.INVISIBLE);
                    ivArrow.setChecked(false);
                }
            }
        }

    }
}
