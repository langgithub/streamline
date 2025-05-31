package com.lang.streamline;

import android.app.Application;
import android.content.Context;

import com.lang.streamline.utils.SysUtils;


public class App extends Application {
    public static App app;
    public static String TAG = "ENV_CHECK";
    public static String TAG_MSG = "PRINT_CHECK";

    public App() {
        App.app = this;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        SysUtils.allowHiddenApis();
    }
}
