package com.ls.rxretrofit.app;

import android.app.Application;
import android.content.Context;

import com.ls.rxretrofit.BuildConfig;

/**
 * Created by liusong on 2018/1/5.
 */

public class App extends Application {
    private boolean isDebug;
    public static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        isDebug = BuildConfig.DEBUG;
    }

    public Context getContext() {
        return mApp.getApplicationContext();
    }

    public boolean isDebug() {
        return isDebug;
    }

}
