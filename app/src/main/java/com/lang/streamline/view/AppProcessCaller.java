package com.lang.streamline.view;

import android.util.Log;

import com.lang.streamline.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * 调用一个主函数入口，一般的沙箱只会hook zygote。所以我们直接使用app process可以绕过zygote
 */
public class AppProcessCaller {
//    private static final String CMD_FORMATTER =
//            "(CLASSPATH=%s /system/bin/app_process /system/bin --nice-name=%s "
//                    + "%s %s)&";
    private static final String CMD_FORMATTER =
            "CLASSPATH=%s /system/bin/app_process /system/bin --nice-name=%s %s %s";

    public static String call(Class<?> mainClass, List<String> params) throws IOException, InterruptedException {
        String sourceDir = App.app.getApplicationInfo().sourceDir;
        StringBuilder paramList = new StringBuilder();
        for (String str : params) {
            paramList.append(str).append(" ");
        }
        String cmd = String.format(Locale.ENGLISH, CMD_FORMATTER, sourceDir, "AppProcessCaller-" + System.currentTimeMillis(),
                mainClass.getName(), paramList);

        Log.i(App.TAG, "cmd: " + cmd);
        Process process = Runtime.getRuntime().exec("sh");
        OutputStream os = process.getOutputStream();
        os.write(cmd.getBytes());
        os.write("\nexit".getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        StreamReadTask streamReadTaskIn = new StreamReadTask(process.getInputStream());
        streamReadTaskIn.start();
        StreamReadTask streamReadTaskError = new StreamReadTask(process.getErrorStream());
        streamReadTaskError.start();
        process.waitFor();


        String response = streamReadTaskIn.waitAndGetData() + "\n" + streamReadTaskError.waitAndGetData();
        Log.i(App.TAG, "result: " + response);
        return response;
    }


    public static String callByRoot(Class<?> mainClass, List<String> params) throws IOException, InterruptedException {
        String sourceDir = App.app.getApplicationInfo().sourceDir;
        StringBuilder paramList = new StringBuilder();
        for (String str : params) {
            paramList.append(str).append(" ");
        }
        String cmd = String.format(Locale.ENGLISH, CMD_FORMATTER, sourceDir, "AppProcessCaller-" + System.currentTimeMillis(),
                mainClass.getName(), paramList);
        Log.i(App.TAG, "cmd: " + cmd);
        Process process = Runtime.getRuntime().exec("sh");
        OutputStream os = process.getOutputStream();
        os.write(cmd.getBytes());
        os.write("\nexit".getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        StreamReadTask streamReadTaskIn = new StreamReadTask(process.getInputStream());
        streamReadTaskIn.start();
        StreamReadTask streamReadTaskError = new StreamReadTask(process.getErrorStream());
        streamReadTaskError.start();
        process.waitFor();


        String response = streamReadTaskIn.waitAndGetData() + "\n" + streamReadTaskError.waitAndGetData();

        Log.i(App.TAG, "result: " + response);
        return response;
    }


    private static class StreamReadTask extends Thread {
        private final InputStream inputStream;
        private String data = null;
        private final Object lock = new Object();

        StreamReadTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        String waitAndGetData() throws InterruptedException {
            if (data != null) {
                return data;
            }
            synchronized (lock) {
                if (data != null) {
                    return data;
                }
                lock.wait();
            }
            return data;
        }


        @Override
        public void run() {
            String line;
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }

            } catch (IOException e) {
                Log.e(App.TAG, "error", e);
            } finally {
                if(inputStream!=null) {
                    try { inputStream.close(); } catch (IOException ignored) {}
                }
                data = sb.toString();
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }
    }
}
