package com.lang.streamline.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Property;
import android.view.WindowManager;
import android.view.WindowMetrics;

import java.lang.reflect.Method;

public class SysUtils {

    private static final String TAG = "SysUtils";

    private static boolean sAllowHiddenApisDone;

    private static Application sApplication;

    public static Application currentApplication() {
        Application application = sApplication;
        if (application == null) {
            try {
                application = sApplication = (Application) getSysMethod(Class.forName("android.app.ActivityThread"), "currentApplication").invoke(null, new Object[0]);
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
        return application;
    }

    public static void setApplication(Application application) {
        sApplication = application;
    }

    public static Method getSysMethod(Class<?> objClass, String methodName) {
        try {
            Method[] methods = (Method[]) Class.class.getDeclaredMethod("getDeclaredMethods", new Class[0]).invoke(objClass, new Object[0]);
            if (methods == null) {
                Log.e(TAG, "fail to get declared methods");
                return null;
            }
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    method.setAccessible(true);
                    return method;
                }
            }
            Log.e(TAG, "fail to get sys method " + methodName);
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public static void allowHiddenApis() {
        if (Build.VERSION.SDK_INT < 28 || sAllowHiddenApisDone) return;
        try {
            Method[] methods = Property.of(Class.class, Method[].class, "Methods").get(Class.forName("dalvik.system.VMRuntime"));
            Method setHiddenApiExemptions = null;
            Method getRuntime = null;
            for (Method method : methods) {
                if ("setHiddenApiExemptions".equals(method.getName())) {
                    setHiddenApiExemptions = method;
                }
                if ("getRuntime".equals(method.getName())) {
                    getRuntime = method;
                }
            }
            setHiddenApiExemptions.invoke(getRuntime.invoke(null), new Object[]{new String[]{"L"}});
            Log.i(TAG, "allowHiddenApis");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        sAllowHiddenApisDone = true;
    }

    public static ClassLoader getClassLoader() {
        return currentApplication().getClassLoader();
    }

    public static String getSystemProperty(String property) {
        try {
            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
            return ReflectUtils.invokeStaticMethod(SystemProperties, "get", property);
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public static int[] getScreenSize() {
        int[] size = new int[2];
        WindowManager wm = (WindowManager) currentApplication().getSystemService(Context.WINDOW_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics currentWindowMetrics = wm.getCurrentWindowMetrics();
            size[0] = currentWindowMetrics.getBounds().width();
            size[1] = currentWindowMetrics.getBounds().height();
            Log.i(TAG, "screen width:" + size[0] + "   screen height:" + size[1]);
        } else {
            size[0] = wm.getDefaultDisplay().getWidth();
            size[1] = wm.getDefaultDisplay().getHeight();
            Log.i(TAG, "screen width:" + size[0] + "   screen height:" + size[1]);
        }
        return size;
    }

    public static String getMarketName() {
        String propName = null;
        switch (Build.MANUFACTURER) {
            case "vivo":
                propName = "ro.vivo.market.name";
                break;
            case "Xiaomi":
                propName = "ro.product.marketname";
                break;
        }
        if (TextUtils.isEmpty(propName)) {
            Log.e(TAG, "not find propName");
            return null;
        }
        return getProps(propName, "");
    }

    public static String getProps(String key, String defValue) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getMethod("get", String.class, String.class);
            String value = (String) method.invoke(null, key, defValue);
            return value;
        } catch (Exception e) {
        }
        return defValue;
    }
}
