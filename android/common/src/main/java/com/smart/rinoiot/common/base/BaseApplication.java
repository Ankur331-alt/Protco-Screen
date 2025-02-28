package com.smart.rinoiot.common.base;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

public abstract class BaseApplication extends MultiDexApplication {
    private static Application application;

    public static Application getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public abstract void init();
}
