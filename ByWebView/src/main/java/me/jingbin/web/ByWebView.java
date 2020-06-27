package me.jingbin.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

/**
 * Created by jingbin on 2020-06-26.
 */
public class ByWebView {

    private WebProgress mProgressBar;
    private WebView mWebView;
    private Builder builder;
    private Activity activity;

    private ByWebView(Builder builder) {
        this.builder = builder;
        this.activity = builder.mActivity;

        RelativeLayout relativeLayout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(activity);
        relativeLayout.addView(mWebView, layoutParams);
        // 进度条布局
        handleWebProgress(builder, relativeLayout);
        builder.mWebContainer.addView(relativeLayout, builder.mLayoutParams);
        // 配置
        handleSetting();
//        mWebChromeClient = new ByWebChromeClient(this);
//        webView.setWebChromeClient(mWebChromeClient);
    }

    /**
     * 4.4以上可用 evaluateJavascript 效率高
     */
    public void loadJs(String jsString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(jsString, null);
        } else {
            mWebView.loadUrl(jsString);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void handleSetting() {
        WebSettings ws = mWebView.getSettings();
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
        // setDefaultZoom  api19被弃用
        // 网页内容的宽度自适应屏幕
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);
        // 排版适应屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        } else {
            ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        // WebView是否新窗口打开(加了后可能打不开网页)
//        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
        ws.setTextZoom(100);
    }

    private void handleWebProgress(Builder builder, RelativeLayout relativeLayout) {
        if (builder.mUseWebProgress) {
            mProgressBar = new WebProgress(activity);
            if (builder.mProgressStartColor != 0 && builder.mProgressEndColor != 0) {
                mProgressBar.setColor(builder.mProgressStartColor, builder.mProgressEndColor);
            } else if (builder.mProgressStartColor != 0) {
                mProgressBar.setColor(builder.mProgressStartColor, builder.mProgressStartColor);
            } else if (!TextUtils.isEmpty(builder.mProgressStartColorString)
                    && !TextUtils.isEmpty(builder.mProgressEndColorString)) {
                mProgressBar.setColor(builder.mProgressStartColorString, builder.mProgressEndColorString);
            } else if (!TextUtils.isEmpty(builder.mProgressStartColorString)
                    && TextUtils.isEmpty(builder.mProgressEndColorString)) {
                mProgressBar.setColor(builder.mProgressStartColorString, builder.mProgressStartColorString);
            }
            if (builder.mProgressHeightDp != 0) {
                mProgressBar.setHeight(builder.mProgressHeightDp);
            }
            mProgressBar.setVisibility(View.GONE);
            relativeLayout.addView(mProgressBar);
        }
    }

    public void loadUrl(String url) {
        if (!TextUtils.isEmpty(url) && url.endsWith("mp4") && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mWebView.loadData(ByWebTools.getVideoHtmlBody(url), "text/html", "UTF-8");
        } else {
            mWebView.loadUrl(url);
        }
        mProgressBar.show();
    }

    public void reload() {
        mWebView.reload();
    }

    public WebView getWebView() {
        return mWebView;
    }

    public WebProgress getProgressBar() {
        return mProgressBar;
    }

    public static Builder with(@NonNull Activity activity) {
        if (activity == null) {
            throw new NullPointerException("activity can not be null .");
        }
        return new Builder(activity);
    }


    public static class Builder {
        private Activity mActivity;
        private Fragment mFragment;
        // 加载链接
        private String mUrl;
        // 默认使用进度条
        private boolean mUseWebProgress = true;
        // 进度条 开始颜色
        private int mProgressStartColor;
        private String mProgressStartColorString;
        // 进度条 结束颜色
        private int mProgressEndColor;
        private String mProgressEndColorString;
        // 进度条 高度
        private int mProgressHeightDp;
        private ViewGroup mWebContainer;
        private ViewGroup.LayoutParams mLayoutParams;
        private WebSettings mWebSettings;

        public Builder(Activity activity) {
            this.mActivity = activity;
        }

        public Builder(Activity activity, Fragment fragment) {
            this.mActivity = activity;
            this.mFragment = fragment;
        }

        /**
         * WebView容器
         */
        public Builder setWebParent(@NonNull ViewGroup webContainer, ViewGroup.LayoutParams layoutParams) {
            this.mWebContainer = webContainer;
            this.mLayoutParams = layoutParams;
            return this;
        }

        /**
         * @param isUser 是否使用进度条，默认true
         */
        public Builder useWebProgress(boolean isUser) {
            this.mUseWebProgress = isUser;
            return this;
        }

        public Builder useWebProgress(int color) {
            return useWebProgress(color, color, 3);
        }

        public Builder useWebProgress(String color) {
            return useWebProgress(color, color, 3);
        }

        public Builder useWebProgress(String startColor, String endColor, int heightDp) {
            mProgressStartColorString = startColor;
            mProgressEndColorString = endColor;
            mProgressHeightDp = heightDp;
            return this;
        }

        public Builder useWebProgress(int startColor, int endColor, int heightDp) {
            mProgressStartColor = startColor;
            mProgressEndColor = endColor;
            mProgressHeightDp = heightDp;
            return this;
        }

        public Builder setWebSettings(WebSettings webSettings) {
            this.mWebSettings = webSettings;
            return this;
        }


        public static Builder with(@NonNull Activity activity) {
            if (activity == null) {
                throw new NullPointerException("activity can not be null .");
            }
            return new Builder(activity);
        }

        public static Builder with(@NonNull Fragment fragment) {
            Activity mActivity = null;
            if ((mActivity = fragment.getActivity()) == null) {
                throw new NullPointerException("activity can not be null .");
            }
            return new Builder(mActivity, fragment);
        }


//        public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {
//            if (mIEventHandler == null) {
//                mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
//            }
//            return mIEventHandler.onKeyDown(keyCode, keyEvent);
//        }

        public ByWebView loadUrl(String url) {
            ByWebView byWebView = new ByWebView(this);
            byWebView.loadUrl(url);
            return byWebView;
        }
    }

}
