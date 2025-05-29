package com.lang.streamline.hhh;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MotionEventModel {

    private static final String TAG = "sadf";

    // 触摸事件开始的时间，以毫秒为单位
    private long downTime;
    // 当前事件发生的时间，以毫秒为单位
    private long eventTime;
    // 事件的动作，如按下、移动、抬起等
    private int action;
    // 当前事件涉及的触摸点数量
    private int pointerCount;
    // 每个触摸点的唯一标识符数组
    private int[] pointerIds;
    // 每个触摸点的 X 坐标数组
    private float[] x;
    // 每个触摸点的 Y 坐标数组
    private float[] y;
    // 每个触摸点的压力值数组，范围从 0（无压力）到 1（最大压力）
    private float[] pressure;
    // 每个触摸点的尺寸数组，范围从 0（最小尺寸）到 1（最大尺寸）
    private float[] size;
    // 每个触摸点的工具类型数组，如手指、触控笔等
    private int[] toolType;
    // 事件发生时的元键状态，如 Shift、Alt 等
    private int metaState;
    // 事件发生时的按钮状态
    private int buttonState;
    // X 轴方向的精确度，通常是屏幕宽度除以屏幕的 X 分辨率
    private float xPrecision;
    // Y 轴方向的精确度，通常是屏幕高度除以屏幕的 Y 分辨率
    private float yPrecision;
    // 生成此事件的设备的 ID
    private int deviceId;
    // 触摸事件的边缘标志，指示触摸是否接近屏幕边缘
    private int edgeFlags;
    // 事件的来源，如触摸屏、触控板等
    private int source;
    // 事件的标志，包含额外的事件属性
    private int flags;
    // 每个触摸点的触摸面积主轴长度数组
    private float[] touchMajor;
    // 每个触摸点的触摸面积次轴长度数组
    private float[] touchMinor;
    // 每个触摸点的工具面积主轴长度数组
    private float[] toolMajor;
    // 每个触摸点的工具面积次轴长度数组
    private float[] toolMinor;
    private static final Random random = new Random();
    private float FLUCTUATION_RANGE = 0.005f; // 0.5% 波动范围
    private long startReplayTime = 0;
    private long originalStartTime = 0;
    private float xFluctuation = 1.0f;
    private float yFluctuation = 1.0f;



    public MotionEventModel(MotionEvent event) {
        this.downTime = event.getDownTime();
        this.eventTime = event.getEventTime();
        this.action = event.getAction();
        this.pointerCount = event.getPointerCount();
        this.pointerIds = new int[pointerCount];
        this.x = new float[pointerCount];
        this.y = new float[pointerCount];
        this.pressure = new float[pointerCount];
        this.size = new float[pointerCount];
        this.toolType = new int[pointerCount];
        this.touchMajor = new float[pointerCount];
        this.touchMinor = new float[pointerCount];
        this.toolMajor = new float[pointerCount];
        this.toolMinor = new float[pointerCount];

        for (int i = 0; i < pointerCount; i++) {
            this.pointerIds[i] = event.getPointerId(i);
            this.x[i] = event.getX(i);
            this.y[i] = event.getY(i);
            Log.d("WI", event.getX(i)+","+event.getY(i));
            this.pressure[i] = event.getPressure(i);
            this.size[i] = event.getSize(i);
            this.toolType[i] = event.getToolType(i);
            this.touchMajor[i] = event.getTouchMajor(i);
            this.touchMinor[i] = event.getTouchMinor(i);
            this.toolMajor[i] = event.getToolMajor(i);
            this.toolMinor[i] = event.getToolMinor(i);
        }

        this.metaState = event.getMetaState();
        this.buttonState = event.getButtonState();
        this.xPrecision = event.getXPrecision();
        this.yPrecision = event.getYPrecision();
        this.deviceId = event.getDeviceId();
        this.edgeFlags = event.getEdgeFlags();
        this.source = event.getSource();
        this.flags = event.getFlags();
    }

    private MotionEventModel() {
        // 空构造函数，仅用于 JSON 反序列化
    }

    public void initializeFluctuations() {
        xFluctuation = 1 + (random.nextFloat() * 2 - 1) * FLUCTUATION_RANGE;
        yFluctuation = 1 + (random.nextFloat() * 2 - 1) * FLUCTUATION_RANGE;
    }

    public MotionEvent toMotionEvent(Activity activity) {
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[pointerCount];
        MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[pointerCount];

        for (int i = 0; i < pointerCount; i++) {
            MotionEvent.PointerProperties prop = new MotionEvent.PointerProperties();
            prop.id = pointerIds[i];
            prop.toolType = toolType[i];
            properties[i] = prop;

            MotionEvent.PointerCoords coord = new MotionEvent.PointerCoords();
            coord.x = x[i] * xFluctuation;
            coord.y = y[i] * yFluctuation;

            coord.pressure = pressure[i];
            coord.size = size[i];
            coord.touchMajor = touchMajor[i];
            coord.touchMinor = touchMinor[i];
            coord.toolMajor = toolMajor[i];
            coord.toolMinor = toolMinor[i];
            coords[i] = coord;
        }

        long adjustedEventTime = calculateAdjustedTime(eventTime);
        long adjustedDownTime = calculateAdjustedTime(downTime);

        // hunue
        int getRealDeviceId = 0;

        return MotionEvent.obtain(
                adjustedDownTime, adjustedEventTime, action, pointerCount, properties, coords,
                metaState, buttonState, xPrecision, yPrecision, getRealDeviceId, edgeFlags, source, flags
        );
    }


    private float applyFluctuation(float value) {
        float fluctuation = 1 + (random.nextFloat() * 2 - 1) * FLUCTUATION_RANGE;
        return value * fluctuation;
    }

    private long calculateAdjustedTime(long originalTime) {
        if (startReplayTime == 0 || originalStartTime == 0) {
            return originalTime; // 如果未设置回放时间，则返回原始时间
        }
        long timeDiff = originalTime - originalStartTime;
        return startReplayTime + timeDiff;
    }

    public void setReplayTimes(long start, long original) {
        startReplayTime = start;
        originalStartTime = original;
        initializeFluctuations(); // 在每次回放开始时初始化波动
    }

    public void setFLUCTUATION_RANGE(float fluctuationRange) {
        this.FLUCTUATION_RANGE = fluctuationRange;
    }

    // Getters for all fields
    public long getDownTime() {
        return downTime;
    }

    public long getEventTime() {
        return eventTime;
    }

    public int getAction() {
        return action;
    }

    public int getPointerCount() {
        return pointerCount;
    }

    public int getPointerId(int index) {
        return pointerIds[index];
    }

    public float getX(int index) {
        return x[index];
    }

    public float getY(int index) {
        return y[index];
    }

    public float getPressure(int index) {
        return pressure[index];
    }

    public float getSize(int index) {
        return size[index];
    }

    public int getToolType(int index) {
        return toolType[index];
    }

    public int getMetaState() {
        return metaState;
    }

    public int getButtonState() {
        return buttonState;
    }

    public float getXPrecision() {
        return xPrecision;
    }

    public float getYPrecision() {
        return yPrecision;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getEdgeFlags() {
        return edgeFlags;
    }

    public int getSource() {
        return source;
    }

    public int getFlags() {
        return flags;
    }

    public float getTouchMajor(int index) {
        return touchMajor[index];
    }

    public float getTouchMinor(int index) {
        return touchMinor[index];
    }

    public float getToolMajor(int index) {
        return toolMajor[index];
    }

    public float getToolMinor(int index) {
        return toolMinor[index];
    }

    // Setters for mutable fields
    public void setX(int index, float value) {
        if (index >= 0 && index < pointerCount) {
            x[index] = value;
        }
    }

    public void setY(int index, float value) {
        if (index >= 0 && index < pointerCount) {
            y[index] = value;
        }
    }

    public void setTouchMajor(int index, float value) {
        if (index >= 0 && index < pointerCount) {
            touchMajor[index] = value;
        }
    }

    public void setTouchMinor(int index, float value) {
        if (index >= 0 && index < pointerCount) {
            touchMinor[index] = value;
        }
    }

    public void setToolMajor(int index, float value) {
        if (index >= 0 && index < pointerCount) {
            toolMajor[index] = value;
        }
    }

    public void setToolMinor(int index, float value) {
        if (index >= 0 && index < pointerCount) {
            toolMinor[index] = value;
        }
    }

    public void setSize(int index, float value) {
        if (index >= 0 && index < pointerCount) {
            size[index] = value;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("downTime", downTime);
        json.put("eventTime", eventTime);
        json.put("action", action);
        json.put("pointerCount", pointerCount);
        json.put("pointerIds", new JSONArray(pointerIds));
        json.put("x", new JSONArray(x));
        json.put("y", new JSONArray(y));
        json.put("pressure", new JSONArray(pressure));
        json.put("size", new JSONArray(size));
        json.put("toolType", new JSONArray(toolType));
        json.put("metaState", metaState);
        json.put("buttonState", buttonState);
        json.put("xPrecision", xPrecision);
        json.put("yPrecision", yPrecision);
        json.put("deviceId", deviceId);
        json.put("edgeFlags", edgeFlags);
        json.put("source", source);
        json.put("flags", flags);
        json.put("touchMajor", new JSONArray(touchMajor));
        json.put("touchMinor", new JSONArray(touchMinor));
        json.put("toolMajor", new JSONArray(toolMajor));
        json.put("toolMinor", new JSONArray(toolMinor));
        return json;
    }

    public static MotionEventModel fromJSONObject(JSONObject json) throws JSONException {
        MotionEventModel event = new MotionEventModel();
        event.downTime = json.getLong("downTime");
        event.eventTime = json.getLong("eventTime");
        event.action = json.getInt("action");
        event.pointerCount = json.getInt("pointerCount");

        JSONArray pointerIdsArray = json.getJSONArray("pointerIds");
        event.pointerIds = new int[event.pointerCount];
        for (int i = 0; i < event.pointerCount; i++) {
            event.pointerIds[i] = pointerIdsArray.getInt(i);
        }

        event.x = jsonArrayToFloatArray(json.getJSONArray("x"));
        event.y = jsonArrayToFloatArray(json.getJSONArray("y"));
        event.pressure = jsonArrayToFloatArray(json.getJSONArray("pressure"));
        event.size = jsonArrayToFloatArray(json.getJSONArray("size"));
        event.toolType = jsonArrayToIntArray(json.getJSONArray("toolType"));
        event.metaState = json.getInt("metaState");
        event.buttonState = json.getInt("buttonState");
        event.xPrecision = (float) json.getDouble("xPrecision");
        event.yPrecision = (float) json.getDouble("yPrecision");
        event.deviceId = json.getInt("deviceId");
        event.edgeFlags = json.getInt("edgeFlags");
        event.source = json.getInt("source");
        event.flags = json.getInt("flags");
        event.touchMajor = jsonArrayToFloatArray(json.getJSONArray("touchMajor"));
        event.touchMinor = jsonArrayToFloatArray(json.getJSONArray("touchMinor"));
        event.toolMajor = jsonArrayToFloatArray(json.getJSONArray("toolMajor"));
        event.toolMinor = jsonArrayToFloatArray(json.getJSONArray("toolMinor"));

        return event;
    }

    private static float[] jsonArrayToFloatArray(JSONArray jsonArray) throws JSONException {
        float[] floatArray = new float[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            floatArray[i] = (float) jsonArray.getDouble(i);
        }
        return floatArray;
    }

    private static int[] jsonArrayToIntArray(JSONArray jsonArray) throws JSONException {
        int[] intArray = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            intArray[i] = jsonArray.getInt(i);
        }
        return intArray;
    }
}