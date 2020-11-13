package com.hyphenate.easeui.modules.contact;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EaseProviderManager;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;

public class EaseContactListFragment extends EaseBaseFragment {
    private EaseContactLayout contactLayout;
    private EaseContactListLayout contactList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
    }

    public int getLayoutId() {
        return R.layout.ease_contact_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgument();
        initView(savedInstanceState);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initArgument() {

    }

    public void initView(Bundle savedInstanceState) {
        contactLayout = findViewById(R.id.contact_layout);
        contactList = contactLayout.getContactList();
    }

    public void initListener() {}

    public void initData() {
        EaseProviderManager.getInstance().setUserProvider(new EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return null;
            }

            @Override
            public EaseUser getUser(EaseUser user) {
                if(TextUtils.equals(user.getUsername(), "chong")) {
                    user.setAvatar("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605085753048&di=d1e68d730cde4b1d399eea7770a50a45&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202005%2F11%2F20200511141839_NUsHG.thumb.400_0.jpeg");
                    user.setNickname("冲");
                }
                if(TextUtils.equals(user.getUsername(), "ljna")) {
                    user.setAvatar("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605085753046&di=de5215509a758bbaed0d7dac0fd756c9&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202006%2F07%2F20200607211021_SNzhk.thumb.400_0.jpeg");
                    user.setNickname("小号的天下");
                }
                return user;
            }
        });
        contactLayout.init();

        contactList.addCustomItem(0, R.drawable.em_chat_voice_call_normal, "找朋友");
        contactList.addCustomItem(1, R.drawable.ease_group_icon, "群组");
    }
}

