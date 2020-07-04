package me.jingbin.web;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jingbin on 2020/7/4.
 */
public class ByLoadJsHolder {

    private WebView mWebView;

    ByLoadJsHolder(WebView webView) {
        this.mWebView = webView;
    }

    public void loadJs(String js, final ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.evaluateJs(js, callback);
        } else {
            mWebView.loadUrl(js);
        }
    }

    public void loadJs(String js) {
        loadJs(js, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void evaluateJs(String js, final ValueCallback<String> callback) {
        mWebView.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (callback != null) {
                    callback.onReceiveValue(value);
                }
            }
        });
    }

    public void quickCallJs(String method, ValueCallback<String> callback, String... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:").append(method);
        if (params == null || params.length == 0) {
            sb.append("()");
        } else {
            sb.append("(").append(concat(params)).append(")");
        }
        loadJs(sb.toString(), callback);
    }

    private String concat(String... params) {
        StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (!isJson(param)) {
                mStringBuilder.append("\"").append(param).append("\"");
            } else {
                mStringBuilder.append(param);
            }
            if (i != params.length - 1) {
                mStringBuilder.append(" , ");
            }
        }
        return mStringBuilder.toString();
    }

    public void quickCallJs(String method, String... params) {
        this.quickCallJs(method, null, params);
    }

    public void quickCallJs(String method) {
        this.quickCallJs(method, (String[]) null);
    }

    static boolean isJson(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }
        boolean tag = false;
        try {
            if (target.startsWith("[")) {
                new JSONArray(target);
            } else {
                new JSONObject(target);
            }
            tag = true;
        } catch (JSONException ignore) {
//            ignore.printStackTrace();
            tag = false;
        }
        return tag;
    }
}
