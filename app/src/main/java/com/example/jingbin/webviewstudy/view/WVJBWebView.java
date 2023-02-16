package com.example.jingbin.webviewstudy.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by du on 16/12/29.
 * https://github.com/wendux/WebViewJavascriptBridge
 */
public class WVJBWebView extends WebView {

    private static final String BRIDGE_NAME = "WVJBInterface";
    private String APP_CACHE_DIRNAME;
    private static final int EXEC_SCRIPT = 1;
    private static final int LOAD_URL = 2;
    private static final int LOAD_URL_WITH_HEADERS = 3;
    private static final int HANDLE_MESSAGE = 4;
    MyHandler mainThreadHandler = null;
    private JavascriptCloseWindowListener javascriptCloseWindowListener = null;


    class MyHandler extends Handler {
        //  Using WeakReference to avoid memory leak
        WeakReference<Context> mContextReference;

        MyHandler(Context context) {
            super(Looper.getMainLooper());
            mContextReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final Context context = mContextReference.get();
            if (context != null) {
                switch (msg.what) {
                    case EXEC_SCRIPT:
                        _evaluateJavascript((String) msg.obj);
                        break;
                    case LOAD_URL:
                        WVJBWebView.super.loadUrl((String) msg.obj);
                        break;
                    case LOAD_URL_WITH_HEADERS: {
                        RequestInfo info = (RequestInfo) msg.obj;
                        WVJBWebView.super.loadUrl(info.url, info.headers);
                    }
                    break;
                    case HANDLE_MESSAGE:
                        WVJBWebView.this.handleMessage((String) msg.obj);
                        break;
                }
            }
        }
    }

    private class RequestInfo {
        String url;
        Map<String, String> headers;

        RequestInfo(String url, Map<String, String> additionalHttpHeaders) {
            this.url = url;
            this.headers = additionalHttpHeaders;
        }
    }

    private class WVJBMessage {
        Object data = null;
        String callbackId = null;
        String handlerName = null;
        String responseId = null;
        Object responseData = null;
    }


    public WVJBWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WVJBWebView(Context context) {
        super(context);
        init();
    }

    private ArrayList<WVJBMessage> startupMessageQueue = null;
    private Map<String, WVJBResponseCallback> responseCallbacks = null;
    private Map<String, WVJBHandler> messageHandlers = null;
    private long uniqueId = 0;
    private boolean alertboxBlock = true;

    public interface WVJBResponseCallback<T> {
        void onResult(T data);
    }

    public interface WVJBMethodExistCallback {
        void onResult(boolean exist);
    }


    public interface JavascriptCloseWindowListener {
        /**
         * @return If true, close the current activity, otherwise, do nothing.
         */
        boolean onClose();
    }


    public interface WVJBHandler<T, R> {
        void handler(T data, WVJBResponseCallback<R> callback);
    }

    public void disableJavascriptAlertBoxSafetyTimeout(boolean disable) {
        alertboxBlock = !disable;
    }

    public void callHandler(String handlerName) {
        callHandler(handlerName, null, null);
    }

    public void callHandler(String handlerName, Object data) {
        callHandler(handlerName, data, null);
    }

    public <T> void callHandler(String handlerName, Object data,
                                WVJBResponseCallback<T> responseCallback) {
        sendData(data, responseCallback, handlerName);
    }

    /**
     * Test whether the handler exist in javascript
     *
     * @param handlerName
     * @param callback
     */
    public void hasJavascriptMethod(String handlerName, final WVJBMethodExistCallback callback) {
        callHandler("_hasJavascriptMethod", handlerName, new WVJBResponseCallback() {
            @Override
            public void onResult(Object data) {
                callback.onResult((boolean) data);
            }
        });
    }

    /**
     * set a listener for javascript closing the current activity.
     */
    public void setJavascriptCloseWindowListener(JavascriptCloseWindowListener listener) {
        javascriptCloseWindowListener = listener;
    }

    public <T, R> void registerHandler(String handlerName, WVJBHandler<T, R> handler) {
        if (handlerName == null || handlerName.length() == 0 || handler == null) {
            return;
        }
        messageHandlers.put(handlerName, handler);
    }

