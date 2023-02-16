package com.example.jingbin.webviewstudy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.jingbin.webviewstudy.MainActivity;
import com.example.jingbin.webviewstudy.R;
import com.example.jingbin.webviewstudy.config.MyJavascriptInterface;
import com.example.jingbin.webviewstudy.utils.StatusBarUtil;
import com.example.jingbin.webviewstudy.utils.WebTools;
import com.example.jingbin.webviewstudy.view.WVJBWebView;

import org.json.JSONObject;

import me.jingbin.web.ByWebTools;
import me.jingbin.web.ByWebView;
import me.jingbin.web.OnByWebClientCallback;
import me.jingbin.web.OnTitleProgressCallback;

/**
 * @author jingbin
 * 自定义WebView，使用 WVJBWebView(https://github.com/wendux/WebViewJavascriptBridge)。
 * 更方面的调用js方法和js调用原生方法，因为有些最新的h5代码里调用js方法使用自带的WebView不能完成。
 * <p>
 * link to https://github.com/youlookwhat/ByWebView
 */
public class WVJBWebViewActivity extends AppCompatActivity {

    // 网页链接
    private int mState;
    private String mUrl;
    private String mTitle;
    private WVJBWebView webView;
    private ByWebView byWebView;
    private TextView tvGunTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_webview);
        getIntentData();
        initTitle();
        getDataFromBrowser(getIntent());
    }

    private void getIntentData() {
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mState = getIntent().getIntExtra("state", 0);
    }

    private void initTitle() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        initToolBar();
        LinearLayout container = findViewById(R.id.ll_container);
        byWebView = ByWebView
                .with(this)
                .setWebParent(container, new LinearLayout.LayoutParams(-1, -1))
                .useWebProgress(ContextCompat.getColor(this, R.color.colorRed))
                .setOnTitleProgressCallback(onTitleProgressCallback)
                .setOnByWebClientCallback(onByWebClientCallback)
                .setCustomWebView(new WVJBWebView(this))
                .addJavascriptInterface("injectedObject", new MyJavascriptInterface(this))
                .loadUrl(mUrl);
        webView = (WVJBWebView) byWebView.getWebView();

        // 原生调用h5方法，传参
        webView.callHandler("uploadFileToWeb", "图片链接", new WVJBWebView.WVJBResponseCallback<Object>() {
            @Override
            public void onResult(Object data) {
                Log.e("uploadFileToWeb", data.toString());
            }
        });
        // h5调用原生方法，传参(json)
        webView.registerHandler("callJavaJSBridge", new WVJBWebView.WVJBHandler<Object, Object>() {
            @Override
            public void handler(final Object data, final WVJBWebView.WVJBResponseCallback<Object> callback) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String json = (String) data;
//                            JSONObject jsonObject = (JSONObject) data;
//                            String json = jsonObject.toString();
                            Log.e("---", "传参json:" + json);
                            callback.onResult("我是h5调用原生方法后，回调回来的数据");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void initToolBar() {
        // 可滚动的title 使用简单 没有渐变效果，文字两旁有阴影
        Toolbar mTitleToolBar = findViewById(R.id.title_tool_bar);
        tvGunTitle = findViewById(R.id.tv_gun_title);
        setSupportActionBar(mTitleToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mTitleToolBar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_more));
        tvGunTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvGunTitle.setSelected(true);
            }
        }, 1900);
        tvGunTitle.setText(mTitle);
    }

    private OnTitleProgressCallback onTitleProgressCallback = new OnTitleProgressCallback() {
        @Override
        public void onReceivedTitle(String title) {
            Log.e("---title", title);
            tvGunTitle.setText(title);
        }

        /**
         * 全屏显示时处理横竖屏。
         * 默认返回false，全屏时为横屏，全屏还原后为竖屏
         * 如果要手动处理，需要返回true！
         *
         * @param isShow 是否显示了全屏视频 true点击了全屏显示，false全屏视频还原
         */
        @Override
        public boolean onHandleScreenOrientation(boolean isShow) {
            return super.onHandleScreenOrientation(isShow);
        }
    };

    private OnByWebClientCallback onByWebClientCallback = new OnByWebClientCallback() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e("---onPageStarted", url);
        }

        @Override
        public boolean onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 如果自己处理，需要返回true
            return super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // 网页加载完成后的回调
            loadCallJs();
        }

        @Override
        public boolean isOpenThirdApp(String url) {
            // 处理三方链接
            Log.e("---url", url);
            return ByWebTools.handleThirdApp(WVJBWebViewActivity.this, url);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 返回键
                handleFinish();
                break;
            case R.id.actionbar_share:// 分享到
                String shareText = webView.getTitle() + webView.getUrl();
                WebTools.share(WVJBWebViewActivity.this, shareText);
                break;
            case R.id.actionbar_cope:// 复制链接
                WebTools.copy(webView.getUrl());
                Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
                break;
            case R.id.actionbar_open:// 打开链接
                WebTools.openLink(WVJBWebViewActivity.this, webView.getUrl());
                break;
            case R.id.actionbar_webview_refresh:// 刷新页面
                byWebView.reload();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 传应用内的数据给html，方便html处理
     */
    private void loadCallJs() {
        // java调用js方法，没用通过JSBridge，暂不知道html里应该怎么写
        byWebView.getLoadJsHolder().quickCallJs("javaCallJS");
    }

    /**
     * 上传图片之后的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        byWebView.handleFileChooser(requestCode, resultCode, intent);
    }

    /**
     * 使用singleTask启动模式的Activity在系统中只会存在一个实例。
     * 如果这个实例已经存在，intent就会通过onNewIntent传递到这个Activity。
     * 否则新的Activity实例被创建。
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDataFromBrowser(intent);
    }

    /**
     * 作为三方浏览器打开传过来的值
     * Scheme: https
     * host: www.jianshu.com
     * path: /p/1cbaf784c29c
     * url = scheme + "://" + host + path;
     */
    private void getDataFromBrowser(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            try {
                String scheme = data.getScheme();
                String host = data.getHost();
                String path = data.getPath();
                String text = "Scheme: " + scheme + "\n" + "host: " + host + "\n" + "path: " + path;
                Log.e("data", text);
                String url = scheme + "://" + host + path;
                byWebView.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 直接通过三方浏览器打开时，回退到首页
     */
    public void handleFinish() {
        supportFinishAfterTransition();
        if (!MainActivity.isLaunch) {
            MainActivity.start(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (byWebView.handleKeyEvent(keyCode, event)) {
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                handleFinish();
            }
            return super.onKeyDown(keyCode, event);
        }
    }

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
        super.onDestroy();
        webView.clear();
        byWebView.onDestroy();
    }

    /**
     * 打开网页:
     *
     * @param mContext 上下文
     * @param url      要加载的网页url
     * @param title    标题
     * @param state    类型
     */
    public static void loadUrl(Context mContext, String url, String title, int state) {
        Intent intent = new Intent(mContext, WVJBWebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("state", state);
        intent.putExtra("title", title == null ? "加载中..." : title);
        mContext.startActivity(intent);
    }
}
