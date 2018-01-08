package com.ls.rxretrofit.app;

import android.app.Application;

import com.ls.rxretrofit.BuildConfig;

/**
 * Created by liusong on 2018/1/5.
 */

public class App extends Application {
    private static boolean isDebug;
    private static Application mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        isDebug = BuildConfig.DEBUG;
        mApp=this;
    }

    public static Application getApp() {
        return mApp;
    }

    public boolean isDebug(){
        return isDebug;
    }

}
