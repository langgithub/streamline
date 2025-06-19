package com.lang.streamline.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.icu.text.Transliterator;
import android.os.Build;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class EditTextClass {

    private static String tag = "EditTextClass";
    @SuppressLint("NewApi")
    public static void autoInputEditTextExecute(EditText searchBar, String keywordsStr) {
        try{
            // 先在主线程里把焦点、键盘弹出来
            searchBar.post(() -> {
                searchBar.requestFocus();
                InputMethodManager imm =
                        (InputMethodManager)searchBar.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
            });

            // 删除内容
            CountDownLatch deleteLatch = new CountDownLatch(1);
            new Thread(() -> {
                int len = searchBar.getText().length();
                while (len != 0){
                    for (int i = 0; i < len; i++) {
                        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                        searchBar.post(() -> {
                            InputConnection ic = createIc(searchBar);
                            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,   KeyEvent.KEYCODE_DEL));
                        });
                    }
                    len = searchBar.getText().length();
                }
                deleteLatch.countDown();
            }).start();
            deleteLatch.await();

            // 搜索内容输入
            String[] words = keywordsStr.split("\\s+");
            String[] pinyins = hanziToPinyinByIcu(words);
            final long charDelay  = 500;  // 每个字符间隔 ms
            final long spaceDelay = 200;  // 词间空格间隔 ms
            CountDownLatch inputLatch = new CountDownLatch(1);
            new Thread(() -> {
                InputConnection ic = createIc(searchBar);
                for (int wi = 0; wi < words.length; wi++) {
                    String word = words[wi];
                    String pinyin = pinyins[wi];
                    Log.d(tag, pinyin);
                    // 输入拼音
                    for (int i = 1; i <= Math.min(pinyin.length(), 28); i++) {
                        final String sub = pinyin.substring(0, i);
                        searchBar.post(() -> ic.setComposingText(sub, 1));
                        try { Thread.sleep(charDelay); } catch (InterruptedException ignored) {}
                    }
                    Log.d(tag, word);
                    // 选择内容
                    searchBar.post(() -> {
                        ic.commitText(word, 1);
                        ic.finishComposingText();
                    });

                    // 模拟空格执行时间
                    try { Thread.sleep(spaceDelay);} catch (InterruptedException ignored) {}
                    if (wi < words.length - 1) searchBar.post(() -> ic.commitText(" ", 1));
                }
                inputLatch.countDown();
            }).start();
            inputLatch.await();
        } catch (Exception ignored) {}
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String[] hanziToPinyinByIcu(String[] hanzi) {
        // Han-Latin：汉字→带声调拼音；Latin-ASCII：去掉声调符号
        Transliterator trans = Transliterator.getInstance("Han-Latin; Latin-ASCII");
        String[] result = new String[hanzi.length];
        for (int i = 0; i < hanzi.length; i++) {
            String chinese = hanzi[i];
            // 转换并合并多余空白
            String pinyin = trans
                    .transliterate(chinese)
                    .replaceAll("\\s+", " ")
                    .trim();
            result[i] = pinyin;
        }
        return result;
    }

    /** Helper：每次 commitText 前重新取一次 InputConnection */
    private static InputConnection createIc(EditText editText) {
        EditorInfo ei = new EditorInfo();
        ei.inputType  = InputType.TYPE_CLASS_TEXT;
        ei.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        InputConnection baseIc = editText.onCreateInputConnection(ei);
        if (baseIc == null) return null;
        return new InputConnectionWrapper(baseIc, true) {
            @Override
            public boolean setComposingText(CharSequence text, int newCursorPosition) {
//                boolean result = super.setComposingText(text, newCursorPosition);
                Log.d(tag, "setComposingText=>"+text);
                return true;
            }

            @Override
            public boolean commitText(CharSequence text, int newCursorPosition) {
                // 同样什么也不做
                boolean result = super.commitText(text, newCursorPosition);
                Log.d(tag, result+"commitText=>"+text);
                return result;
            }

            @Override
            public boolean finishComposingText() {
                boolean result = super.finishComposingText();
                Log.d(tag, result+"finishComposingText->");
                return result;
            }
        };
    }

    /**
     * @param activity 分发事件页面
     * @param action down or up
     * @param code keycode
     */
    private void sendKey(Activity activity, int action, int code, long downTime, long eventTime){
        // 构造一个 BACK 键的按下/抬起事件
        KeyEvent ev = new KeyEvent(
                downTime, eventTime, action, code, 0,  /* repeat */
                0,  /* metaState */KeyCharacterMap.VIRTUAL_KEYBOARD, 0,  /* scancode */
                KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);
        activity.getWindow().getDecorView().dispatchKeyEvent(ev);
    }

    /**
     * 模拟 system ui 滑动返回
     * @param activity 需要返回页面
     */
    public void triggerBack(Activity activity){
        long downTime = SystemClock.uptimeMillis();
        sendKey(activity, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, downTime, downTime);
        try{ Thread.sleep(1+new Random().nextInt(2)); }catch (Exception ignored){}
        long eventTime = SystemClock.uptimeMillis();
        sendKey(activity, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, downTime, eventTime);
    }
}
