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
 - [DeepLink的基本使用](https://jingbin.me/2018/07/02/deeplink-intro-use/)

具体说明地址:[Android 关于WebView全方面的使用（项目应用篇）](http://www.jianshu.com/p/163d39e562f0)
 
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/view_00.png"></img>
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/电话短信邮件测试.png"></img>
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/上传图片.png"></img>

### 下载 
 - [https://fir.im/webviewstudy](https://fir.im/webviewstudy)

<img width="300" height=“300” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/download.png"></img>

## 推荐阅读
 - [https://developer.android.com/reference/android/webkit/WebSettings.html](https://developer.android.com/reference/android/webkit/WebSettings.html)
 - [Android WebView总结](http://www.jianshu.com/p/32d48ca7d0e0)
 - [WebView的使用及实战](http://www.jianshu.com/p/dbf9b7c04be5)
 - [WebView性能、体验分析与优化](https://tech.meituan.com/WebViewPerf.html)
 - [Android WebView开发问题及优化汇总](http://www.cnblogs.com/spring87/p/4532687.html)
 - [Android WebView与JavaScript交互(注入js)](http://www.apkfuns.com/android-webview%E4%B8%8Ejavascript%E4%BA%A4%E4%BA%92.html)
 - [android WebView js调用android原生代码](http://blog.csdn.net/wangtingshuai/article/details/8631835)

## 修复
 - 修复显示多个进度条问题
 - 修复net::ERR_UNKNOWN+URL+SCHEME的问题
 - 可根据SCHEME跳京东，支付宝，微信原生App,用户可根据包名自行添加
 - 修复显示进度条问题
 - 修复上传图片页面没有显示“添加图片”的问题
 
## 所遇问题
 - [WebView加载网页不显示图片解决办法](http://blog.csdn.net/u013320868/article/details/52837671)
 - [webview: 视频全屏播放按返回页面被放大的问题](http://blog.csdn.net/guozhiganggang/article/details/52097975)
 - [Failed to init browser shader disk cache.](https://chromium.googlesource.com/chromium/src/+/dc26192a317d9511ce983fd4b26b1130fe25761a)
 - [EGL_BAD_DISPLAY](http://forum.xda-developers.com/showthread.php?t=2212632)
 - [Unknown frame routing id: 3](https://chromium.googlesource.com/chromium/src.git/+/46.0.2478.0/content/browser/android/java/gin_java_bridge_message_filter.cc)
 - 找不到assets目录下资源：注意assets在哪层文件夹下！与AndroidManifest.xml同级
 - 视频播放宽度比webview设置的宽度大，超过屏幕：设置ws.setLoadWithOverviewMode(false);
 - onDestroy时的清除资源操作
 - 通过js向网页内传值(待完善)
 - 混淆时应加上（[通过JS向网页传值，如不加有时候会传值失败](http://www.jianshu.com/p/f3b3e91575ee)）:
   ```java
   -keepattributes *Annotation*
   -keepattributes *JavascriptInterface*
   -keepclassmembers class * {
      @android.webkit.JavascriptInterface <methods>;
   }