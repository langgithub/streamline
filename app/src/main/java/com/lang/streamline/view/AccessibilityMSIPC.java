package com.lang.streamline.view;


import android.annotation.SuppressLint;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import com.lang.streamline.App;
import com.lang.streamline.utils.Command;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
public class AccessibilityMSIPC  {

    private static final String CMD_FORMATTER =
            "CLASSPATH=%s /system/bin/app_process /system/bin --nice-name=%s %s %s";

    public static String callByRoot(Class<?> mainClass, List<String> params) throws IOException, InterruptedException {
        String sourceDir = App.app.getApplicationInfo().sourceDir;
        StringBuilder paramList = new StringBuilder();
        for (String str : params) {
            paramList.append(str).append(" ");
        }
        return String.format(Locale.ENGLISH, CMD_FORMATTER, sourceDir,
                "AppProcessCaller-" + System.currentTimeMillis(),
                mainClass.getName(), paramList);
    }
    public String doCollect() throws Exception {
        String ret = callByRoot(AccessibilityMSIPC.class, Collections.singletonList(App.app.getPackageName()));
        Log.i(App.TAG, ret);
        Command.callNormal(ret);
        return ret;
//        return AppProcessCaller.callByRoot(AccessibilityMSIPC.class, Collections.singletonList(App.app.getPackageName()));
    }

    public static void main(String[] args) {
        try {
            // frameworks/base/services/accessibility/java/com/android/server/accessibility/AccessibilityManagerService.java
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClass.getDeclaredMethod("getService", String.class);
            getService.setAccessible(true);
            IBinder binder = (IBinder) getService.invoke(null, "accessibility");
            assert binder != null;
            Log.i(App.TAG, String.valueOf("binder="+binder.getClass().getName()));

            Class<?> stub = Class.forName("android.view.accessibility.IAccessibilityManager$Stub");
            Method asInterface = stub.getDeclaredMethod("asInterface", IBinder.class);
            asInterface.setAccessible(true);
            Object ams_proxy = asInterface.invoke(null, binder);
            // AccessibilityManagerService 继承 IAccessibilityManager.Stub，所以可能直接是 proxy
            Log.i(App.TAG, String.valueOf(ams_proxy==null));


//            Field mLockField = ams_proxy.getClass().getDeclaredField("mLock");
//            mLockField.setAccessible(true);
//            Object mLock = mLockField.get(ams_proxy);

            // 获取全局 Map（跨用户 App 注册的）
            assert ams_proxy != null;
            Field gconnField = ams_proxy.getClass().getDeclaredField("mGlobalInteractionConnections");
            gconnField.setAccessible(true);
            SparseArray<?> globalMap = (SparseArray<?>) gconnField.get(ams_proxy);

            for (int i = 0; i < Objects.requireNonNull(globalMap).size(); i++) {
                int windowId = globalMap.keyAt(i);
                Object wrapper = globalMap.valueAt(i);

                Field connField = wrapper.getClass().getDeclaredField("mConnection");
                connField.setAccessible(true);
                Object conn = connField.get(wrapper);

                // 你现在拿到了目标 app 的 binder proxy，可以调用它了
                Log.d("IPC", "windowId: " + windowId + ", conn: " + conn);
            }
        } catch (Throwable e) {
            Log.e(App.TAG, "error", e);
            e.printStackTrace();
        }
    }
}
