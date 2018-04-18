package com.example.jingbin.webviewstudy;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author jingbin
 * @data 2018/2/2
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
