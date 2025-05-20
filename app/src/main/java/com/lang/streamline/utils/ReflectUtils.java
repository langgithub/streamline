package com.lang.streamline.utils;

import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;


public class ReflectUtils {

    private static final String TAG = "ReflectUtils";

    public static <T> T newInstance(String className) {
        try {
            return (T) ClassUtils.getClass(className).newInstance();
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public static <T> T getStaticFieldValue(Class<?> fieldCls, String fieldName) {
        Field field = getStaticField(fieldCls, fieldName);
        if (field == null) {
            try {
                return (T) field.get(null);
            } catch (IllegalAccessException e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
        return null;
    }

    public static <T> void setStaticFieldValue(Class<?> fieldCls, String fieldName, T value) {
        Field field = getStaticField(fieldCls, fieldName);
        if (field == null) {
            try {
                field.set(null, value);
            } catch (IllegalAccessException e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
    }

    public static Field getStaticField(Class<?> fieldCls, String fieldName) {
        Field field = getStaticDeclaredField(fieldCls, fieldName);
        if (field == null) {
            field = doGetStaticField(fieldCls, fieldName);
        }
        return field;
    }

    private static Field getStaticDeclaredField(Class<?> fieldCls, String fieldName) {
        if (fieldCls == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        try {
            return FieldUtils.getDeclaredField(fieldCls, fieldName, true);
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    private static Field doGetStaticField(Class<?> fieldCls, String fieldName) {
        if (fieldCls == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        try {
            return FieldUtils.getField(fieldCls, fieldName, true);
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public static <T> T getObjectFieldValue(Object obj, String fieldName) {
        Field field = getObjectField(obj, fieldName);
        if (field != null) {
            try {
                return (T) field.get(obj);
            } catch (IllegalAccessException e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
        return null;
    }

    public static Field getObjectField(Object obj, String fieldName) {
        Field field = getDeclaredField(obj, fieldName);
        if (field == null) {
            field = doGetObjectField(obj, fieldName);
        }
        return field;
    }

    public static <T> void setObjectFieldValue(Object obj, String fieldName, T value) {
        Field field = getObjectField(obj, fieldName);
        if (field != null) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
    }

    private static Field getDeclaredField(Object obj, String fieldName) {
        if (obj == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        try {
            return FieldUtils.getDeclaredField(obj.getClass(), fieldName, true);
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    private static Field doGetObjectField(Object obj, String fieldName) {
        if (obj == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        try {
            return FieldUtils.getField(obj.getClass(), fieldName, true);
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public static <T> T invokeStaticMethod(Class<?> cls, String methodName, Object... args) {
        try {
            return invokeStaticMethodThrow(cls, methodName, args);
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public static <T> T invokeStaticMethodThrow(Class<?> cls, String methodName, Object... args) throws Exception {
        if (cls == null || TextUtils.isEmpty(methodName)) {
            return null;
        }
        return (T) MethodUtils.invokeStaticMethod(cls, methodName, args);
    }

    public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
        if (obj == null || TextUtils.isEmpty(methodName)) {
            return null;
        }
        try {
            return (T) MethodUtils.invokeMethod(obj, methodName, args);
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public static <T> T invokeStaticMethod(Class<?> cls, String methodName, Object[] args, Class<?>[] parameterTypes) {
        if (cls == null || TextUtils.isEmpty(methodName)) {
            return null;
        }
        try {
            return (T) MethodUtils.invokeStaticMethod(cls, methodName, args, parameterTypes);
        } catch (Throwable e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }
}

