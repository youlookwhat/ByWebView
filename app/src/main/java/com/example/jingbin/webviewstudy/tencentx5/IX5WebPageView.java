package com.example.jingbin.webviewstudy.tencentx5;

import android.view.View;

/**
 * Created by jingbin on 2016/11/17.
 */
public interface IX5WebPageView {

    /**
     * 隐藏进度条
     */
    void hindProgressBar();

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
     * 添加js监听
     */
    void addImageClickListener();

    /**
     * 添加视频全屏view
     */
    void fullViewAddView(View view);

    void showVideoFullView();

    void hindVideoFullView();
}
