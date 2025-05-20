
package com.lang.streamline.utils;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ReflectUtils2 {

    private static final Method forNameMethod;

    private static final Method getMethodMethod;

    private static final Method getDeclaredMethodMethod;

    private static final Method getDeclaredFieldMethod;

    static {
        try {
            forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            getMethodMethod = Class.class.getDeclaredMethod("getMethod", String.class, Class[].class);
            getDeclaredMethodMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            getDeclaredFieldMethod = Class.class.getDeclaredMethod("getDeclaredField", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        try {
            return (Class<?>) Objects.requireNonNull(forNameMethod.invoke(null, className));
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof ClassNotFoundException) {
                throw (ClassNotFoundException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        if (!(object instanceof Class)) {
            object = object.getClass();
        }
        try {
            return (Method) getDeclaredMethodMethod.invoke(object, methodName, parameterTypes);
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof NoSuchMethodException) {
                throw (NoSuchMethodException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Method getMethod(Object object, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        if (!(object instanceof Class)) {
            object = object.getClass();
        }
        try {
            return (Method) Objects.requireNonNull(getMethodMethod.invoke(object, methodName, parameterTypes));
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof NoSuchMethodException) {
                throw (NoSuchMethodException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static Field getDeclaredField(Object object, String fieldName) throws NoSuchFieldException {
        if (!(object instanceof Class)) {
            object = object.getClass();
        }
        try {
            return (Field) Objects.requireNonNull(getDeclaredFieldMethod.invoke(object, fieldName));
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof NoSuchFieldException) {
                throw (NoSuchFieldException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

}
