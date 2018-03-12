package com.xdroid.errview;

import android.app.Application;

import com.xdroid.library.utils.AppContext;

public class XApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.setContext(getApplicationContext());
    }
}
