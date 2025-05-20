package com.github.streamline.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class ToastHelper {

    private static final String TAG = "ToastHelper";

    private WindowManager windowManager;
    private View toastView;
    private WindowManager.LayoutParams params;
    private int duration;
    private Timer timer;

    @SuppressLint("InlinedApi")
    private ToastHelper(Context context, String text, int duration) {
        this.duration = duration;
        timer = new Timer();
//        @SuppressLint("ShowToast") Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
//        toastView = toast.getView();
        toastView = createCustomToastView(context, text);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = Animation.INFINITE;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.verticalMargin = 0.2f;
        params.alpha = 0.8f;
        params.y = -30;
    }


    public static ToastHelper makeText(Context context, String text, int duration) {
        return new ToastHelper(context, text, duration);
    }

    public void show() {
        Log.d("UIAutomatorStub", "toastView:"+(toastView==null));
        windowManager.addView(toastView, params);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                windowManager.removeView(toastView);
            }
        }, duration);
    }

    public void cancel() {
        windowManager.removeView(toastView);
        timer.cancel();
    }


    /**
     * 动态生成一个自定义 Toast 布局视图
     *
     * @param context 应用或 Activity 的上下文
     * @param message 要显示的消息
     * @return 构造好的 View 对象
     */
    public static View createCustomToastView(Context context, String message) {
        // 创建一个 LinearLayout 作为容器
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        // 设置布局参数为包裹内容
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        // 设置内边距（单位：像素，根据需要调整）
        int padding = dpToPx(context, 16);
        layout.setPadding(padding, padding, padding, padding);
        // 设置背景颜色（半透明黑色）
        layout.setBackgroundColor(Color.argb(200, 0, 0, 0));
        // 设置布局居中显示
        layout.setGravity(Gravity.CENTER);

        // 创建一个 TextView 来显示消息
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(message);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);

        // 将 TextView 添加到 LinearLayout
        layout.addView(textView);
        return layout;
    }

    /**
     * 将 dp 转换为 px（像素），便于设置控件大小或内边距
     */
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}