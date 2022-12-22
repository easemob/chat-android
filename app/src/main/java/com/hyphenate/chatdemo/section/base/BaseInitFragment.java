package com.hyphenate.chatdemo.section.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseInitFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        View view;
        if(layoutId == 0) {
            view = getLayoutView(inflater, container);
        }else {
            view = inflater.inflate(layoutId, container, false);
        }

        initArgument();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initViewModel();
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return null;
    }

    /**
     * 获取布局id
     * @return
     */
    protected int getLayoutId() {
        return 0;
    }

    /**
     * 获取传递参数
     */
    protected void initArgument() {}

    /**
     * 初始化布局相关
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {
        Log.e("TAG", "fragment = "+this.getClass().getSimpleName());
    }


    /**
     * 初始化ViewModel相关
     */
    protected void initViewModel() {}

    /**
     * 初始化监听等
     */
    protected void initListener() {}

    /**
     * 初始化数据相关
     */
    protected void initData() {}

    /**
     * 通过id获取当前view控件，需要在onViewCreated()之后的生命周期调用
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }
}
