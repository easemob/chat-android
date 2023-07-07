package com.hyphenate.chatdemo.section.chat.delegates;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatdemo.section.chat.viewholder.ChatUrlPreviewHolder;
import com.hyphenate.chatdemo.section.chat.views.ChatRowUrlPreview;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUrlPreviewAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
   public static final String URL_REGEX = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
   @Override
   protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
      return new ChatRowUrlPreview(parent.getContext(), isSender);
   }

   @Override
   protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
      return new ChatUrlPreviewHolder(view, itemClickListener);
   }

   @Override
   public boolean isForViewType(EMMessage item, int position) {
      return item.getType() == EMMessage.Type.TXT && checkContainsUrl(((EMTextMessageBody) item.getBody()).getMessage());
   }

   public boolean checkContainsUrl(String content){
      Pattern p = Pattern.compile(URL_REGEX);
      Matcher m = p.matcher(content);
      return m.find();
   }
}
