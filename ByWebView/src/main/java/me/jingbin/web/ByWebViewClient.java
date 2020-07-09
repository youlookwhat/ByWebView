package me.jingbin.web;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;

/**
 * Created by jingbin on 2020/06/30
 * 监听网页链接:
 * - 根据标识:打电话、发短信、发邮件
 * - 进度条的显示
 * - 添加javascript监听
 * - 唤起京东，支付宝，微信原生App
 */
public class ByWebViewClient extends WebViewClient {

    private WeakReference<Activity> mActivityWeakReference = null;
    private ByWebView mByWebView;
    private OnByWebClientCallback onByWebClientCallback;

    ByWebViewClient(Activity activity, ByWebView byWebView) {
        mActivityWeakReference = new WeakReference<Activity>(activity);
        this.mByWebView = byWebView;
    }

    void setOnByWebClientCallback(OnByWebClientCallback onByWebClientCallback) {
        this.onByWebClientCallback = onByWebClientCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (onByWebClientCallback != null) {
            return onByWebClientCallback.isOpenThirdApp(url);
        } else {
            Activity mActivity = this.mActivityWeakReference.get();
            if (mActivity != null && !mActivity.isFinishing()) {
                return ByWebTools.handleThirdApp(mActivity, url);
            } else {
                return !url.startsWith("http:") && !url.startsWith("https:");
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (onByWebClientCallback != null) {
            return onByWebClientCallback.isOpenThirdApp(url);
        } else {
            Activity mActivity = this.mActivityWeakReference.get();
            if (mActivity != null && !mActivity.isFinishing()) {
                return ByWebTools.handleThirdApp(mActivity, url);
            } else {
                return !url.startsWith("http:") && !url.startsWith("https:");
            }
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        // html加载完成之后，添加监听图片的点击js函数
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity != null && !mActivity.isFinishing()
                && !ByWebTools.isNetworkConnected(mActivity) && mByWebView.getProgressBar() != null) {
            mByWebView.getProgressBar().hide();
        }
        if (onByWebClientCallback != null) {
            onByWebClientCallback.onPageFinished(view, url);
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        // 6.0以下执行
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        mByWebView.showErrorView();
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        // 这个方法在 android 6.0才出现。加了正常的页面可能会出现错误页面
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int statusCode = errorResponse.getStatusCode();
//            if (404 == statusCode || 500 == statusCode) {
//                mByWebView.showErrorView();
//            }
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (request.isForMainFrame()) {
            // 是否是为 main frame创建
            mByWebView.showErrorView();
        }
    }

    /**
     * 解决google play上线 WebViewClient.onReceivedSslError问题
     */
    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("SSL认证失败，是否继续访问？");
        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 视频全屏播放按返回页面被放大的问题
     */
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if (newScale - oldScale > 7) {
            //异常放大，缩回去。
            view.setInitialScale((int) (oldScale / newScale * 100));
        }
    }

}
