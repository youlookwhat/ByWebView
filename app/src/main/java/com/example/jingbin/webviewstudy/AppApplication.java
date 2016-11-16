package com.example.jingbin.webviewstudy;

import android.app.Application;

/**
 * Created by jingbin on 2016/11/16.
 */

public class AppApplication extends Application{

    private static AppApplication application;

    public static AppApplication getInstance() {
        if (application == null) {
            synchronized (AppApplication.class) {
                if (application == null) {
                    application = new AppApplication();
                }
            }
        }
        return application;
    }

    @SuppressWarnings({"unused"})
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

}
