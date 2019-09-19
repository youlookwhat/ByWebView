package com.example.jingbin.webviewstudy.tencentx5;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by jingbin on 2019/01/15.
 * 监听网页链接:
 * - 根据标识:打电话、发短信、发邮件
 * - 进度条的显示
 * - 添加javascript监听
 * - 唤起京东，支付宝，微信原生App
 */
public class MyX5WebViewClient extends com.tencent.smtt.sdk.WebViewClient {

    private IX5WebPageView mIWebPageView;
    private X5WebViewActivity mActivity;

    MyX5WebViewClient(IX5WebPageView mIWebPageView) {
        this.mIWebPageView = mIWebPageView;
        mActivity = (X5WebViewActivity) mIWebPageView;

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.e("jing", "----url:" + url);
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return mIWebPageView.isOpenThirdApp(url);
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        // html加载完成之后，添加监听图片的点击js函数
        mIWebPageView.onPageFinished(view, url);
        super.onPageFinished(view, url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(com.tencent.smtt.sdk.WebView view, int errorCode, String description, String
            failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (errorCode == 404) {
            //用javascript隐藏系统定义的404页面信息
            String data = "Page NO FOUND！";
            view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
        }
    }

    // SSL Error. Failed to validate the certificate chain,error: java.security.cert.CertPathValidatorExcept
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); //解决方案, 不要调用super.xxxx
    }

    // 视频全屏播放按返回页面被放大的问题
    @Override
    public void onScaleChanged(com.tencent.smtt.sdk.WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if (newScale - oldScale > 7) {
            view.setInitialScale((int) (oldScale / newScale * 100)); //异常放大，缩回去。
        }
    }

}
