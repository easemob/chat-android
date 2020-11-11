package com.hyphenate.easeui.modules.conversation;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.interfaces.OnItemLongClickListener;
import com.hyphenate.easeui.manager.EaseProviderManager;
import com.hyphenate.easeui.provider.EaseConversationInfoProvider;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;

public class EaseConversationListFragment extends EaseBaseFragment {
    private EaseConversationListLayout list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initListener();
    }

    public int getLayoutId() {
        return R.layout.ease_fragment;
    }

    private void initListener() {
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("TAG", "onItemClick position = "+position);
            }
        });

        list.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                Log.e("TAG", "onItemLongClick position = "+position);
                return true;
            }
        });

    }

    private void initView(Bundle savedInstanceState) {
        list = findViewById(R.id.list);
        list.init();

        EaseProviderManager.getInstance().setConversationInfoProvider(new EaseConversationInfoProvider() {
            @Override
            public String getDefaultTypeAvatar(String type) {
                if(TextUtils.equals(type, EMConversation.EMConversationType.Chat.name())) {
                    return "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605085753048&di=154d47eff01205a62cb779f8588eeb43&imgtype=0&src=http%3A%2F%2Fb.hiphotos.baidu.com%2Fzhidao%2Fwh%253D450%252C600%2Fsign%3Da587b23df11f3a295a9dddcaac159007%2F500fd9f9d72a60590cfef2f92934349b023bba62.jpg";
                }
                return null;
            }

            @Override
            public int getDefaultTypeAvatarResource(String type) {
                if(TextUtils.equals(type, EMConversation.EMConversationType.GroupChat.name())) {
                    return R.drawable.ease_default_image;
                }
                return 0;
            }

            @Override
            public String getConversationName(String username) {
                if(TextUtils.equals(username, "ljn")) {
                    return "刘吉南";
                }
                return null;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        list.loadDefaultData();
    }

}

