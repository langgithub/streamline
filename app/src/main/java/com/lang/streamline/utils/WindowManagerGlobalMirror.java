package com.lang.streamline.utils;

import android.view.View;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class WindowManagerGlobalMirror {

    private static final Class<?> clazz;

    private static final Method getInstanceMethod;

    private static final Method getWindowViewsMethod;

    static {
        try {
            clazz = ReflectUtils2.getClass("android.view.WindowManagerGlobal");
            getInstanceMethod = ReflectUtils2.getDeclaredMethod(clazz, "getInstance");
            getWindowViewsMethod = ReflectUtils2.getDeclaredMethod(clazz, "getWindowViews");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static Object getInstance(Object self) {
        try {
            return getInstanceMethod.invoke(self);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<View> getWindowViews(Object self) {
        try {
            return (List<View>) getWindowViewsMethod.invoke(self);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
