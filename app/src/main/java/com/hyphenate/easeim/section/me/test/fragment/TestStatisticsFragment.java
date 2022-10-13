package com.hyphenate.easeim.section.me.test.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessageStatistics;
import com.hyphenate.chat.EMStatisticsManager.EMSearchMessageDirect;
import com.hyphenate.chat.EMStatisticsManager.EMSearchMessageType;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.databinding.DemoFragmentTestStatisticsBinding;
import com.hyphenate.easeim.section.base.BaseInitFragment;

public class TestStatisticsFragment extends BaseInitFragment implements View.OnClickListener {

    private DemoFragmentTestStatisticsBinding viewBinding;
    private long endTime;
    private long startTime;
    private EMSearchMessageDirect direct;
    private EMSearchMessageType style;
    private static final String[] directs = {"ALL", "SEND", "RECEIVE"};
    private static final String[] types = {"ALL", "TXT", "IMAGE", "VIDEO", "LOCATION", "VOICE", "FILE", "CMD", "CUSTOM"};

    @Override
    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewBinding = DemoFragmentTestStatisticsBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        viewBinding.btnSearchStatisticsNum.setOnClickListener(this);
        viewBinding.btnSearchStatisticsSize.setOnClickListener(this);
        viewBinding.btnSearchMsg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_search_statistics_num) {
            searchStatisticsNum();
        }else if(v.getId() == R.id.btn_search_statistics_size) {
            searchStatisticsSize();
        }else if(v.getId() ==R.id.btn_search_msg) {
            searchMsgStatistics();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        direct = EMSearchMessageDirect.valueOf(directs[0]);
        style = EMSearchMessageType.valueOf(types[0]);

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> directAdapter = new ArrayAdapter<String>(mContext, R.layout.demo_item_spinner_select, directs);
        //设置数组适配器的布局样式
        directAdapter.setDropDownViewResource(R.layout.demo_item_spinner_dropdown);
        //设置下拉框的标题，不设置就没有难看的标题了
        viewBinding.spinnerDirect.setPrompt("Please select direct");
        //设置下拉框的数组适配器
        viewBinding.spinnerDirect.setAdapter(directAdapter);
        //设置下拉框默认的显示第一项
        viewBinding.spinnerDirect.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        viewBinding.spinnerDirect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                direct = EMSearchMessageDirect.valueOf(directs[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<String>(mContext, R.layout.demo_item_spinner_select, types);
        //设置数组适配器的布局样式
        styleAdapter.setDropDownViewResource(R.layout.demo_item_spinner_dropdown);
        //设置下拉框的标题，不设置就没有难看的标题了
        viewBinding.spinnerType.setPrompt("Please select message type");
        //设置下拉框的数组适配器
        viewBinding.spinnerType.setAdapter(styleAdapter);
        //设置下拉框默认的显示第一项
        viewBinding.spinnerType.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        viewBinding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                style = EMSearchMessageType.valueOf(types[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void searchMsgStatistics() {
        String msgId = viewBinding.tvSearchMsg.getText().toString().trim();
        if(TextUtils.isEmpty(msgId)) {
            showToast("Please input message id which you want to search");
            return;
        }
        EMMessageStatistics statistics = EMClient.getInstance().statisticsManager().getMsgStatistics(msgId);
        if(statistics == null) {
            showToast("msgId: "+msgId+", search result is null");
            return;
        }
        viewBinding.tvShowMsg.setText(statistics.toString());
    }

    private void getSearchParams() {
        String endTimeText = viewBinding.etEndTime.getText().toString().trim();
        endTime = 0;
        if(!TextUtils.isEmpty(endTimeText)) {
            endTime = Long.valueOf(endTimeText);
        }
        String startTimeText = viewBinding.etStartTime.getText().toString().trim();
        startTime = 0;
        if(!TextUtils.isEmpty(endTimeText)) {
            startTime = Long.valueOf(startTimeText);
        }
    }

    private void searchStatisticsNum() {
        getSearchParams();
        int number = EMClient.getInstance().statisticsManager().searchMsgStatisticsNumber(startTime, endTime, direct, style);
        viewBinding.tvShowNum.setText("Search result: "+number);
    }

    private void searchStatisticsSize() {
        getSearchParams();
        long number = EMClient.getInstance().statisticsManager().searchMsgStatisticsSize(startTime, endTime, direct, style);
        viewBinding.tvShowSize.setText("Search result: "+number);
    }
}
