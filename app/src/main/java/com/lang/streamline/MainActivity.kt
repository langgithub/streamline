package com.lang.streamline

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.assist.AssistContent
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Surface
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.permission.FloatWindowManager
import com.lang.streamline.hhh.MotionEventModel
import com.lang.streamline.network.ClaimSecretRequest
import com.lang.streamline.network.ISecretApi
import com.lang.streamline.network.NewRetrofitCallback
import com.lang.streamline.network.RetrofitClient
import com.lang.streamline.ui.theme.TestUiAutomator2Theme
import com.lang.streamline.utils.DisplayUtils
import com.lang.streamline.utils.EditTextClass
import com.lang.streamline.utils.MotionEventAssetsCopier
import com.lang.streamline.utils.WindowManagerGlobalMirror
import org.json.JSONObject
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {
    private var floatView: FloatView? = null
    private var searchBar: EditText? = null
    private var excutePath: String? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.e("MyActivity", "xxx dispatchTouchEvent: " + ev.device.sources)
        Log.e("MyActivity", MotionEventModel(ev).toJSONObject().toString())
        return super.dispatchTouchEvent(ev)
    }
//    @SuppressLint("RestrictedApi")
//    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
//        recordKeyEvent(event);
//        val keyCode: Int = event.keyCode
//        Log.d("MyActivity", "dispatchKeyEvent: " + KeyEvent.keyCodeToString(keyCode))
//        return super.dispatchKeyEvent(event)
//    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val sb = StringBuilder()
        sb.append("KeyEvent Details:\n")
        sb.append("  Action: ").append(getActionString(event.action)).append("\n")
        sb.append("  KeyCode: ").append(KeyEvent.keyCodeToString(event.keyCode)).append("\n")
        sb.append("  ScanCode: ").append(event.scanCode).append("\n")
        sb.append("  MetaState: ").append(event.metaState).append("\n")
        sb.append("  Flags: 0x").append(Integer.toHexString(event.flags)).append("\n")
        sb.append("  Source: ").append(event.source).append("\n")
        sb.append("  DeviceId: ").append(event.deviceId).append("\n")
        sb.append("  EventTime: ").append(event.eventTime).append("\n")
        sb.append("  DownTime: ").append(event.downTime).append("\n")
        sb.append("  RepeatCount: ").append(event.repeatCount).append("\n")

        Log.d("MyActivity", sb.toString())
        return super.dispatchKeyEvent(event)
    }

    private fun getActionString(action: Int): String {
        return when (action) {
            KeyEvent.ACTION_DOWN -> "ACTION_DOWN"
            KeyEvent.ACTION_UP -> "ACTION_UP"
            KeyEvent.ACTION_MULTIPLE -> "ACTION_MULTIPLE"
            else -> "Unknown($action)"
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("MyActivity", "onKeyUp: " + KeyEvent.keyCodeToString(keyCode))
//        return super.onKeyUp(keyCode, event)
        return false
    }

    override fun onBackPressed() {
        Log.d("MyActivity", "onBackPressed")
        super.onBackPressed()
    }

    fun recordKeyEvent(ev:KeyEvent)  {
        var e = JSONObject()
        e.put("downTime",    ev.getDownTime());
        e.put("eventTime",   ev.getEventTime());
        e.put("action",      ev.getAction());
        e.put("keyCode",     ev.getKeyCode());
        e.put("repeatCount", ev.getRepeatCount());
        e.put("metaState",   ev.getMetaState());
        e.put("deviceId",    ev.getDeviceId());
        e.put("scanCode",    ev.getScanCode());
        e.put("flags",       ev.getFlags());
        e.put("source",      ev.getSource());
        Log.d("MyActivity", e.toString())
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestUiAutomator2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        onButtonClick = { showFloatView() },
                        onEditTextCreated = { et ->
                            searchBar = et
                        },
                        onAutoEdit = {
                            thread(start = true){
                                EditTextClass.autoInputEditTextExecute(searchBar, "输撒 发射 aasdfasfdafasfasf 中国三甲基计算机")
                                Log.i("TAG", "ok1")
                                thread(start = true){
                                    EditTextClass.autoInputEditTextExecute(searchBar, "cf a")
                                    Log.i("TAG", "ok")
                                    thread(start = true){
                                        EditTextClass.autoInputEditTextExecute(searchBar, "111 cf 点法发入 a 考虑是否就正式访问")
                                        Log.i("TAG", "ok")

                                    }
                                }
                            }
//                            val views = WindowManagerGlobalMirror.getWindowViews(
//                                WindowManagerGlobalMirror.getInstance(null)
//                            )
//
//                            EditTextClass().triggerBack(this)
                            Log.i("MyActivity", DisplayUtils.hasNavigationBar(this).toString())
                        },
                        onInputManagerClick = {
                            // 利用root 注入事件
//                            val clilck = applicationContext.filesDir.absolutePath+"/click.json"
//                            val cmd = "getprop zxcvbnm1 -c '${excutePath} $clilck'"
//                            Log.d("MyActivity","执行cmd:$cmd")
//                            Command.call(cmd)

                            //
//                            AccessibilityMSIPC().doCollect()
//                            var takeShot = "%s "
//                            Command.call(cmd)
//                            captureScreenshot(applicationContext)

//                            RetrofitClient.getInstance().createService(ISecretApi::class.java).queryPlugins(
//                                ClaimSecretRequest("123456", "123456")
//                            ).enqueue(object : NewRetrofitCallback<String?>() {
//                                    override fun onSuccess(result: String?) {
//                                        if (result != null) {
//                                            Log.d("MyActivity", result)
//                                        }
//                                    }
//
//                                    override fun onError(message: String?) {
//                                        super.onError(message)
//                                        if (message != null) {
//                                            Log.d("MyActivity", message)
//                                        }
//                                    }
//                                })
                        }
                    )
                }
            }
        }

        MotionEventAssetsCopier().copyAssetsToFiles(applicationContext)
        excutePath = applicationContext.filesDir.absolutePath+"/arm64-v8a/send_event"
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE);
    }

    private fun captureScreenshot(context: Context) {
        try {
            // 获取屏幕尺寸与方向
            val wm: WindowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
            val display: Display = wm.defaultDisplay
            val metrics: DisplayMetrics = DisplayMetrics()
            display.getRealMetrics(metrics)

            val width: Int = metrics.widthPixels
            val height: Int = metrics.heightPixels

            // 方向：0=竖屏，1=横屏等
            val rotation: Int = display.getRotation()
            val orientationDegrees = when (rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }

            val param = String.format(
                "-P %dx%d@%dx%d/%d",
                width, height, width, height, orientationDegrees
            )

            var cmd = String.format(
                "getprop zxcvbnm1 -c 'LD_LIBRARY_PATH=%s %s/minicap %s -s > /sdcard/Download/screen.jpg'",
                context.filesDir.absolutePath+"/arm64-v8a",
                context.filesDir.absolutePath+"/arm64-v8a",
                param
            )
            Log.d("Minicap", "cmd: ${cmd.toString()}")
            val process = Runtime.getRuntime().exec(cmd)
            val resultCode = process.waitFor()
            Log.d("Minicap", "Capture result: $resultCode")
        } catch (e: Exception) {
            Log.e("Minicap", "Failed to capture screenshot", e)
        }
    }

    /**
     * 检查是否有悬浮窗权限，然后初始化并显示 FloatView
     */
    private fun showFloatView() {
        val floatEnabled: Boolean = FloatWindowManager.getInstance()
            .checkFloatPermission(this)
        if (!floatEnabled) {
            Log.i("MainActivity", "float permission not checked")
            return
        }
        if (floatView == null) {
            floatView = FloatView(this)
        }
        floatView?.show()

        thread {
            isAccessibilityServiceActive()
        }
    }

    override fun onProvideAssistContent(outContent: AssistContent?) {
        // 会在系统准备截图/抓取内容时回调，例如 Google Assistant、screencap 等系统行为
        Log.i("MainActivity", "监听截图回调")
    }

    fun isAccessibilityServiceActive(): Boolean {
        val am: AccessibilityManager = applicationContext.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val services: List<AccessibilityServiceInfo> = am.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_GENERIC
        )
        Log.i("MainActivity", "无障碍状态："+am.isEnabled.toString())
        for (info in services) {
            // UIAutomator 的服务包名通常以 "uiautomator" 或 "com.android.uiautomator" 开头
            Log.i("MainActivity", "services>"+info.getResolveInfo().serviceInfo.packageName)
        }
        return false
    }

    private fun autoEditText() {
        // 假设 searchBar 已经是你的 EditText 实例
        searchBar!!.requestFocus()
        val imm = searchBar!!.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)

        val ei = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        }
        val ic: InputConnection = searchBar!!.onCreateInputConnection(ei)

        val keywords = arrayOf("你好", "事件", "输撒发射点法发入", "中", "阿萨")
        val handler = Handler(Looper.getMainLooper())
        val charDelay = 1000L   // 每个字符的“打字时间”
        val spaceDelay = 500L   // 词与词之间的空格延迟
        var cumulativeDelay = 200L   // 开始前先等 200ms

        for ((index, word) in keywords.withIndex()) {
            // 累加：打完这个词需要 word.length * charDelay
            cumulativeDelay += word.length * charDelay
            // 1) 打词
            handler.postDelayed({
                ic.commitText(word, 1)
            }, cumulativeDelay)

            // 2) 如果不是最后一个词，再打个空格
            if (index < keywords.size - 1) {
                cumulativeDelay += spaceDelay
                handler.postDelayed({
                    ic.commitText(" ", 1)
                }, cumulativeDelay)
            }
        }
    }
}
@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    onEditTextCreated: (EditText) -> Unit,
    onAutoEdit: () -> Unit,
    onInputManagerClick: () -> Unit,
) {
    // 用 Column 安排文本和按钮
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Hello $name!")
        Button(
            onClick = { onButtonClick() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "测试打开悬浮窗")
        }
        AndroidView(
            factory = { ctx ->
                // 在这里 new 出一个 EditText
                EditText(ctx).apply {
                    hint = "请输入内容"
                    inputType = InputType.TYPE_CLASS_TEXT
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                            Log.d("TAG", "\nbeforeTextChanged text = ${s.toString()}")
                        }
                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                            Log.d("TAG", "onTextChanged text = ${s.toString()}")
                        }
                        override fun afterTextChanged(s: Editable) {
                            // 这里只关心改完之后的文本
                            Log.d("TAG", "afterTextChanged text = ${s.toString()}")
                        }
                    })
                    onEditTextCreated(this)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        )
        Button(
            onClick = { onAutoEdit() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "测试自动化EditText")
        }
        Button(
            onClick = { onInputManagerClick() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "测试点击365，325")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestUiAutomator2Theme {
        Greeting(name = "Android", onButtonClick = {}, onEditTextCreated = {}, onAutoEdit={}, onInputManagerClick={})
    }
}