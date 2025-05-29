#include <jni.h>
#include <sys/system_properties.h>
#include <android/log.h>
#include <string.h>
#include <fcntl.h>
#include <errno.h>
#include <stdbool.h>
#include <unistd.h>
#include "auto_event.h"

#define MODEL_VALUE_MAX 64
#define UNKNOWN_MODEL "UNKNOWN"
#define LOG_TAG "SHOW_TIME"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


// getevent -lt
// ./send_event /dev/input/event2
struct event_prop ReadMi_13r = {
        "23124RN87C",
        "/dev/input/event2",
        7.111f,
        7.253f,
        25,
        46,
        25,
        0
};

#define PROP_LIST_SIZE 0x10
struct event_prop *event_prop_list[PROP_LIST_SIZE] = {
        &ReadMi_13r
};


char *get_build_model() {
    static char model[MODEL_VALUE_MAX];
    if (__system_property_get("ro.product.model", model) > 0) {
        return model;
    }
    return UNKNOWN_MODEL;
}

struct event_prop *get_event_prop(const char *model) {
    for (int i = 0; i < PROP_LIST_SIZE; i++) {
        if (strcmp(event_prop_list[i]->model, model) == 0) {
            return event_prop_list[i];
        }
    }
    return NULL;
}


JNIEXPORT jint JNICALL
Java_com_lang_streamline_utils_AutoEventNativeUtils_click(JNIEnv *env, jclass clazz,
                                                          jint x,
                                                          jint y) {
    char *model = get_build_model();
    if (strcmp(model, UNKNOWN_MODEL) == 0) {
        return -1;
    }
    struct event_prop *prop = get_event_prop(model);
    if (prop == NULL) {
        return -2;
    }
    errno = 0;
    LOGI("path -> %s", prop->path);
    LOGI("uid=%d euid=%d", getuid(), geteuid());
    int fd = open(prop->path, O_RDWR);
    if (fd < 0 && errno == 13) {
        LOGI("权限不够");
        return -3;
    }
    int result = click(prop, x, y);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_lang_streamline_utils_AutoEventNativeUtils_swipe(JNIEnv *env, jclass clazz,
                                                          jobjectArray precision_points) {
    char *model = get_build_model();
    if (strcmp(model, UNKNOWN_MODEL) == 0) {
        return -1;
    }
    struct event_prop *prop = get_event_prop(model);
    if (prop == NULL) {
        return -2;
    }
    errno = 0;
    int fd = open(prop->path, O_RDWR);
    if (fd < 0 && errno == 13) {
        return -3;
    }
    jsize len = (*env)->GetArrayLength(env, precision_points);
    jobject pointObj;
    Point pointsArray[len];
    for (int i = 0; i < len; i++) {
        pointObj = (*env)->GetObjectArrayElement(env, precision_points, i);
        jclass cls = (*env)->FindClass(env, "android/graphics/Point");
        jfieldID fidX = (*env)->GetFieldID(env, cls, "x", "I");
        jfieldID fidY = (*env)->GetFieldID(env, cls, "y", "I");
        jint x = (*env)->GetIntField(env, pointObj, fidX);
        jint y = (*env)->GetIntField(env, pointObj, fidY);
        pointsArray[i].x = x;
        pointsArray[i].y = y;
    }
    int result = swipe(prop, pointsArray, len);
    return result;
}

JNIEXPORT jfloatArray JNICALL
Java_com_lang_streamline_utils_AutoEventNativeUtils_getPrecious(JNIEnv *env,
                                                                          jclass clazz) {
    jfloatArray result = (*env)->NewFloatArray(env, 2);
    char *model = get_build_model();
    if (strcmp(model, UNKNOWN_MODEL) == 0) {
        return result;
    }
    struct event_prop *prop = get_event_prop(model);
    jfloat values[] = {prop->x_precision, prop->y_precision};
    (*env)->SetFloatArrayRegion(env, result, 0, 2, values);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_lang_streamline_utils_AutoEventNativeUtils_rootify(JNIEnv* env, jclass clazz) {
    if (setgid(0)!=0 || setuid(0)!=0) {
        return -1; // 失败
    }
    return 0;    // 成功
}