package com.hyphenate.easeim.section.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMLanguage;
import com.hyphenate.easeim.R;

import java.util.List;

public class LanguageAdapter extends ArrayAdapter<EMLanguage> {
    private long mSelectedIndex = 0;

    public LanguageAdapter(@NonNull Context context, @NonNull List<EMLanguage> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent) {
        if(convertedView == null) {
            convertedView = LayoutInflater.from(getContext()).inflate(R.layout.demo_item_language, parent, false);
        }

        TextView textView = (TextView) convertedView.findViewById(R.id.language_content);
        ImageView imageView = (ImageView) convertedView.findViewById(R.id.language_select);

        EMLanguage languageItem = getItem(position);
        textView.setText(languageItem.LanguageLocalName);
        long id = getItemId(position);
        if (mSelectedIndex == id) {
            imageView.setImageResource(R.drawable.yes);
        }else {
            imageView.setImageResource(0);
        }

        return convertedView;
    }

    public void setSelectedIndex(long index) {
        mSelectedIndex = index;
    }

    public long getSelectedIndex() {
        return mSelectedIndex;
    }
}
