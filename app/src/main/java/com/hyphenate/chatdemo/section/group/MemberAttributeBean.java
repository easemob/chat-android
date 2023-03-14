package com.hyphenate.chatdemo.section.group;

import java.io.Serializable;

public class MemberAttributeBean implements Serializable {

   /**
    * {"nickName":"apex1"}
    */

   private String nickName;

   public String getNickName() {
      return nickName;
   }

   public void setNickName(String nickName) {
      this.nickName = nickName;
   }

}
