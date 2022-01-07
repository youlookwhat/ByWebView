package com.example.jingbin.webviewstudy;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @author jingbin
 * @data 2018/2/2
 */

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        app = this;
        initX5();
    }

    public static App getInstance() {
        return app;
    }

    private void initX5() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    /**
     * 方法数超64k 解决 https://developer.android.com/studio/build/multidex?hl=zh-cn
     * 继承 MultiDexApplication 或 实现此方法。
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initWebView();
        MultiDex.install(this);
    }

    /**
     * Android P针对 WebView在不同进程下无法访问非自己进程中的webview目录
     * fix Using WebView from more than one process at once with the same data directory is not supported
     */
    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            String packageName = this.getPackageName();
            if (!packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }
}
