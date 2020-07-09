package me.jingbin.web;

import android.webkit.WebView;

/**
 * Created by jingbin on 2020/6/30.
 */
public abstract class OnByWebClientCallback {

    public void onPageFinished(WebView view, String url) {

    }

    public boolean isOpenThirdApp(String url) {
        return !url.startsWith("http:") && !url.startsWith("https:");
    }
}
