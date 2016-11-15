package com.example.jingbin.webviewstudy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LoadImageWebviewActivity extends AppCompatActivity {

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    WebView webview_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webview_list = (WebView) findViewById(R.id.webview);
        initWebView();
        webview_list.setWebChromeClient(
                new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                        if (progress == 100) {
                            //handler.sendEmptyMessage(1);// 如果全部载入,隐藏进度对话框
                        }

                        super.onProgressChanged(view, progress);
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
                    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                        openFileChooserImplForAndroid5(uploadMsg);
                        return true;
                    }
                }
        );


//        webview_list.addJavascriptInterface(new BrowserInterface(this), "BrowserInterface");
//        webview_list.loadUrl("http://taoyanran.duapp.com/kaws/salvation/salvation.html");
        webview_list.loadUrl("https://v4-stage-api.kangaiweishi.com/v4/articles/fa1ffcd611934e80bb6e490bed15efb8.html");
    }


    private void initWebView() {
        WebSettings ws = webview_list.getSettings();
        ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        //   ws.setUseWideViewPort(true); /**合作商页面适应屏幕*/
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// 保存表单数据
//        ws.setBuiltInZoomControls(false);// 隐藏缩放按钮
        //----------缩放---------
//        webview_list.setOnTouchListener(this);//监听触摸事件
        ws.setSupportZoom(true);
        // 双击缩放
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
//        webview_list.setFocusable(true);
//        webview_list.requestFocus();
//        webview_list.setClickable(true);
//        webview_list.setLongClickable(true);
        //设置加载进来的页面自适应手机屏幕
//        ws.setLoadWithOverviewMode(true);

        //设置缓存模式
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        //缩放比例 1
        webview_list.setInitialScale(1);
//        ws.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        //---------------------
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        // 缩放排版:
//        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 适应屏幕
//        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        ws.setSupportMultipleWindows(true);// 新加
//        ws.setUseWideViewPort(true); /**合作商页面适应屏幕*/
        webview_list.setWebViewClient(new MyWebViewClient());
    }


    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http://v.youku.com/")) {
                return true;
            } else {
                view.loadUrl(url);
            }
            return false;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            // html加载完成之后，添加监听图片的点击js函数
            //  stopProgressDialog();
            // mProgressBar.setVisibility(View.GONE);
//            addImageClickListener();
//            view.loadUrl("javascript:window.android.test();");
            super.onPageFinished(view, url);
        }

        //        webView默认是不处理https请求的，页面显示空白，需要进行如下设置：
//         onReceivedSslError为webView处理ssl证书设置
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
        }
    }


    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webview_list.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //获取历史列表
            WebBackForwardList mWebBackForwardList = webview_list
                    .copyBackForwardList();
            //判断当前历史列表是否最顶端,其实canGoBack已经判断过
            if (mWebBackForwardList.getCurrentIndex() > 0) {
                webview_list.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
//                        progressBar.show();// 显示进度对话框
                        break;
                    case 1:
//                        progressBar.hide();// 隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
                        break;
                }
            }

            super.handleMessage(msg);
        }
    };

    public class BrowserInterface {
        private Context ctx;

        public BrowserInterface(Context context) {
            this.ctx = context;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(ctx, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public boolean isLogin() {
            return true;
        }

        @JavascriptInterface
        public String getCurrentUserId() {
            return "gfdsg";
        }

        @JavascriptInterface
        public String getCity() {
            return "bri";
        }

        @JavascriptInterface
        public void closeBrowser() {
            ((Activity) ctx).finish();
        }

        @JavascriptInterface
        public void startLogin() {
        }

    }

    public static void view(Context mContext) {
        Intent intent = new Intent(mContext, LoadImageWebviewActivity.class);
        mContext.startActivity(intent);
    }
}
