package com.hyphenate.easeim.section.chat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.model.KV;
import com.hyphenate.easeim.section.chat.viewmodel.ConferenceInviteViewModel;
import com.hyphenate.easeim.section.conference.ConferenceInviteActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectUserCardActivity extends BaseInitActivity implements  EaseTitleBar.OnBackPressListener {
    private static final String TAG = SelectUserCardActivity.class.getSimpleName();

    private List<KV<String, Integer>> contacts = new ArrayList<>();
    private SelectUserCardActivity.ContactsAdapter contactsAdapter;
    private EaseTitleBar mTitleBar;
    private ListView mListView;
    private TextView start_btn;
    private static String groupId;
    private String[] exist_member;
    private String toUser;
    private int selectIndex = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conference_invite;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toUser = intent.getStringExtra("toUser");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = findViewById(R.id.title_bar);

        contactsAdapter = new SelectUserCardActivity.ContactsAdapter(mContext, contacts);

        contactsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectIndex = position;
                contactsAdapter.notifyActual();
            }
        });

        mListView = findViewById(R.id.listView);
        mListView.setAdapter(contactsAdapter);
        start_btn= findViewById(R.id.btn_start);
        start_btn.setVisibility(View.GONE);

        addHeader();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTitleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        ConferenceInviteViewModel viewModel = new ViewModelProvider(this).get(ConferenceInviteViewModel.class);
        viewModel.getConferenceInvite().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<KV<String, Integer>>>() {
                @Override
                public void onSuccess(List<KV<String, Integer>> data) {
                    contacts = data;
                    contactsAdapter.setData(contacts);
                }
            });
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                if(contactsAdapter != null){
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                if(contactsAdapter != null){
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        });
        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                if(contactsAdapter != null){
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        });
        viewModel.getConferenceMembers(groupId,exist_member);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }


    private void addHeader() {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.ease_search_bar, null);
        EditText query = headerView.findViewById(R.id.query);
        ImageView queryClear = headerView.findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactsAdapter.filter(s);
                if(!TextUtils.isEmpty(s)) {
                    queryClear.setVisibility(View.VISIBLE);
                }else {
                    queryClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        queryClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideKeyboard();
            }
        });
        mListView.addHeaderView(headerView);
    }


    @Override
    public void onBackPress(View view) {
        onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 发送名片提示框
     */
    private void sendUserCardDisplay(String userId) {
        EMLog.i(TAG, " sendUserCardDisplay user:" + toUser);
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectUserCardActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(SelectUserCardActivity.this, R.layout.demo_activity_send_user_card, null);
        Button send_btn = dialogView.findViewById(R.id.btn_send);
        Button cancel_btn = dialogView.findViewById(R.id.btn_cancel);

        TextView userNickView = dialogView.findViewById(R.id.user_nick_name);
        TextView userIdView = dialogView.findViewById(R.id.userId_view);
        ImageView headView = dialogView.findViewById(R.id.head_view);
        EaseUser user = DemoHelper.getInstance().getUserInfo(userId);

        if(user != null){
            userNickView.setText(user.getNickname());
            Glide.with(mContext).load(user.getAvatar()).placeholder(R.drawable.em_login_logo).into(headView);
        }else{
            userNickView.setText(user.getUsername());
        }
        userIdView.setText(getApplicationContext().getString(R.string.personal_card) + userId);

        dialog.setView(dialogView);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
//        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        dialog.show();


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("user", user);
                setResult(RESULT_OK, intent);
                dialog.dismiss();
                finish();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    private class ContactsAdapter extends BaseAdapter {
        private Context context;
        private List<KV<String, Integer>> filteredContacts = new ArrayList<>();
        private List<KV<String, Integer>> contacts = new ArrayList<>();

        private SelectUserCardActivity.ContactsAdapter.ContactFilter mContactFilter;
        public ConferenceInviteActivity.ICheckItemChangeCallback checkItemChangeCallback;
        private OnItemClickListener mOnItemClickListener;


        public ContactsAdapter(Context context, List<KV<String, Integer>> contacts) {
            this.context = context;
            this.contacts = contacts;
            filteredContacts.addAll(contacts);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        @Override
        public int getCount() {
            return filteredContacts == null ? 0 : filteredContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredContacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = convertView;
            SelectUserCardActivity.ContactsAdapter.ViewHolder viewHolder = null;
            if(contentView == null) {
                contentView = LayoutInflater.from(mContext).inflate(R.layout.demo_usercard_item, null);
                viewHolder = new SelectUserCardActivity.ContactsAdapter.ViewHolder(contentView,position);
                viewHolder.setmOnItemClickListener(mOnItemClickListener);
                contentView.setTag(viewHolder);
            }else {
                viewHolder = (SelectUserCardActivity.ContactsAdapter.ViewHolder) contentView.getTag();
                viewHolder.setmOnItemClickListener(mOnItemClickListener);
            }
            //viewHolder.reset();

            KV<String, Integer> contact = filteredContacts.get(position);
            String userName = contact.getFirst();
            EaseUserUtils.setUserAvatar(mContext, userName, viewHolder.headerImage);
            EaseUserUtils.setUserNick(userName, viewHolder.nameText);
            if(position == selectIndex){
                sendUserCardDisplay(userName);
            }
            return contentView;
        }

        @Override
        public void notifyDataSetChanged() {
            filteredContacts.clear();
            filteredContacts.addAll(contacts);
            notifyActual();
        }

        private void notifyActual() {
            super.notifyDataSetChanged();
        }

        public void setData(List<KV<String, Integer>> data) {
            contacts = data;
            if(data != null) {
                this.filteredContacts.addAll(data);
            }
            notifyDataSetChanged();
        }



        void filter(CharSequence constraint) {
            if(mContactFilter == null) {
                mContactFilter = new SelectUserCardActivity.ContactsAdapter.ContactFilter(contacts);
            }

            mContactFilter.filter(constraint, new SelectUserCardActivity.IFilterCallback() {
                @Override
                public void onFilter(List<KV<String, Integer>> filtered) {
                    filteredContacts.clear();
                    filteredContacts.addAll(filtered);
                    if(!filtered.isEmpty()) {
                        notifyActual();
                    }else {
                        notifyDataSetInvalidated();
                    }
                }
            });
        }

        private class ViewHolder {
            View view;
            ImageView headerImage;
            TextView nameText;
            int position;
            private OnItemClickListener mOnItemClickListener;

            public ViewHolder(View view,int position) {
                this.view = view;
                this.position = position;
                headerImage = view.findViewById(R.id.head_icon);
                nameText = view.findViewById(R.id.name);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOnItemClickListener != null){
                            mOnItemClickListener.onItemClick(v, position);
                        }
                    }
                });
            }

            public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
                this.mOnItemClickListener = mOnItemClickListener;
            }
        }

        private class ContactFilter extends Filter {
            private SelectUserCardActivity.IFilterCallback mFilterCallback;
            private List<KV<String, Integer>> contacts;

            public ContactFilter(List<KV<String, Integer>> contacts) {
                this.contacts = contacts;
            }

            public void filter(CharSequence constraint, SelectUserCardActivity.IFilterCallback callback) {
                this.mFilterCallback = callback;
                super.filter(constraint);
            }

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                if(prefix == null || prefix.length() == 0) {
                    results.values = contacts;
                    results.count = contacts.size();
                }else {
                    String prefixString = prefix.toString();
                    int count = contacts.size();
                    List<KV<String, Integer>> newValues = new ArrayList<>();
                    for(int i = 0; i < count; i++) {
                        KV<String, Integer> user = contacts.get(i);
                        String username = user.getFirst();
                        if(username.startsWith(prefixString)) {
                            newValues.add(user);
                        }else {
                            String[] splits = username.split(" ");
                            if(splits.length == 0) {
                                continue;
                            }
                            List<String> words = new ArrayList<>();
                            for(int j = splits.length - 1; j >= 0 ; j--) {
                                if(!splits[j].isEmpty()) {
                                    words.add(splits[j]);
                                }else {
                                    break;
                                }
                            }
                            for (String word : words) {
                                if(word.startsWith(prefixString)) {
                                    newValues.add(user);
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<KV<String, Integer>> result = results.values != null ? (List<KV<String, Integer>>) results.values : new ArrayList<>();
                if(mFilterCallback != null) {
                    mFilterCallback.onFilter(result);
                }
            }
        }

    }

    interface IFilterCallback {
        void onFilter(List<KV<String, Integer>> filtered);
    }

    public interface ICheckItemChangeCallback {
        void onCheckedItemChanged(View v, String username, int state);
    }
}