    // send the onResult message to javascript
    private void sendData(Object data, WVJBResponseCallback responseCallback,
                          String handlerName) {
        if (data == null && (handlerName == null || handlerName.length() == 0)) {
            return;
        }
        WVJBMessage message = new WVJBMessage();
        if (data != null) {
            message.data = data;
        }
        if (responseCallback != null) {
            String callbackId = "java_cb_" + (++uniqueId);
            responseCallbacks.put(callbackId, responseCallback);
            message.callbackId = callbackId;
        }
        if (handlerName != null) {
            message.handlerName = handlerName;
        }
        queueMessage(message);
    }

    private synchronized void queueMessage(WVJBMessage message) {

        if (startupMessageQueue != null) {
            startupMessageQueue.add(message);
        } else {
            dispatchMessage(message);
        }
    }

    private void dispatchMessage(WVJBMessage message) {
        String messageJSON = message2JSONObject(message).toString();
        evaluateJavascript(String.format("WebViewJavascriptBridge._handleMessageFromJava(%s)", messageJSON));
    }

    // handle the onResult message from javascript
    private void handleMessage(String info) {
        try {
            JSONObject jo = new JSONObject(info);
            WVJBMessage message = JSONObject2WVJBMessage(jo);
            if (message.responseId != null) {
                WVJBResponseCallback responseCallback = responseCallbacks
                        .remove(message.responseId);
                if (responseCallback != null) {
                    responseCallback.onResult(message.responseData);
                }
            } else {
                WVJBResponseCallback responseCallback = null;
                if (message.callbackId != null) {
                    final String callbackId = message.callbackId;
                    responseCallback = new WVJBResponseCallback() {
                        @Override
                        public void onResult(Object data) {
                            WVJBMessage msg = new WVJBMessage();
                            msg.responseId = callbackId;
                            msg.responseData = data;
                            dispatchMessage(msg);
                        }
                    };
                }

                WVJBHandler handler;
                handler = messageHandlers.get(message.handlerName);
                if (handler != null) {
                    handler.handler(message.data, responseCallback);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JSONObject message2JSONObject(WVJBMessage message) {
        JSONObject jo = new JSONObject();
        try {
            if (message.callbackId != null) {
                jo.put("callbackId", message.callbackId);
            }
            if (message.data != null) {
                jo.put("data", message.data);
            }
            if (message.handlerName != null) {
                jo.put("handlerName", message.handlerName);
            }
            if (message.responseId != null) {
                jo.put("responseId", message.responseId);
            }
            if (message.responseData != null) {
                jo.put("responseData", message.responseData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    private WVJBMessage JSONObject2WVJBMessage(JSONObject jo) {
        WVJBMessage message = new WVJBMessage();
        try {
            if (jo.has("callbackId")) {
                message.callbackId = jo.getString("callbackId");
            }
            if (jo.has("data")) {
                message.data = jo.get("data");
            }
            if (jo.has("handlerName")) {
                message.handlerName = jo.getString("handlerName");
            }
            if (jo.has("responseId")) {
                message.responseId = jo.getString("responseId");
            }
            if (jo.has("responseData")) {
                message.responseData = jo.get("responseData");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }


    //    @Keep
    void init() {
        mainThreadHandler = new MyHandler(getContext());
        APP_CACHE_DIRNAME = getContext().getFilesDir().getAbsolutePath() + "/webcache";
        this.responseCallbacks = new HashMap<>();
        this.messageHandlers = new HashMap<>();
        this.startupMessageQueue = new ArrayList<>();
        WebSettings settings = getSettings();
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setAllowFileAccess(false);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCachePath(APP_CACHE_DIRNAME);
        settings.setUseWideViewPort(true);
        super.setWebChromeClient(mWebChromeClient);
        super.setWebViewClient(mWebViewClient);

        registerHandler("_hasNativeMethod", new WVJBHandler() {
            @Override
            public void handler(Object data, WVJBResponseCallback callback) {
                callback.onResult(messageHandlers.get(data) != null);
            }
        });
        registerHandler("_closePage", new WVJBHandler() {
            @Override
            public void handler(Object data, WVJBResponseCallback callback) {
                if (javascriptCloseWindowListener == null
                        || javascriptCloseWindowListener.onClose()) {
                    ((Activity) getContext()).onBackPressed();
                }
            }
        });
        registerHandler("_disableJavascriptAlertBoxSafetyTimeout", new WVJBHandler() {
            @Override
            public void handler(Object data, WVJBResponseCallback callback) {
                disableJavascriptAlertBoxSafetyTimeout((boolean) data);
            }
        });
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            super.addJavascriptInterface(new Object() {
                //                @Keep
                @JavascriptInterface
                public void notice(String info) {
                    if (mainThreadHandler != null) {
                        Message msg = mainThreadHandler.obtainMessage(HANDLE_MESSAGE, info);
                        mainThreadHandler.sendMessage(msg);
                    }
                }

            }, BRIDGE_NAME);
        }

    }

    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WVJBWebView.super.evaluateJavascript(script, null);
        } else {
            super.loadUrl("javascript:" + script);
        }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param script
     */
    public void evaluateJavascript(final String script) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            _evaluateJavascript(script);
        } else {
            if (mainThreadHandler != null) {
                Message msg = mainThreadHandler.obtainMessage(EXEC_SCRIPT, script);
                mainThreadHandler.sendMessage(msg);
            }
        }
    }


    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     */
    @Override
    public void loadUrl(String url) {
        if (mainThreadHandler != null) {
            Message msg = mainThreadHandler.obtainMessage(LOAD_URL, url);
            mainThreadHandler.sendMessage(msg);
        }
    }


    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     * @param additionalHttpHeaders
     */
    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (mainThreadHandler != null) {
            Message msg = mainThreadHandler.obtainMessage(LOAD_URL_WITH_HEADERS, new RequestInfo(url, additionalHttpHeaders));
            mainThreadHandler.sendMessage(msg);
        }
    }


    // proxy client
    WebChromeClient webChromeClient;
    WebViewClient webViewClient;

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        webChromeClient = client;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        webViewClient = client;
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress > 80) {
                try {
                    InputStream is = view.getContext().getAssets()
                            .open("WebViewJavascriptBridge.js");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    String js = new String(buffer);
                    evaluateJavascript(js);
//                    evaluateJavascript("file:///android_asset/WebViewJavascriptBridge.js");
//                    loadUrl("javascript:(\"file:///android_asset/WebViewJavascriptBridge.js\")()");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                synchronized (WVJBWebView.this) {
                    if (startupMessageQueue != null) {
                        for (int i = 0; i < startupMessageQueue.size(); i++) {
                            dispatchMessage(startupMessageQueue.get(i));
                        }
                        startupMessageQueue = null;
                    }
                }
            }

            if (webChromeClient != null) {
                webChromeClient.onProgressChanged(view, newProgress);
            } else {
                super.onProgressChanged(view, newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (webChromeClient != null) {
                webChromeClient.onReceivedTitle(view, title);
            } else {
                super.onReceivedTitle(view, title);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (webChromeClient != null) {
                webChromeClient.onReceivedIcon(view, icon);
            } else {
                super.onReceivedIcon(view, icon);
            }
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            if (webChromeClient != null) {
                webChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
            } else {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (webChromeClient != null) {
                webChromeClient.onShowCustomView(view, callback);
            } else {
                super.onShowCustomView(view, callback);
            }
        }


        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void onShowCustomView(View view, int requestedOrientation,
                                     CustomViewCallback callback) {
            if (webChromeClient != null) {
                webChromeClient.onShowCustomView(view, requestedOrientation, callback);
            } else {
                super.onShowCustomView(view, requestedOrientation, callback);
            }
        }

        @Override
        public void onHideCustomView() {
            if (webChromeClient != null) {
                webChromeClient.onHideCustomView();
            } else {
                super.onHideCustomView();
            }
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            if (webChromeClient != null) {
                return webChromeClient.onCreateWindow(view, isDialog,
                        isUserGesture, resultMsg);
            }
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public void onRequestFocus(WebView view) {
            if (webChromeClient != null) {
                webChromeClient.onRequestFocus(view);
            } else {
                super.onRequestFocus(view);
            }
        }

        @Override
        public void onCloseWindow(WebView window) {
            if (webChromeClient != null) {
                webChromeClient.onCloseWindow(window);
            } else {
                super.onCloseWindow(window);
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
            if (!alertboxBlock) {
                result.confirm();
            }
            if (webChromeClient != null) {
                if (webChromeClient.onJsAlert(view, url, message, result)) {
                    return true;
                }
            }
            Dialog alertDialog = new AlertDialog.Builder(getContext()).
                    setMessage(message).
                    setCancelable(false).
                    setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (alertboxBlock) {
                                result.confirm();
                            }
                        }
                    })
                    .create();
            alertDialog.show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            if (!alertboxBlock) {
                result.confirm();
            }
            if (webChromeClient != null && webChromeClient.onJsConfirm(view, url, message, result)) {
                return true;
            } else {
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertboxBlock) {
                            if (which == Dialog.BUTTON_POSITIVE) {
                                result.confirm();
                            } else {
                                result.cancel();
                            }
                        }
                    }
                };
                new AlertDialog.Builder(getContext())
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, listener).show();
                return true;

            }

        }

        @Override
        public boolean onJsPrompt(WebView view, String url, final String message,
                                  String defaultValue, final JsPromptResult result) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                String prefix = "_wvjbxx";
                if (message.equals(prefix)) {
                    if (mainThreadHandler != null) {
                        Message msg = mainThreadHandler.obtainMessage(HANDLE_MESSAGE, defaultValue);
                        mainThreadHandler.sendMessage(msg);
                    }
                }
                return true;
            }
            if (!alertboxBlock) {
                result.confirm();
            }
            if (webChromeClient != null && webChromeClient.onJsPrompt(view, url, message, defaultValue, result)) {
                return true;
            } else {
                final EditText editText = new EditText(getContext());
                editText.setText(defaultValue);
                if (defaultValue != null) {
                    editText.setSelection(defaultValue.length());
                }
                float dpi = getContext().getResources().getDisplayMetrics().density;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertboxBlock) {
                            if (which == Dialog.BUTTON_POSITIVE) {
                                result.confirm(editText.getText().toString());
                            } else {
                                result.cancel();
                            }
                        }
                    }
                };
                new AlertDialog.Builder(getContext())
                        .setTitle(message)
                        .setView(editText)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, listener)
                        .show();
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int t = (int) (dpi * 16);
                layoutParams.setMargins(t, 0, t, 0);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                editText.setLayoutParams(layoutParams);
                int padding = (int) (15 * dpi);
                editText.setPadding(padding - (int) (5 * dpi), padding, padding, padding);
                return true;
            }

        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            if (webChromeClient != null) {
                return webChromeClient.onJsBeforeUnload(view, url, message, result);
            }
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
                                            long estimatedDatabaseSize,
                                            long totalQuota,
                                            WebStorage.QuotaUpdater quotaUpdater) {
            if (webChromeClient != null) {
                webChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater);
            } else {
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater);
            }
        }

        @Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            if (webChromeClient != null) {
                webChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            if (webChromeClient != null) {
                webChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
            } else {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            if (webChromeClient != null) {
                webChromeClient.onGeolocationPermissionsHidePrompt();
            } else {
                super.onGeolocationPermissionsHidePrompt();
            }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            if (webChromeClient != null) {
                webChromeClient.onPermissionRequest(request);
            } else {
                super.onPermissionRequest(request);
            }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            if (webChromeClient != null) {
                webChromeClient.onPermissionRequestCanceled(request);
            } else {
                super.onPermissionRequestCanceled(request);
            }
        }

        @Override
        public boolean onJsTimeout() {
            if (webChromeClient != null) {
                return webChromeClient.onJsTimeout();
            }
            return super.onJsTimeout();
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            if (webChromeClient != null) {
                webChromeClient.onConsoleMessage(message, lineNumber, sourceID);
            } else {
                super.onConsoleMessage(message, lineNumber, sourceID);
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (webChromeClient != null) {
                return webChromeClient.onConsoleMessage(consoleMessage);
            }
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public Bitmap getDefaultVideoPoster() {

            if (webChromeClient != null) {
                return webChromeClient.getDefaultVideoPoster();
            }
            return super.getDefaultVideoPoster();
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (webChromeClient != null) {
                return webChromeClient.getVideoLoadingProgressView();
            }
            return super.getVideoLoadingProgressView();
        }

        @Override
        public void getVisitedHistory(ValueCallback<String[]> callback) {
            if (webChromeClient != null) {
                webChromeClient.getVisitedHistory(callback);
            } else {
                super.getVisitedHistory(callback);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            if (webChromeClient != null) {
                return webChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
    };

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (webViewClient != null) {
                return webViewClient.shouldOverrideUrlLoading(view, url);
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (webViewClient != null) {
                webViewClient.onPageStarted(view, url, favicon);
            } else {
                super.onPageStarted(view, url, favicon);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (webViewClient != null) {
                webViewClient.onPageFinished(view, url);
            } else {
                super.onPageFinished(view, url);
            }

        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (webViewClient != null) {
                webViewClient.onLoadResource(view, url);
            } else {
                super.onLoadResource(view, url);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        public void onPageCommitVisible(WebView view, String url) {
            if (webViewClient != null) {
                webViewClient.onPageCommitVisible(view, url);
            } else {
                super.onPageCommitVisible(view, url);
            }
        }

        @Override
        @Deprecated
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (webViewClient != null) {
                return webViewClient.shouldInterceptRequest(view, url);
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (webViewClient != null) {
                return webViewClient.shouldInterceptRequest(view, request);
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }

        @Override
        @Deprecated
        public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
            if (webViewClient != null) {
                webViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
            } else {
                super.onTooManyRedirects(view, cancelMsg, continueMsg);
            }
        }

        @Override
        @Deprecated
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (webViewClient != null) {
                webViewClient.onReceivedError(view, errorCode, description, failingUrl);
            } else {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (webViewClient != null) {
                webViewClient.onReceivedError(view, request, error);
            } else {
                super.onReceivedError(view, request, error);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (webViewClient != null) {
                webViewClient.onReceivedHttpError(view, request, errorResponse);
            } else {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            if (webViewClient != null) {
                webViewClient.onFormResubmission(view, dontResend, resend);
            } else {
                super.onFormResubmission(view, dontResend, resend);
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            if (webViewClient != null) {
                webViewClient.doUpdateVisitedHistory(view, url, isReload);
            } else {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (webViewClient != null) {
                webViewClient.onReceivedSslError(view, handler, error);
            } else {
                super.onReceivedSslError(view, handler, error);
            }

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            if (webViewClient != null) {
                webViewClient.onReceivedClientCertRequest(view, request);
            } else {
                super.onReceivedClientCertRequest(view, request);
            }

        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            if (webViewClient != null) {
                webViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
            } else {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            if (webViewClient != null) {
                return webViewClient.shouldOverrideKeyEvent(view, event);
            } else {
                return super.shouldOverrideKeyEvent(view, event);
            }
        }

        @Override
        @Deprecated
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            if (webViewClient != null) {
                webViewClient.onUnhandledKeyEvent(view, event);
            } else {
                super.onUnhandledKeyEvent(view, event);
            }

        }

//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        public void onUnhandledInputEvent(WebView view, InputEvent event) {
//            if (webViewClient != null) {
//                webViewClient.onUnhandledInputEvent(view, event);
//            } else {
//                super.onUnhandledInputEvent(view, event);
//            }
//
//        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            if (webViewClient != null) {
                webViewClient.onScaleChanged(view, oldScale, newScale);
            } else {
                super.onScaleChanged(view, oldScale, newScale);
            }

        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            if (webViewClient != null) {
                webViewClient.onReceivedLoginRequest(view, realm, account, args);
            } else {
                super.onReceivedLoginRequest(view, realm, account, args);
            }
        }
    };

    public void clear() {
        if (mainThreadHandler != null) {
            mainThreadHandler.removeCallbacksAndMessages(null);
            mainThreadHandler = null;
        }
        javascriptCloseWindowListener = null;
    }

}
