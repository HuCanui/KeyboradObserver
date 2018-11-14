package com.hucanhui.android;

import android.app.Application;

/**
 * create at：2018/11/14 on 6:17 PM
 * des:
 * author:hucanhui
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KeyboardVisibilityObserver.getInstance().init(this);
    }
}
