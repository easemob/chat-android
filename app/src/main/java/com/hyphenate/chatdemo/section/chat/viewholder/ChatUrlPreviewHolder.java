package com.hyphenate.chatdemo.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatdemo.section.chat.views.ChatRowUrlPreview;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;

public class ChatUrlPreviewHolder extends EaseChatRowViewHolder {

   public ChatUrlPreviewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
      super(itemView, itemClickListener);
//      ((ChatRowUrlPreview)itemView).setAdapter(getAdapter());
   }

   public static ChatUrlPreviewHolder create(ViewGroup parent, boolean isSender,
                                               MessageListItemClickListener itemClickListener) {
      return new ChatUrlPreviewHolder(new ChatRowUrlPreview(parent.getContext(), isSender), itemClickListener);
   }

   @Override
   public void onBubbleClick(EMMessage message) {
      super.onBubbleClick(message);
   }

   @Override
   public void refreshView() {
      getAdapter().notifyDataSetChanged();
   }
}
