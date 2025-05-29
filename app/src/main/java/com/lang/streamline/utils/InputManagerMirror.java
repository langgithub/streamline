package com.lang.streamline.utils;

import android.hardware.input.InputManager;
import android.view.InputEvent;
import android.view.MotionEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InputManagerMirror {

    private static final Class<?> clazz;

    private static final Method getInstanceMethod;

    private static final Method injectInputEventMethod;

    private static final Object inputManagerMirrorObject;

    static {
        clazz = InputManager.class;
        try {
            getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            injectInputEventMethod = clazz.getDeclaredMethod("injectInputEvent", InputEvent.class, int.class);
            inputManagerMirrorObject = InputManagerMirror.getInstance(null);
        } catch (NoSuchMethodException e) {
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

    public static Boolean injectInputEvent(Object self, InputEvent event, int mode) {
        try {
            return (boolean) injectInputEventMethod.invoke(self, event, mode);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public  boolean injectInputEvent(InputEvent event, int mode) {
        return InputManagerMirror.injectInputEvent(inputManagerMirrorObject, event, mode);
    }
}
