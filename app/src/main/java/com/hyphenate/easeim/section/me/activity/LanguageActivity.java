package com.hyphenate.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.contact.adapter.LanguageAdapter;
import com.hyphenate.chat.translator.EMLanguage;
import com.hyphenate.chat.translator.EMTranslationManager;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.List;

public class LanguageActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, AdapterView.OnItemClickListener {
    private EaseTitleBar titleBar;
    private ListView rvList;
    private LanguageAdapter adapter;

    private List<EMLanguage> emLanguageList;

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

        emLanguageList = EMTranslationManager.getInstance().getSupportedLanguages();
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