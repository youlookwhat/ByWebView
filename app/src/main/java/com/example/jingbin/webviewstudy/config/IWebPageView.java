package com.example.jingbin.webviewstudy.config;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by jingbin on 2019/07/27.
 */
public interface IWebPageView {

    /**
     * 显示webview
     */
    void showWebView();

    /**
     * 隐藏webview
     */
    void hindWebView();

    /**
     * 进度条变化时调用
     *
     * @param newProgress 进度0-100
     */
    void startProgress(int newProgress);

    /**
     * 添加视频全屏view
     */
    void fullViewAddView(View view);

    /**
     * 显示全屏view
     */
    void showVideoFullView();

    /**
     * 隐藏全屏view
     */
    void hindVideoFullView();

    /**
     * 设置横竖屏
     */
    void setRequestedOrientation(int screenOrientationPortrait);

    /**
     * 得到全屏view
     */
    FrameLayout getVideoFullView();

    /**
     * 加载视频进度条
     */
    View getVideoLoadingProgressView();

    /**
     * 返回标题处理
     */
    void onReceivedTitle(WebView view, String title);

    /**
     * 上传图片打开文件夹
     */
    void startFileChooserForResult(Intent intent, int requestCode);

    /**
     * 页面加载结束，添加js监听等
     */
    void onPageFinished(WebView view, String url);

    /**
     * 是否处理打开三方app
     * @param url
     */
    boolean isOpenThirdApp(String url);
}
