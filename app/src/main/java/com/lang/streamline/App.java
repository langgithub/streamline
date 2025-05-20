package com.lang.streamline;

import android.app.Application;
import android.content.Context;

import com.lang.streamline.utils.SysUtils;


public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SysUtils.allowHiddenApis();
    }
}
