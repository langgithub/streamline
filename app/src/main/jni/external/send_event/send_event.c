#include <linux/input.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <jni.h>
#include <android/log.h>
#include "json_object.h"
#include "json_tokener.h"

#define LOG_TAG "MotionInject"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static void emit(int fd, int type, int code, int value) {
    struct input_event ie;
    memset(&ie, 0, sizeof(ie));
    gettimeofday(&ie.time, NULL);
    ie.type = type;
    ie.code = code;
    ie.value = value;
    write(fd, &ie, sizeof(ie));
}

static int extract_int_array(struct json_object* jarr, int* out, int max) {
    int len = json_object_array_length(jarr);
    for (int i = 0; i < len && i < max; ++i) {
        out[i] = json_object_get_int(json_object_array_get_idx(jarr, i));
    }
    return len;
}

static int extract_float_array(struct json_object* jarr, float* out, int max) {
    int len = json_object_array_length(jarr);
    for (int i = 0; i < len && i < max; ++i) {
        out[i] = (float)json_object_get_double(json_object_array_get_idx(jarr, i));
    }
    return len;
}

void inject_sequence_from_json_array(const char* path, const char* json_array_str) {
    struct json_object* root_array = json_tokener_parse(json_array_str);
    if (!root_array || !json_object_is_type(root_array, json_type_array)) {
        LOGI("Invalid JSON array");
        return;
    }

    int fd = open(path, O_RDWR);
    if (fd < 0) {
        LOGI("Failed to open device: %s", path);
        return;
    }

    int len = json_object_array_length(root_array);
    for (int idx = 0; idx < len; ++idx) {
        struct json_object* obj = json_object_array_get_idx(root_array, idx);

        int pointerCount = json_object_get_int(json_object_object_get(obj, "pointerCount"));
        int action = json_object_get_int(json_object_object_get(obj, "action"));

        struct json_object *jx = json_object_object_get(obj, "x");
        struct json_object *jy = json_object_object_get(obj, "y");
        struct json_object *jp = json_object_object_get(obj, "pressure");
        struct json_object *jid = json_object_object_get(obj, "pointerIds");
        struct json_object *jtmaj = json_object_object_get(obj, "touchMajor");
        struct json_object *jtmin = json_object_object_get(obj, "touchMinor");
        struct json_object *jtooltype = json_object_object_get(obj, "toolType");

        float *x = malloc(sizeof(float) * pointerCount);
        float *y = malloc(sizeof(float) * pointerCount);
        float *pressure = malloc(sizeof(float) * pointerCount);
        float *touchMajor = malloc(sizeof(float) * pointerCount);
        float *touchMinor = malloc(sizeof(float) * pointerCount);
        int *pid = malloc(sizeof(int) * pointerCount);
        int *toolType = malloc(sizeof(int) * pointerCount);

        extract_float_array(jx, x, pointerCount);
        extract_float_array(jy, y, pointerCount);
        extract_float_array(jp, pressure, pointerCount);
        extract_float_array(jtmaj, touchMajor, pointerCount);
        extract_float_array(jtmin, touchMinor, pointerCount);
        extract_int_array(jid, pid, pointerCount);
        extract_int_array(jtooltype, toolType, pointerCount);

        for (int i = 0; i < pointerCount; i++) {
            emit(fd, EV_ABS, ABS_MT_SLOT, i);
            if (action == 0 /* down */ || action == 2 /* move */) {
                emit(fd, EV_ABS, ABS_MT_TRACKING_ID, pid[i]);
                emit(fd, EV_ABS, ABS_MT_POSITION_X, (int)x[i]);
                emit(fd, EV_ABS, ABS_MT_POSITION_Y, (int)y[i]);
                emit(fd, EV_ABS, ABS_MT_PRESSURE, (int)(pressure[i]));
                emit(fd, EV_ABS, ABS_MT_TOUCH_MAJOR, (int)(touchMajor[i]));
                emit(fd, EV_ABS, ABS_MT_TOUCH_MINOR, (int)(touchMinor[i]));
                emit(fd, EV_ABS, ABS_MT_TOOL_TYPE, toolType[i]);
            } else if (action == 1 /* up */) {
                emit(fd, EV_ABS, ABS_MT_TRACKING_ID, -1);
            }
        }

        if (action == 0) {
            emit(fd, EV_KEY, BTN_TOUCH, 1);
        } else if (action == 1) {
            emit(fd, EV_KEY, BTN_TOUCH, 0);
        }

        emit(fd, EV_SYN, SYN_REPORT, 0);
        usleep(5000);  // simulate realistic timing

        free(x);
        free(y);
        free(pressure);
        free(touchMajor);
        free(touchMinor);
        free(pid);
        free(toolType);
    }

    close(fd);
    json_object_put(root_array);
}

int main(int argc, char *argv[]) {
    if (argc != 3) {
        fprintf(stderr, "Usage: %s <input_device> <json_file>\n", argv[0]);
        return 1;
    }

    const char *device_path = argv[1];
    const char *json_path = argv[2];

    FILE *file = fopen(json_path, "r");
    if (!file) {
        perror("Failed to open JSON file");
        return 1;
    }

    fseek(file, 0, SEEK_END);
    long len = ftell(file);
    fseek(file, 0, SEEK_SET);

    char *buffer = (char *)malloc(len + 1);
    fread(buffer, 1, len, file);
    buffer[len] = '\0';
    fclose(file);

    inject_sequence_from_json_array(device_path, buffer);

    free(buffer);
    return 0;
}
