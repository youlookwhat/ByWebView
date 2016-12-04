# WebViewStudy
> For the most part the android webview functional practice

## Contains the content

 - 拨打电话、发送短信、发送邮件
 - 上传图片(版本兼容)
 - 进度条设置
 - 字体大小设置
 - 返回网页上一层、显示网页标题
 - 全屏播放网络视频
 - **与JS交互**

 
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/view_00.png"></img>
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/电话短信邮件测试.png"></img>
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/上传图片.png"></img>
<img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/百度一下.png"></img>

## 推荐阅读
 - [https://developer.android.com/reference/android/webkit/WebSettings.html](https://developer.android.com/reference/android/webkit/WebSettings.html)
 - [http://www.jianshu.com/p/32d48ca7d0e0](http://www.jianshu.com/p/32d48ca7d0e0)
 - [http://www.apkfuns.com/android-webview%E4%B8%8Ejavascript%E4%BA%A4%E4%BA%92.html](http://www.apkfuns.com/android-webview%E4%B8%8Ejavascript%E4%BA%A4%E4%BA%92.html)
 - [http://www.jianshu.com/p/dbf9b7c04be5](http://www.jianshu.com/p/dbf9b7c04be5)
 - [http://www.jianshu.com/p/32d48ca7d0e0](http://www.jianshu.com/p/32d48ca7d0e0)

 
## 所遇问题
 - [WebView加载网页不显示图片解决办法](http://blog.csdn.net/u013320868/article/details/52837671)
 - [webview: 视频全屏播放按返回页面被放大的问题](http://blog.csdn.net/guozhiganggang/article/details/52097975)
 - [Failed to init browser shader disk cache.](https://chromium.googlesource.com/chromium/src/+/dc26192a317d9511ce983fd4b26b1130fe25761a)
 - [EGL_BAD_DISPLAY](http://forum.xda-developers.com/showthread.php?t=2212632)
 - [Unknown frame routing id: 3](https://chromium.googlesource.com/chromium/src.git/+/46.0.2478.0/content/browser/android/java/gin_java_bridge_message_filter.cc)
 - 找不到assets目录下资源：注意assets在哪层文件夹下！与AndroidManifest.xml同级
 - 视频播放宽度比webview设置的宽度大，超过屏幕：设置ws.setLoadWithOverviewMode(false);
 - onDestroy时的清除资源操作