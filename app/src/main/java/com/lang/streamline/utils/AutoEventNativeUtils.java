package com.lang.streamline.utils;

import android.graphics.Point;

public class AutoEventNativeUtils {
    static {
        System.loadLibrary("auto");
    }

    public static native int click(int x, int y);
    public static native int swipe(Point[] points);
    public static native float[] getPrecious();
    public static native int rootify();
}