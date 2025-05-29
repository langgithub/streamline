package com.lang.streamline.utils;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.PathInterpolator;

import java.util.Random;

public class SimpleInteractionController {

    private static final long CLICK_LENGTH = 70;

    private static final long PRESS_LENGTH = 350;

    private static final long STEP_LENGTH = 16;

    private final Random random = new Random();

    private long touchDownTime;

    private final InputManagerMirror adapter;

    public SimpleInteractionController(InputManagerMirror adapter) {
        this.adapter = adapter;
    }

    public boolean click(float x, float y) {
        x += random.nextFloat();
        y += random.nextFloat();

        if (touchDown(x, y)) {
            SystemClock.sleep(CLICK_LENGTH);
            if (touchUp(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean swipe(float downX, float downY, float upX, float upY, long duration) {
        downX += random.nextFloat();
        downY += random.nextFloat();
        upX += random.nextFloat();
        upY += random.nextFloat();

        int steps = (int) (duration / STEP_LENGTH);

        boolean ret;
        float xStep;
        float yStep;

        if (steps == 0)
            steps = 1;

        xStep = ((float) (upX - downX)) / steps;
        yStep = ((float) (upY - downY)) / steps;

        float xError = Math.max(Math.abs(upX - downX) / 4, 200f);
        float yError = Math.max(Math.abs(upY - downY) / 4, 200f);

        PathInterpolator tNoise = new PathInterpolator(random.nextFloat() / 5 + 0.2f, 0, random.nextFloat() / 5 + 0.6f, 1);
        PathInterpolator xNoise = new PathInterpolator(random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.7f, random.nextFloat() / 5 + 0.7f);
        PathInterpolator yNoise = new PathInterpolator(random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.7f, random.nextFloat() / 5 + 0.7f);

        ret = touchDown(downX, downY);
        SystemClock.sleep(STEP_LENGTH);
        for (int i = 1; i < steps; i++) {
            float offset = (float) i / steps;
            offset = tNoise.getInterpolation(offset);

            ret &= touchMove(
                    downX + (upX - downX) * xNoise.getInterpolation(offset) + (xNoise.getInterpolation(offset) - offset) * xError,
                    downY + (upY - downY) * yNoise.getInterpolation(offset) + (yNoise.getInterpolation(offset) - offset) * yError
            );

            if (!ret)
                break;
            SystemClock.sleep(STEP_LENGTH);
        }
        ret &= touchUp(upX, upY);

        return ret;
    }

    public boolean drag(float downX, float downY, float upX, float upY, long duration) {
        downX += random.nextFloat();
        downY += random.nextFloat();
        upX += random.nextFloat();
        upY += random.nextFloat();

        int steps = (int) (duration / STEP_LENGTH);

        boolean ret;
        float xStep;
        float yStep;

        if (steps == 0)
            steps = 1;

        xStep = ((float) (upX - downX)) / steps;
        yStep = ((float) (upY - downY)) / steps;

        float xError = Math.max(Math.abs(upX - downX) / 4, 200f);
        float yError = Math.max(Math.abs(upY - downY) / 4, 200f);

        PathInterpolator tNoise = new PathInterpolator(random.nextFloat() / 5 + 0.2f, 0, random.nextFloat() / 5 + 0.6f, 1);
        PathInterpolator xNoise = new PathInterpolator(random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.7f, random.nextFloat() / 5 + 0.7f);
        PathInterpolator yNoise = new PathInterpolator(random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.1f, random.nextFloat() / 5 + 0.7f, random.nextFloat() / 5 + 0.7f);

        ret = touchDown(downX, downY);
        SystemClock.sleep(STEP_LENGTH);
        for (int i = 1; i < steps; i++) {
            float offset = (float) i / steps;
            offset = tNoise.getInterpolation(offset);

            ret &= touchMove(
                    downX + (upX - downX) * xNoise.getInterpolation(offset) + (xNoise.getInterpolation(offset) - offset) * xError,
                    downY + (upY - downY) * yNoise.getInterpolation(offset) + (yNoise.getInterpolation(offset) - offset) * yError
            );
            if (!ret)
                break;
            SystemClock.sleep(STEP_LENGTH);
        }
        SystemClock.sleep(PRESS_LENGTH / 2);
        ret &= touchUp(upX, upY);

        return ret;
    }

    private boolean touchDown(float x, float y) {
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[]{new MotionEvent.PointerProperties()};
        properties[0].id = 0;
        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[]{new MotionEvent.PointerCoords()};
        coords[0].orientation = 0;
        coords[0].pressure = 1.0f;
        coords[0].size = .0f;
        coords[0].x = Math.round(x);
        coords[0].y = Math.round(y);

        touchDownTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(
                touchDownTime, touchDownTime, MotionEvent.ACTION_DOWN,
                1, properties, coords,
                0, 0, 1.0f, 1.0f,
                0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);

        return injectEventSync(event);
    }

    private boolean touchUp(float x, float y) {
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[]{new MotionEvent.PointerProperties()};
        properties[0].id = 0;
        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[]{new MotionEvent.PointerCoords()};
        coords[0].orientation = 0;
        coords[0].pressure = 1.0f;
        coords[0].size = .0f;
        coords[0].x = Math.round(x);
        coords[0].y = Math.round(y);

        final long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(
                touchDownTime, eventTime, MotionEvent.ACTION_UP,
                1, properties, coords,
                0, 0, 1.0f, 1.0f,
                0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);

        touchDownTime = 0;
        return injectEventSync(event);
    }

    private boolean touchMove(float x, float y) {
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[]{new MotionEvent.PointerProperties()};
        properties[0].id = 0;
        properties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[]{new MotionEvent.PointerCoords()};
        coords[0].orientation = 0;
        coords[0].pressure = 1.0f;
        coords[0].size = .0f;
        coords[0].x = x;
        coords[0].y = y;

        final long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(
                touchDownTime, eventTime, MotionEvent.ACTION_MOVE,
                1, properties, coords,
                0, 0, 1.0f, 1.0f,
                0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);

        return injectEventSync(event);
    }

    public boolean sendKey(int keyCode) {
        return sendKey(keyCode, 0);
    }

    public boolean sendKey(int keyCode, int metaState) {
        final long eventTime = SystemClock.uptimeMillis();
        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN,
                keyCode, 0, metaState, 0, 0, 0,
                InputDevice.SOURCE_KEYBOARD);
        if (injectEventSync(downEvent)) {
            KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP,
                    keyCode, 0, metaState, 0, 0, 0,
                    InputDevice.SOURCE_KEYBOARD);
            if (injectEventSync(upEvent)) {
                return true;
            }
        }
        return false;
    }

    public void sendText(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int keyCode;
            if (c >= '0' && c <= '9') {
                keyCode = c - '0' + KeyEvent.KEYCODE_0;
            } else if (c >= 'a' && c <= 'z') {
                keyCode = c - 'a' + KeyEvent.KEYCODE_A;
            } else if (c >= 'A' && c <= 'Z') {
                keyCode = c - 'A' + KeyEvent.KEYCODE_A;
            } else {
                throw new IllegalStateException("only support a-z 0-9, got：" + c);
            }
            sendKey(keyCode);
            SystemClock.sleep((long) (random.nextFloat() * 1000));
        }
    }

    private boolean injectEventSync(InputEvent event) {
        return adapter.injectInputEvent(event, 1);
    }

    public void randomDrag(int downX, int downY, int upX, int upY, int duration) {
        int rangeX = Math.abs(downX - upX) / 5;
        int rangeY = Math.abs(downY - upY) / 5;
        downX = randomInt(downX, rangeX);
        downY = randomInt(downY, rangeY);
        upX = randomInt(upX, rangeX);
        upY = randomInt(upY, rangeY);
        duration = randomInt(duration, duration / 5);
        drag(downX, downY, upX, upY, duration);
    }

    /**
     * @param a
     * @param range
     * @return random.choice(( a - range, a + range))
     */
    public static int randomInt(int a, int range) {
        Random random = new Random();
        int i = random.nextInt(range);
        int sign = random.nextInt(2);
        return sign == 1 ? a + i : a - i;
    }


}
