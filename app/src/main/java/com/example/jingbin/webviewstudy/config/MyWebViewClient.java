package com.example.jingbin.webviewstudy.config;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jingbin.webviewstudy.WebViewActivity;
import com.example.jingbin.webviewstudy.utils.CheckNetwork;

/**
 * Created by jingbin on 2016/11/17.
 * 监听网页链接:
 * - 优酷视频直接跳到自带浏览器
 * - 根据标识:打电话、发短信、发邮件
 * - 进度条的显示
 * - 添加javascript监听
 */

public class MyWebViewClient extends WebViewClient {

    private IWebPageView iWebPageView;
    private WebViewActivity activity;

    public MyWebViewClient(IWebPageView iWebPageView) {
        this.iWebPageView = iWebPageView;
        activity = (WebViewActivity) iWebPageView;

    }

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
            activity.startActivity(intent);
            return true;

            // 电话、短信、邮箱
        } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return true;
        }
        iWebPageView.startProgress();
        view.loadUrl(url);
        return false;
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        if (activity.progress90) {
            iWebPageView.hindProgressBar();
        } else {
            activity.pageFinish = true;
        }
        if (!CheckNetwork.isNetworkConnected(activity)) {
            iWebPageView.hindProgressBar();
        }
        // html加载完成之后，添加监听图片的点击js函数
        iWebPageView.addImageClickListener();
        super.onPageFinished(view, url);
    }

    // 视频全屏播放按返回页面被放大的问题
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if(newScale - oldScale > 7) {
            view.setInitialScale((int)(oldScale / newScale * 100)); //异常放大，缩回去。
        }
    }
}
