package com.hyphenate.chatdemo.section.chat.views;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatdemo.DemoHelper;
import com.hyphenate.chatdemo.R;
import com.hyphenate.chatdemo.section.chat.model.UrlPreViewBean;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.chatrow.AutolinkSpan;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.util.EMLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRowUrlPreview extends EaseChatRow {
   private TextView contentView;
   private TextView title;
   private TextView description;
   private EaseImageView icon;
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

      UrlPreViewBean urlPreviewInfo = DemoHelper.getInstance().getUrlPreviewInfo(message.getMsgId());
      if (urlPreviewInfo == null ){
         parsingUrl(spans[0].getURL(),message.getMsgId(),new EMValueCallBack<UrlPreViewBean>() {
            @Override
            public void onSuccess(UrlPreViewBean value) {
                itemActionCallback.refreshView();
            }

            @Override
            public void onError(int error, String errorMsg) {
               quoteItem.setVisibility(GONE);
               EMLog.e("ChatRowUrlPreview","parsingUrl onError" + errorMsg + error);
            }
         });
      }else {
         if (TextUtils.isEmpty(urlPreviewInfo.getTitle())){
            quoteItem.setVisibility(GONE);
         }else {
            quoteItem.setVisibility(VISIBLE);
            title.setText(urlPreviewInfo.getTitle());
            description.setText(urlPreviewInfo.getContent());
            if (urlPreviewInfo.getPrimaryImg().endsWith(".gif")){
               Glide.with(context)
                       .asGif().load(urlPreviewInfo.getPrimaryImg())
                       .placeholder(R.drawable.em_icon_preview_error)
                       .error(R.drawable.em_icon_preview_error)
                       .into(icon);
            }else {
               Glide.with(context)
                       .load(urlPreviewInfo.getPrimaryImg())
                       .placeholder(R.drawable.em_icon_preview_error)
                       .error(R.drawable.em_icon_preview_error)
                       .into(icon);
            }
         }
      }

      if (index != -1) {
         spannable.removeSpan(spans[0]);
         spannable.setSpan(new AutolinkSpan(spans[0].getURL()), index
                 , end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
      }
   }

   private void parsingUrl(String url,String msgId, EMValueCallBack<UrlPreViewBean> callBack){
      EaseThreadManager.getInstance().runOnIOThread(()->{
         try {
            UrlPreViewBean urlPreViewBean = new UrlPreViewBean();
            String descriptionContent = "";
            String logoUrl = "";
            String src = "";
            Document document = Jsoup.connect(url)
                    .header("content-type","text/html;charset=utf-8")
                    .timeout(5000).get();
            String title = document.title();

            Element description = document.select("head meta[name=description]").first();

            Element metaTag = document.selectFirst("head meta[property=og:image]");
            if (metaTag != null){
               src = metaTag.attr("content");
            }

            Elements linkTags = document.select("head link");
            if (linkTags != null){
               // 遍历linkTags，解析相关属性值
               for (Element linkTag : linkTags) {
                  String href = linkTag.attr("href");
                  String rel = linkTag.attr("rel");

                  // 如果rel属性值为"apple-touch-icon-precomposed"，则输出href属性值
                  if (rel.equals("apple-touch-icon-precomposed") && DemoHelper.getInstance().isPicture(href)) {
                     src = href;
                  }
               }
            }

            Element logoElement = document.select("link[rel='icon']").first();
            if (logoElement != null && TextUtils.isEmpty(src)){
               src = logoElement.attr("href");
            }

            Element imgElement = document.selectFirst("img");
            if (imgElement != null && TextUtils.isEmpty(src)){
               src = imgElement.absUrl("src");
            }

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

            urlPreViewBean.setTitle(title);//标题
            urlPreViewBean.setPrimaryImg(logoUrl); // 首图
            urlPreViewBean.setContent(descriptionContent); // 内容

            EMLog.d("ChatRowUrlPreview",
                    "title:" + title +"\n"
                     + "description " + descriptionContent + "\n"
                     + "logo " + logoUrl + "\n");

            DemoHelper.getInstance().saveUrlPreviewInfo(msgId, urlPreViewBean);
            post(()->{
               callBack.onSuccess(urlPreViewBean);
            });
         } catch (IOException e) {
            e.printStackTrace();
            post(()->{
               callBack.onError(1,e.getMessage());
            });
         }
      });
   }

   public boolean checkContainsUrl(String content){
      Pattern p = Pattern.compile(URL_REGEX);
      Matcher m = p.matcher(content);
      return m.find();
   }

}
