# ByWebView
[![JitPack][5]][6] [![API][7]][8] [![download][1]][2] <!--[![version][3]][4] -->

<!--![](https://img.shields.io/github/stars/youlookwhat/WebViewStudy.svg?style=flat-square) ![](https://img.shields.io/github/forks/youlookwhat/WebViewStudy.svg?style=flat-square) ![GitHub watchers](https://img.shields.io/github/watchers/youlookwhat/WebViewStudy.svg?style=flat-square&label=Watch)
-->

## Features

 - 基本配置使用(宽度自适应、返回网页上一层、显示网页标题等)
 - 唤起三方应用(拨打电话、发送短信、发送邮件等)
 - 上传图片(版本兼容)
 - 错误页面处理
 - 全屏播放网络视频
 - **与Js交互实例**
 - 优雅的进度条显示控件

**Demo示例：**

 - DeepLink的基本使用
 - 被作为第三方浏览器打开
 - 与ToolBar联动，自定义WebView
 - **腾讯x5使用示例**

## Document

 - [Android 关于WebView全方面的使用（项目应用篇）](http://www.jianshu.com/p/163d39e562f0)
 - [Android DeepLink介绍与使用](https://juejin.cn/post/6844903954149539848)
 - [Android 应用被作为第三方浏览器打开](https://www.jianshu.com/p/272bfb6c0779)
 - [Android WebView与JS交互实例](https://www.jianshu.com/p/97f52819a19d)
 - [一款Android WebView进度条显示控件，使其加载进度平滑过渡](https://github.com/youlookwhat/WebProgress)

## 引用
1. Add the JitPack repository to your build file

 ```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
 ```
2. Add the dependency:

 ```java
dependencies {
	implementation 'com.github.youlookwhat:ByWebView:1.2.1'
}

 ```

## Use
```java
byWebView = ByWebView
        .with(this)
        .setWebParent(container, new LinearLayout.LayoutParams(-1, -1))
        .useWebProgress(ContextCompat.getColor(this, R.color.coloRed))
        .loadUrl(mUrl);
```

### 与Js交互
 - 调用Js方法：

```java
// 无参数调用
byWebView.getLoadJsHolder().quickCallJs("javacalljs");
// 传递参数调用
byWebView.getLoadJsHolder().quickCallJs("javacalljswithargs", "android传入到网页里的数据，有参");
```
 - Js调用Java方法：

```java
ByWebView.with(this)
	.addJavascriptInterface("injectedObject", new MyJavascriptInterface(this))
	.loadUrl(mUrl);
window.injectedObject.startFunction()
```

### 生命周期处理
```java
@Override
protected void onPause() {
    super.onPause();
    byWebView.onPause();
}

@Override
protected void onResume() {
    super.onResume();
    byWebView.onResume();
}

@Override
protected void onDestroy() {
    byWebView.onDestroy();
    super.onDestroy();
}
```

### 返回操作
```java
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (byWebView.handleKeyEvent(keyCode, event)) {
        return true;
    } else {
        return super.onKeyDown(keyCode, event);
    }
}
```

### 上传图片之后的回调
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    byWebView.handleFileChooser(requestCode, resultCode, intent);
}
```

### 监听 标题、进度条、WebViewClient
```java
byWebView = ByWebView
        .with(this)
        .setWebParent(container, new LinearLayout.LayoutParams(-1, -1))
        .useWebProgress(ContextCompat.getColor(this, R.color.coloRed))
        .setOnTitleProgressCallback(onTitleProgressCallback)
        .setOnByWebClientCallback(onByWebClientCallback)
        .addJavascriptInterface("injectedObject", new MyJavascriptInterface(this))
        .loadUrl(mUrl);
```
```java
private OnTitleProgressCallback onTitleProgressCallback = new OnTitleProgressCallback() {
    @Override
    public void onReceivedTitle(String title) {
        Log.e("---title", title);
    }

    @Override
    public void onProgressChanged(int newProgress) {
        // 返回的进度
    }

    @Override
    public boolean onHandleScreenOrientation(boolean isShow) {
        // 返回true，视频全屏时，横竖屏自己处理
        return false;
    }
};

private OnByWebClientCallback onByWebClientCallback = new OnByWebClientCallback() {

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        
    }

    @Override
    public boolean onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        // 如果自己处理，需要返回true
        return super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // 网页加载完成后的回调
    }

    @Override
    public boolean isOpenThirdApp(String url) {
        // 处理三方链接
        Log.e("---url", url);
        return ByWebTools.handleThirdApp(ByWebViewActivity.this, url);
    }
};
```

### 自定义错误页面

```java
ByWebView.with(this)
	.setErrorLayout(R.layout.by_load_url_error,"无法打开网页")
	.loadUrl(mUrl);
```

### 完整设置
```java
byWebView = ByWebView
        .with(this)
        .useWebProgress(true) // 是否使用进度条，默认true，如使用可不用配置
        .setWebParent(container, new LinearLayout.LayoutParams(-1, -1)) // 设置WebView父容器
        .useWebProgress("#ffb6cf", "#ff0000", 3)// 进度条渐变色(开始颜色，结束颜色，高度)
        .setOnTitleProgressCallback(onTitleProgressCallback)// title 和 progress 监听
        .setOnByWebClientCallback(onByWebClientCallback)    // WebViewClient监听
        .setErrorLayout(R.layout.by_load_url_error, "无法打开网页") // 设置错误页面及标题
        .addJavascriptInterface("injectedObject", new MyJavascriptInterface(this)) // 设置Js监听
        .loadUrl(mUrl);
```
 
## Screenshots
 
 ![bywebview](https://github.com/youlookwhat/ByWebView/blob/master/art/bywebview.png?raw=true)

## Download
 - [Demo示例下载][2]

<!--<img width="300" height=“300” src="https://github.com/youlookwhat/WebViewStudy/blob/master/art/download.png"></img>-->


 
## Tip
 - 混淆时应加上（通过JS向网页传值，如不加有时候会传值失败）:

   ```java
   -keepattributes *Annotation*
   -keepattributes *JavascriptInterface*
   -keepclassmembers class * {
      @android.webkit.JavascriptInterface <methods>;
   }
   
## Thanks
 - [AgentWeb](https://github.com/Justson/AgentWeb)

## Other
 - [WebView的使用及实战](http://www.jianshu.com/p/dbf9b7c04be5)
 - [WebView性能、体验分析与优化](https://tech.meituan.com/WebViewPerf.html)
 - [Android WebView开发问题及优化汇总](http://www.cnblogs.com/spring87/p/4532687.html)
 - [https://developer.android.com/reference/android/webkit/WebSettings.html](https://developer.android.com/reference/android/webkit/WebSettings.html)
   
[1]:https://img.shields.io/badge/download-apk-blue.svg?style=flat
[2]:https://github.com/youlookwhat/download/raw/main/ByWebView.apk

[3]:https://img.shields.io/badge/version-2.7.2-brightgreen.svg?style=flat
[4]:https://github.com/youlookwhat/download/raw/main/ByWebView.apk

[5]:https://jitpack.io/v/youlookwhat/ByWebView.svg
[6]:https://jitpack.io/#youlookwhat/ByWebView

[7]:https://img.shields.io/badge/API-14%2B-red.svg?style=flat
[8]:https://android-arsenal.com/api?level=14