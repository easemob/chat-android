package com.hyphenate.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMLanguage;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.me.adapter.LanguageAdapter;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, AdapterView.OnItemClickListener {
    private EaseTitleBar titleBar;
    private ListView rvList;
    private LanguageAdapter adapter;

    private List<EMLanguage> emLanguageList = new ArrayList<>();

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, LanguageActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_language;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar_language);
        rvList = findViewById(R.id.language_list);

        //获取微软支持的翻译语言
//        emLanguageList = EMClient.getInstance().translationManager().getSupportedLanguages();
        if(emLanguageList.size() <= 0){
            // 默认多语言列表
            defaultLanguages();
        }
        adapter = new LanguageAdapter(mContext, emLanguageList);
        rvList.setAdapter(adapter);
        initSelectedLanguage();
        rvList.setOnItemClickListener(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }


    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }


    @Override
    public void onRightClick(View view) {
        updateLanguage();
        onBackPressed();
    }

    private void initSelectedLanguage() {
        String languageCode = DemoHelper.getInstance().getModel().getTargetLanguage();
        int selectedIndex = 0;

        for(int index = 0 ; index < emLanguageList.size(); index++) {
            EMLanguage language = emLanguageList.get(index);
            if(language.LanguageCode.equals(languageCode)) {
                selectedIndex = index;
                break;
            }
        }

        adapter.setSelectedIndex(selectedIndex);
    }

    private void defaultLanguages(){
        emLanguageList.add(new EMLanguage("zh-Hans", "中文 (简体)", "中文 (简体)"));
        emLanguageList.add(new EMLanguage("zh-Hant", "繁體中文 (繁體)", "繁體中文 (繁體)"));
        emLanguageList.add(new EMLanguage("en", "English", "English"));
        emLanguageList.add(new EMLanguage("id", "Indonesia", "Indonesia"));
        emLanguageList.add(new EMLanguage("ko", "한국어", "한국어"));
        emLanguageList.add(new EMLanguage("it", "Italiano", "Italiano"));
        emLanguageList.add(new EMLanguage("pt", "Português (Brasil)", "Português (Brasil)"));
        emLanguageList.add(new EMLanguage("ja", "日本語", "日本語"));
        emLanguageList.add(new EMLanguage("fr", "Français", "Français"));
        emLanguageList.add(new EMLanguage("de", "Deutsch", "Deutsch"));
    }

    private void updateLanguage() {
        long selectedIndex = adapter.getSelectedIndex();
        String languageCode = emLanguageList.get((int)selectedIndex).LanguageCode;

        DemoHelper.getInstance().getModel().setTargetLanguage(languageCode);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(id != adapter.getSelectedIndex()) {
            adapter.setSelectedIndex(id);
            adapter.notifyDataSetChanged();
        }
    }
}
