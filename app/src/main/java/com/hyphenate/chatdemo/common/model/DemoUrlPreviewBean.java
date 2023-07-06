package com.hyphenate.chatdemo.common.model;

public class DemoUrlPreviewBean {
   private String title; // 标题
   private String content; // 内容
   private String primaryImg; // 主图

   public String getPrimaryImg() {
      return primaryImg;
   }

   public void setPrimaryImg(String primaryImg) {
      this.primaryImg = primaryImg;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public String toString() {
      return "DemoUrlPreviewBean{" +
              "title='" + title + '\'' +
              ", content='" + content + '\'' +
              ", primaryImg='" + primaryImg + '\'' +
              '}';
   }
}
