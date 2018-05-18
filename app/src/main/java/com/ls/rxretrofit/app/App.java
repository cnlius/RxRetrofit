package com.ls.rxretrofit.app;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.ls.rxretrofit.BuildConfig;

/**
 * Created by liusong on 2018/1/5.
 */

public class App extends Application {
    public static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        if(isDebug()){
            initDebug();
        }
    }

    private void initDebug() {
        Stetho.initializeWithDefaults(this);
    }

    public Context getContext() {
        return mApp.getApplicationContext();
    }

    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

}
