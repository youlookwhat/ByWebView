# WebViewStudy
[![fir.im][1]][2] ![](https://img.shields.io/github/stars/youlookwhat/WebViewStudy.svg?style=flat-square) ![](https://img.shields.io/github/forks/youlookwhat/WebViewStudy.svg?style=flat-square) ![GitHub watchers](https://img.shields.io/github/watchers/youlookwhat/WebViewStudy.svg?style=flat-square&label=Watch)

### Contains the content

 - 拨打电话、发送短信、发送邮件
 - 上传图片(版本兼容)
 - 进度条、字体大小设置
 - 返回网页上一层、显示网页标题
 - 全屏播放网络视频
 - **与JS交互实例**
 - DeepLink的基本使用
 - 被作为第三方浏览器打开

### 文档
 - [Android 关于WebView全方面的使用（项目应用篇）](http://www.jianshu.com/p/163d39e562f0)
 - [Android DeepLink介绍与使用](https://www.jianshu.com/p/127c80f62655)
 - [Android 应用被作为第三方浏览器打开](https://www.jianshu.com/p/272bfb6c0779)
 - [Android WebView与JS交互实例](https://www.jianshu.com/p/97f52819a19d)
 
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/view_00.png"></img>
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/电话短信邮件测试.png"></img>
 <img width="260" height=“374” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/上传图片.png"></img>

#### 下载 
 - [https://fir.im/webviewstudy](https://fir.im/webviewstudy)

<img width="300" height=“300” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/download.png"></img>

### 修复
 - 修复显示多个进度条问题
 - 修复net::ERR_UNKNOWN+URL+SCHEME的问题
 - 可根据SCHEME跳京东，支付宝，微信原生App,用户可根据包名自行添加
 - 修复显示进度条问题
 - 修复上传图片页面没有显示“添加图片”的问题
 
### 所遇问题
 - [WebView加载网页不显示图片解决办法](http://blog.csdn.net/u013320868/article/details/52837671)
 - [webview: 视频全屏播放按返回页面被放大的问题](http://blog.csdn.net/guozhiganggang/article/details/52097975)
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
   
### 资料
 - [WebView的使用及实战](http://www.jianshu.com/p/dbf9b7c04be5)
 - [WebView性能、体验分析与优化](https://tech.meituan.com/WebViewPerf.html)
 - [Android WebView开发问题及优化汇总](http://www.cnblogs.com/spring87/p/4532687.html)
 - [https://developer.android.com/reference/android/webkit/WebSettings.html](https://developer.android.com/reference/android/webkit/WebSettings.html)
   
[1]:https://img.shields.io/badge/download-fir.im-brightgreen.svg?style=flat-square
[2]:https://fir.im/webviewstudy