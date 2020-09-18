package com.hyphenate.easeim.section.base;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hyphenate.easeim.R;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class WebViewActivity extends BaseInitActivity {
    private EaseTitleBar titleBar;
    private ProgressBar progressBar;
    private WebView webview;
    private String url;
    private boolean showTitle;

    public static void actionStart(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String url, boolean showTitle) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("showTitle", showTitle);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        url = intent.getStringExtra("url");
        showTitle = intent.getBooleanExtra("showTitle", true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_base_webview;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        webview = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar);

        if(!showTitle) {
            titleBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                //判断网页是否可以后退
                if(webview.canGoBack()) {
                    webview.goBack();
                }else {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
    }

    @Override
    protected void initData() {
        super.initData();
        if(!TextUtils.isEmpty(url)) {
            webview.loadUrl(url);
        }
        //配置WebSettings
        WebSettings settings = webview.getSettings();
        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        settings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他操作
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片

        //配置WebViewClient，使用WebView加载
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//表示等待证书相应
            }
        });

        //配置WebChromeClient类
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                titleBar.setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}

