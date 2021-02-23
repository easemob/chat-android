package com.hyphenate.easeim.section.conference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import easemob.hyphenate.calluikit.EaseCallUIKit;
import easemob.hyphenate.calluikit.utils.EaseCallKitUtils;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.model.KV;
import com.hyphenate.easeim.section.chat.viewmodel.ConferenceInviteViewModel;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static easemob.hyphenate.calluikit.utils.EaseMsgUtils.CALL_GROUP_ID;

public class ConferenceInviteActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {
    private static final String TAG = "ConferenceInvite";
    private static final int STATE_UNCHECKED = 0;
    private static final int STATE_CHECKED = 1;
    private static final int STATE_CHECKED_UNCHANGEABLE = 2;

    private List<KV<String, Integer>> contacts = new ArrayList<>();
    private ContactsAdapter contactsAdapter;
    private EaseTitleBar mTitleBar;
    private TextView mBtnStart;
    private ListView mListView;
    private static String groupId;
    private String[] exist_member;

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_conference_invite;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        String group = intent.getStringExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID);
        if(group != null){
            groupId = group;
            exist_member = intent.getStringArrayExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_EXIST_MEMBERS);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = findViewById(R.id.title_bar);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStart.setText(String.format(getString(R.string.button_start_video_conference), 0));

        contactsAdapter = new ContactsAdapter(mContext, contacts);
        mListView = findViewById(R.id.listView);
        mListView.setAdapter(contactsAdapter);

        addHeader();
    }

    @Override
    protected void initListener() {
        super.initListener();
        contactsAdapter.checkItemChangeCallback = new ICheckItemChangeCallback() {
            @Override
            public void onCheckedItemChanged(View v, String username, int state) {
                int count = getSelectMembers().length;
                mBtnStart.setText(String.format(getString(R.string.button_start_video_conference), count));
            }
        };
        mBtnStart.setOnClickListener(this);
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
        viewModel.getConferenceMembers(groupId);
        viewModel.setExistMembers(exist_member);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private String[] getSelectMembers() {
        List<String> results = new ArrayList<>();
        for(int i = 0; i < contacts.size(); i++) {
            KV<String, Integer> item = contacts.get(i);
            if(item.getSecond() == STATE_CHECKED) {
                results.add(item.getFirst());
            }
        }
        return results.toArray(new String[0]);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start :
                String[] members = getSelectMembers();
                if(members.length == 0) {
                    showToast(R.string.tips_select_contacts_first);
                    return;
                }
                //用户自定义扩展字段
                Map<String, Object> params = new HashMap<>();
                params.put("groupId", groupId);
                //开始邀请人员
                EaseCallUIKit.getInstance().startInviteMultipleCall(members,params);
                finish();
                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
        EaseCallUIKit.getInstance().startInviteMultipleCall(null,null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            EaseCallUIKit.getInstance().startInviteMultipleCall(null,null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if (y1 - y2 > 50) {
            } else if (y2 - y1 > 50) {
            } else if (x1 - x2 > 50) {
            } else if (x2 - x1 > 50) {
                EaseCallUIKit.getInstance().startInviteMultipleCall(null,null);
            }
        }
        return super.onTouchEvent(event);
    }


    private class ContactsAdapter extends BaseAdapter {
        private Context context;
        private List<KV<String, Integer>> filteredContacts = new ArrayList<>();
        private List<KV<String, Integer>> contacts = new ArrayList<>();
        private ContactFilter mContactFilter;
        public ICheckItemChangeCallback checkItemChangeCallback;


        public ContactsAdapter(Context context, List<KV<String, Integer>> contacts) {
            this.context = context;
            this.contacts = contacts;
            filteredContacts.addAll(contacts);
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
            ViewHolder viewHolder = null;
            if(contentView == null) {
                contentView = LayoutInflater.from(mContext).inflate(R.layout.demo_contact_item, null);
                viewHolder = new ViewHolder(contentView);
                contentView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) contentView.getTag();
            }
            viewHolder.reset();

            KV<String, Integer> contact = filteredContacts.get(position);
            String userName = contact.getFirst();
            EaseUserUtils.setUserAvatar(mContext, userName, viewHolder.headerImage);
            EaseUserUtils.setUserNick(userName, viewHolder.nameText);
            switch (contact.getSecond()) {
                case STATE_CHECKED_UNCHANGEABLE :
                    viewHolder.checkBox.setButtonDrawable(R.drawable.demo_checkbox_bg_gray_selector);
                    viewHolder.checkBox.setChecked(true);
                    viewHolder.checkBox.setClickable(false);
                    break;
                default:
                    ViewHolder finalViewHolder = viewHolder;
                    contentView.setOnClickListener(view -> {
                        finalViewHolder.checkBox.toggle();
                    });
                    viewHolder.checkBox.setButtonDrawable(R.drawable.demo_checkbox_bg_selector);
                    viewHolder.checkBox.setChecked(contact.getSecond() == STATE_CHECKED);
                    viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            contact.setSecond(isChecked ? STATE_CHECKED : STATE_UNCHECKED);
                            if(checkItemChangeCallback != null) {
                                checkItemChangeCallback.onCheckedItemChanged(buttonView, contact.getFirst(), contact.getSecond());
                            }
                        }
                    });

                    break;
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
                mContactFilter = new ContactFilter(contacts);
            }

            mContactFilter.filter(constraint, new IFilterCallback() {
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
            CheckBox checkBox;

            public ViewHolder(View view) {
                this.view = view;
                headerImage = view.findViewById(R.id.head_icon);
                nameText = view.findViewById(R.id.name);
                checkBox = view.findViewById(R.id.checkbox);
            }

            public void reset() {
                view.setOnClickListener(null);
                nameText.setText(null);
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(false);
            }
        }

        private class ContactFilter extends Filter {
            private IFilterCallback mFilterCallback;
            private List<KV<String, Integer>> contacts;

            public ContactFilter(List<KV<String, Integer>> contacts) {
                this.contacts = contacts;
            }

            public void filter(CharSequence constraint, IFilterCallback callback) {
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
