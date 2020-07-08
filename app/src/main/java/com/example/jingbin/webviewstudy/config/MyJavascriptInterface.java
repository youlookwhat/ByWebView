package com.example.jingbin.webviewstudy.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.jingbin.webviewstudy.utils.WebTools;

/**
 * Created by jingbin on 2016/11/17.
 * js通信接口
 */
public class MyJavascriptInterface {

    private Context context;

    public MyJavascriptInterface(Context context) {
        this.context = context;
    }

    /**
     * 前端代码嵌入js：
     * imageClick 名应和js函数方法名一致
     *
     * @param src 图片的链接
     */
    @JavascriptInterface
    public void imageClick(String src) {
        Log.e("imageClick", "----点击了图片");
        Log.e("---src", src);
        WebTools.showToast(src);
    }

    /**
     * 前端代码嵌入js
     * 遍历<li>节点
     *
     * @param type    <li>节点下type属性的值
     * @param item_pk item_pk属性的值
     */
    @JavascriptInterface
    public void textClick(String type, String item_pk) {
        if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(item_pk)) {
            Log.e("textClick", "----点击了文字");
            Log.e("type", type);
            Log.e("item_pk", item_pk);
            WebTools.showToast("type: " + type + ", item_pk:" + item_pk);
        }
    }

    /**
     * 网页使用的js，方法无参数
     */
    @JavascriptInterface
    public void startFunction() {
        Log.e("startFunction", "----无参");
        WebTools.showToast("无参方法");
    }

    /**
     * 网页使用的js，方法有参数，且参数名为data
     *
     * @param data 网页js里的参数名
     */
    @JavascriptInterface
    public void startFunction(String data) {
        Log.e("startFunction", "----有参方法: " + data);
        WebTools.showToast("----有参方法: " + data);
    }

    /**
     * 获取网页源代码
     */
    @JavascriptInterface
    public void showSource(String html) {
        Log.e("showSourceCode", html);
    }
}
