# 环信/EaseIM
--------

## 重要通知
⚠️ **本项目已停止维护**  
由于技术栈升级，环信官方已全面转向Kotlin开发。基于Java的此项目版本不再维护更新。

请开发者迁移至新版Kotlin项目：  
👉 [easemob-demo-android](https://github.com/easemob/easemob-demo-android)

--------
## 简介
环信App是基于环信SDK开发的一款完整的类微信的聊天APP。展示的功能包括：注册新用户，用户登录，添加好友，单聊，群聊，发送文字，表情，语音，图片，地理位置等消息，以及实时音视频通话等。
## 创建应用并获取AppKey
运行demo之前，请务必先到[环信管理后台](https://console.easemob.com/user/register)创建您自己的应用并获取到Appkey，然后将获取到的Appkey配置到AndroidManifest.xml中。
>```Java
>   <!-- 设置环信应用的AppKey -->
>   <meta-data android:name="EASEMOB_APPKEY"  android:value="创建应用获取到的Appkey" />
>```
注册并创建应用的详情介绍，请移步环信官网：[注册并创建应用](http://docs-im.easemob.com/im/quickstart/guide/experience#%E6%B3%A8%E5%86%8C%E5%B9%B6%E5%88%9B%E5%BB%BA%E5%BA%94%E7%94%A8)
## 如何集成环信IM SDK
请移步环信官网：[Android SDK 介绍及导入](http://docs-im.easemob.com/im/android/sdk/import)
## 体验环信App
请移步环信官网：[场景DEMO及源码下载](https://www.easemob.com/download/im)
## 应用架构介绍
环信APP采用谷歌官方建议的应用架构：
![](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)
此架构有如下优点：
>（1）UI和业务逻辑解耦。</br>
>（2）有效避免生命周期组件内存泄漏。</br>
>（3）提高模块可测试性。</br>
>（4）提高应用稳定性，有效降低以下异常发生概率。</br>

对于架构各部分的理解：
>（1）Activity/Fragment为View层，主要是负责数据的展示刷新和交互事件的触发。</br>
>（2）ViewModel 是用来保存应用UI数据的类，它会在配置变更（Configuration Change）后继续存在。</br>
>（3）LiveData是一个具有生命周期感知特性的可观察的数据保持类。</br>
>（4）Repository（仓库），主要是用于各种数据的业务请求操作( 网络请求和数据库查询)， 可以把它看作 Model 层的 一部分。</br>
>（5）Room 是在 Sqlite 之上添加的一个抽象层, 以便实现更加强大的数据库访问，其可直接返回 LiveData， 用于监听数据返回。</br>
## 工具要求
>（1）Android Studio 3.2或更高版本。</br>
>（2）SDK targetVersion至少为26。</br>
## 项目结构介绍
1、项目结构
>项目分为主module和两个本地依赖库。</br>
>easeui是环信为了方便开发者快速使用环信SDK开发的UI库。</br>
>player是播放视频库，依赖于easeui。</br>

2、主要功能介绍
项目是按照功能块进行项目分类，主要的功能块包含如下：</br>
聊天，会议，联系人，会话，群组及聊天室，系统消息及关于环信等模块
>chat模块，包含了聊天相关类，包含了聊天页面及其相关类，聊天历史类，音视频通话类等</br>
>conference模块，主要是会议相关类</br>
>contact模块，包含联系人列表，添加联系人，黑名单等</br>
>converstaion模块，主要是会话列表相关</br>
>dialog模块，是各类DialogFragment</br>
>group模块，主要包含群组和聊天室相关功能</br>
>login模块，主要包含登录，注册等功能</br>
>me模块，包含了个人信息，通用设置，开发者设置等功能</br>
>message模块，主要是系统消息功能</br>
>search模块，主要是各类搜索页面</br>

3、重要类介绍
>ChatActivity: 会话页面，核心类，主要逻辑写在 ChatFragment 中。ChatFragment 继承自 EaseChatFragment，EaseChatFragment实现了聊天列表功能，包含了发送文字，表情，图片等功能；ChatFragment展示了对EaseChatFragment的功能扩展实践。</br>
>DemoApplication：继承于系统的 Application 类，其 onCreate() 为整个程序的入口，相关的初始化操作都在这里面；</br>
>DemoHelper: Demo 全局帮助类，主要功能为初始化 EaseUI、环信 SDK 及 Demo 相关的实例，以及封装一些全局使用的方法；</br>
>MainActivity: 主页面，包含会话列表页面（ConversationListFragment）、联系人列表页（ContactListFragment）、设置页面（AboutMeFragment）；</br>
>ConversationListFragment：会话列表类，继承自EaseConversationListFragment。ConversationListFragment展示了对EaseConversationListFragment类的扩展；</br>
>ContactListFragment：联系人列表类，继承自EaseContactListFragment。ContactListFragment展示了对EaseContactListFragment类的扩展。</br>
## 环信IM基本功能介绍
[消息](http://docs-im.easemob.com/im/android/basics/message)</br>
[好友管理](http://docs-im.easemob.com/im/android/basics/buddy)</br>
[群组管理](http://docs-im.easemob.com/im/android/basics/group)</br>
[聊天室管理](http://docs-im.easemob.com/im/android/basics/chatroom)</br>
[多设备登录](http://docs-im.easemob.com/im/android/basics/multidevices)</br>
## 环信关于第三方推送的集成介绍
[第三方推送集成](http://docs-im.easemob.com/im/android/push/thirdpartypush)</br>
[设置当前登录用户的推送昵称](http://docs-im.easemob.com/im/android/push/nickname)</br>
[离线推送问题排查](http://docs-im.easemob.com/im/android/push/troubleshooting)</br>
## 可能会遇到的问题
1、如果遇到Error: Default interface methods are only supported starting with Android N (--min-api 24)问题</br>
需要指定JDK版本</br>
>```Java
>android {
>   ......
>
>    //指定jdk版本
>    compileOptions {
>        sourceCompatibility JavaVersion.VERSION_1_8
>        targetCompatibility JavaVersion.VERSION_1_8
>    }
>
>}
>```

2、解决Android9.0以上强制使用https的问题 

解决办法可以参考：[StackOverFlow](https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted)，也可以直接在AndroidManifest.xml文件的application标签中设置android:usesCleartextTraffic=“true”
>```Java
><application 
>    android:usesCleartextTraffic="true" > 
>   ......
></application>
>```

3、从github拉取源码，运行demo，遇到登录报缺失Appkey及注册demo崩溃

请先到[环信管理后台](https://console.easemob.com/user/register)创建您自己的应用并获取到Appkey，然后将获取到的Appkey配置到AndroidManifest.xml中。
>```Java
>   <!-- 设置环信应用的AppKey -->
>   <meta-data android:name="EASEMOB_APPKEY"  android:value="创建应用获取到的Appkey" />
>```
注册并创建应用的详情介绍，请移步环信官网：[注册并创建应用](http://docs-im.easemob.com/im/quickstart/guide/experience#%E6%B3%A8%E5%86%8C%E5%B9%B6%E5%88%9B%E5%BB%BA%E5%BA%94%E7%94%A8)
## 相关文档
请参考集成文档： http://docs-im.easemob.com/im/android/sdk/import
