package com.hyphenate.chatdemo.section.chat.views;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.common.model.DemoUrlPreviewBean;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.chatrow.AutolinkSpan;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.util.EMLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRowUrlPreview extends EaseChatRow {
   private TextView contentView;
   private TextView title;
   private TextView description;
   private ShapeableImageView icon;
   private ConstraintLayout quoteItem;
   public static final String URL_REGEX = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
           + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";

   public ChatRowUrlPreview(Context context, boolean isSender) {
      super(context, isSender);
   }


   @Override
   protected void onInflateView() {
      inflater.inflate(showSenderType ? R.layout.ease_row_sent_url_preview : R.layout.ease_row_received_url_preview, this);
   }

   @Override
   protected void onFindViewById() {
      contentView = findViewById(R.id.tv_content);
      title = findViewById(R.id.title);
      description = findViewById(R.id.description);
      icon = findViewById(R.id.iv_icon);
      quoteItem = findViewById(R.id.quote_item);
   }

   @Override
   protected void onSetUpView() {
      EMTextMessageBody messageBody = (EMTextMessageBody) message.getBody();
      if (messageBody != null){
         String content = messageBody.getMessage();
         Spannable span = EaseSmileUtils.getSmiledText(context, content);
         // 设置内容
         contentView.setText(span, TextView.BufferType.SPANNABLE);

         contentView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               contentView.setTag(com.hyphenate.easeui.R.id.action_chat_long_click,true);
               if (itemClickListener != null) {
                  return itemClickListener.onBubbleLongClick(v, message);
               }
               return false;
            }
         });
         quoteItem.setVisibility(GONE);
         replaceSpan();
      }
   }

   /**
    * 解决长按事件与relink冲突，参考：https://www.jianshu.com/p/d3bef8449960
    */
   private void replaceSpan() {
      Spannable spannable = (Spannable) contentView.getText();
      URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);

      String url = spans[0].getURL();
      int index = spannable.toString().indexOf(url);
      int end = index + url.length();
      if (index == -1) {
         if (url.contains("http://")) {
            url = url.replace("http://", "");
         } else if (url.contains("https://")) {
            url = url.replace("https://", "");
         } else if (url.contains("rtsp://")) {
            url = url.replace("rtsp://", "");
         }
         index = spannable.toString().indexOf(url);
         end = index + url.length();
      }

      DemoUrlPreviewBean urlPreviewInfo = DemoHelper.getInstance().getUrlPreviewInfo(message.getMsgId());
      if (urlPreviewInfo == null ){
         parsingUrl(spans[0].getURL(), new EMValueCallBack<DemoUrlPreviewBean>() {
            @Override
            public void onSuccess(DemoUrlPreviewBean value) {
               EaseThreadManager.getInstance().runOnMainThread(new Runnable() {
                  @Override
                  public void run() {
                     DemoHelper.getInstance().saveUrlPreviewInfo(message.getMsgId(),value);
                     itemActionCallback.refreshView();
                  }
               });
            }

            @Override
            public void onError(int error, String errorMsg) {
               EaseThreadManager.getInstance().runOnMainThread(new Runnable() {
                  @Override
                  public void run() {
                     EMLog.e("ChatRowUrlPreview","parsingUrl onError" + errorMsg + error);
                  }
               });
            }
         });
      }else {
         quoteItem.setVisibility(VISIBLE);
         title.setText(urlPreviewInfo.getTitle());
         description.setText(urlPreviewInfo.getContent());
         Glide.with(context).load(urlPreviewInfo.getPrimaryImg()).placeholder(R.drawable.em_icon_rectangle).into(icon);
      }

      if (index != -1) {
         spannable.removeSpan(spans[0]);
         spannable.setSpan(new AutolinkSpan(spans[0].getURL()), index
                 , end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
      }
   }

   private void parsingUrl(String url, EMValueCallBack<DemoUrlPreviewBean> callBack){
      EaseThreadManager.getInstance().runOnIOThread(()->{
         try {
            DemoUrlPreviewBean demoUrlPreviewBean = new DemoUrlPreviewBean();
            String descriptionContent = "";
            String logoUrl = "";
            Document document = Jsoup.connect(url)
                    .header("content-type","text/html;charset=utf-8")
                    .timeout(5000).get();
            String title = document.title();

            Element description = document.select("head meta[name=description]").first();

            String src = document.getElementsByTag("img").first().attr("src");

            if (!checkContainsUrl(src)){
               // 如果不是标准url路径 判断是否是 //开头或者 /开头做相应处理
               if(src.startsWith("//")){
                  logoUrl = "http:" + src;
               }else {
                  logoUrl = url + src;
               }
            }else {
               if(src.startsWith("//")){
                  logoUrl = "http:" + src;
               } else if (src.startsWith("www")){
                  logoUrl = "http://" + src;
               }else {
                  logoUrl = src;
               }
            }

            // Get the content of the description node
            if (description != null){
               descriptionContent = description.attr("content");
            }

            demoUrlPreviewBean.setTitle(title);//标题
            demoUrlPreviewBean.setPrimaryImg(logoUrl); // 首图
            demoUrlPreviewBean.setContent(descriptionContent); // 内容

            EMLog.d("ChatRowUrlPreview",
                    "title:" + title +"\n"
                     + "description " + descriptionContent + "\n"
                     + "logo " + logoUrl + "\n");

            callBack.onSuccess(demoUrlPreviewBean);
         } catch (IOException e) {
            e.printStackTrace();
            callBack.onError(1,e.getMessage());
         }
      });
   }

   public boolean checkContainsUrl(String content){
      Pattern p = Pattern.compile(URL_REGEX);
      Matcher m = p.matcher(content);
      return m.find();
   }

}
