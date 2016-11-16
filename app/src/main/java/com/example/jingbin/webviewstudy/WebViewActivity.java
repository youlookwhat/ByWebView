package com.example.jingbin.webviewstudy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.jingbin.webviewstudy.utils.CheckNetwork;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 网页可以处理:
 * 点击相应控件:拨打电话、发送短信、发送邮件、上传图片、播放视频
 * 进度条、返回网页上一层、显示网页标题
 */
public class WebViewActivity extends AppCompatActivity {

    /*进度条*/
    @BindView(R.id.pb_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.webview_detail)
    WebView webView;
    // 全屏时视频加载view
    @BindView(R.id.video_fullView)
    FrameLayout video_fullView;

    /*进度条是否加载到90%*/
    private boolean progress90;
    /*网页是否加载完成*/
    private boolean pageFinish;

    /*点击上传图片相关*/
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    /*加载视频相关*/
    private MyWebChromeClient xwebchromeclient;
    private View xCustomView;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        setTitle("详情");
        initWebView();
        viewPage();
    }

    private void viewPage() {
        String url = null;
        if (getIntent() != null) {
            url = getIntent().getStringExtra("url");
//            webView.loadUrl(url.startsWith("http") ? url : "http://" + url);

            webView.loadUrl(url); //加载本地assert目录下网页
        }
    }

    private void initWebView() {
        mProgressBar.setVisibility(View.VISIBLE);

        WebSettings ws = webView.getSettings();
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);

        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);

        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);

//        setDefaultZoom
        // This method was deprecated in API level 19. api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        //缩放比例 1
        webView.setInitialScale(1);

        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);

        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);
        // 排版适应屏幕
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否支持多个窗口。
        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

//        This enum was deprecated in API level 14. Use setTextZoom(int) and getTextZoom() instead.
        /** 设置字体默认缩放大小（默认总体为16sp，默认缩放比例为110）*/
//        int defaultTextZoom = SPUtils.getInt("UserInfo", "default_textZoom", 110);
//        ws.setTextZoom(defaultTextZoom);

        xwebchromeclient = new MyWebChromeClient();
        webView.setWebChromeClient(xwebchromeclient);
        // 与js交互
        webView.addJavascriptInterface(new ImageClickInterface(this), "injectedObject");

        webView.setWebViewClient(new MyWebViewClient());
    }

    public class MyWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 优酷视频跳转浏览器播放
            if (url.startsWith("http://v.youku.com/")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addCategory("android.intent.category.BROWSABLE");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
                return true;

                // 电话、短信、邮箱
            } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ignored) {
                }
                return true;
            }
            startProgress90();
            view.loadUrl(url);
            return false;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            // html加载完成之后，添加监听图片的点击js函数
            // mProgressBar.setVisibility(View.GONE);
            if (progress90) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                pageFinish = true;
            }
            if (!CheckNetwork.isNetworkConnected(WebViewActivity.this)) {
                mProgressBar.setVisibility(View.GONE);
            }
//            DebugUtil.debug(tag, "onPageFinished:" + pageFinish);
            addImageClickListener();
            super.onPageFinished(view, url);
        }
    }

    // 注入js函数监听
    private void addImageClickListener() {
//        Log.d(tag, "==addImageClickListener");
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                //  "objs[i].onclick=function(){alert(this.getAttribute(\"has_link\"));}" +
                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"has_link\"));}" +
                "}" +
                "})()");

        webView.loadUrl("javascript:(function(){" +
                "var objs =document.getElementsByTagName(\"a\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){" +
                "window.injectedObject.textClick(this.getAttribute(\"kaws_type\"),this.getAttribute(\"kaws_item_pk\"));}" +
                "}" +
                "})()");
    }

    /**
     * 进度条 假装加载到90%
     */
    public void startProgress90() {
        for (int i = 0; i < 900; i++) {
            final int progress = i + 1;
            mProgressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setProgress(progress);
                    if (progress == 900) {
                        progress90 = true;
                        if (pageFinish) {
                            startProgress90to100();
                        }
                    }
                }
            }, (i + 1) * 2);
        }
    }

    /**
     * 进度条 加载到100%
     */
    public void startProgress90to100() {
        for (int i = 900; i <= 1000; i++) {
            final int progress = i + 1;
            mProgressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setProgress(progress);
                    if (progress == 1000) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }, (i + 1) * 2);
        }
    }

    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }

    }

    public class MyWebChromeClient extends WebChromeClient {
        private View xprogressvideo;


        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            webView.setVisibility(View.INVISIBLE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            FrameLayout decor = (FrameLayout) getWindow().getDecorView();
            video_fullView = new FullscreenHolder(WebViewActivity.this);
            video_fullView.addView(view);
            decor.addView(video_fullView);

            xCustomView = view;
            xCustomViewCallback = callback;
            video_fullView.setVisibility(View.VISIBLE);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {
            if (xCustomView == null)// 不是全屏播放状态
                return;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);
            video_fullView.removeView(xCustomView);
            xCustomView = null;
            video_fullView.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();
            webView.setVisibility(View.VISIBLE);
        }

        // 视频加载时进程loading
        @Override
        public View getVideoLoadingProgressView() {
            if (xprogressvideo == null) {
                LayoutInflater inflater = LayoutInflater.from(WebViewActivity.this);
                xprogressvideo = inflater.inflate(R.layout.video_loading_progress, null);
            }
            return xprogressvideo;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            // mProgressBar.setProgress(newProgress);
            if (progress90) {
                int progress = newProgress * 100;
//                DebugUtil.debug(tag, "progress:" + progress);
                if (progress > 900) {
                    mProgressBar.setProgress(progress);
                    if (progress == 1000) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            // 设置title
            setTitle(title);
        }

        //扩展浏览器上传文件
        //3.0++版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooserImpl(uploadMsg);
        }

        //3.0--版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserImpl(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg);
        }

        // For Android > 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
            openFileChooserImplForAndroid5(uploadMsg);
            return true;
        }
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "图片选择");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        xwebchromeclient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {//全屏播放退出全屏
                hideCustomView();
                return true;
            } else if (webView.canGoBack()) {//返回上一页面
                webView.goBack();
                return true;
            } else {
                webView.loadUrl("about:blank");
                finish();
            }
        }
        return false;
    }

    /**
     * 判断是否是全屏
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        /** 支付宝网页版在打开文章详情之后,无法点击按钮下一步  xia 20160506*/
        webView.resumeTimers();
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        video_fullView.removeAllViews();
        if (webView != null) {
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
    }

    /**
     * 打开网页:
     *
     * @param url 要加载的网页url
     */
    public static void loadUrl(Context mContext, String url) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("url", url);
        mContext.startActivity(intent);
    }
}
