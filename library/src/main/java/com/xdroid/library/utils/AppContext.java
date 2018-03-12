package com.xdroid.library.utils;

import android.content.Context;

public class AppContext {

    private static Context mContext;

    public static void setContext(Context mContext) {
        AppContext.mContext = mContext;
    }

    public static Context getContext() {
        return mContext;
    }
}
