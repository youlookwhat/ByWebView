package com.example.jingbin.webviewstudy;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

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
    }

    public static App getInstance() {
        return app;
    }
}
