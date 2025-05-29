
#include "auto_event.h"
#include <android/log.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>

#define LOG_TAG "SHOW_TIME"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


int generate_random(int min, int max) {
    int range = max - min + 1;
    int random = rand() % range + min;
    return random;
}

int min(int a, int b) {
    return (a < b) ? a : b;
}

int click(struct event_prop *prop, int x, int y) {
    LOGI("click %d %d", x, y);
    int fd;
    ssize_t ret;
    int version;
    struct input_event event;
    fd = open(prop->path, O_RDWR);
    if (fd < 0) {
        LOGI("could not open %s\n", strerror(errno));
        return 1;
    }
    if (ioctl(fd, EVIOCGVERSION, &version)) {
        LOGI("could not get driver version for %s\n", strerror(errno));
        return 1;
    }

    int tracking_id = generate_random(10, 60000);
    int major = generate_random(prop->min_major, prop->max_major);
    int minor = generate_random(min(prop->min_minor, major), major);
    if (prop->rule == 0) {
        minor = major;
    }
    int event_x = x * prop->x_precision;
    int event_y = y * prop->y_precision;

    int event_seq[12][3] = {
            {EV_KEY, BTN_TOUCH,       ACTION_DOWN},
            {EV_KEY, BTN_TOOL_FINGER, ACTION_DOWN},
            {EV_ABS, ABS_MT_TRACKING_ID, tracking_id},
            {EV_ABS, ABS_MT_POSITION_X,  event_x},
            {EV_ABS, ABS_MT_POSITION_Y,  event_y},
            {EV_ABS, ABS_MT_TOUCH_MAJOR, major},
            {EV_ABS, ABS_MT_TOUCH_MINOR, minor},
            {EV_SYN, SYN_REPORT,         0},
            {EV_ABS, ABS_MT_TRACKING_ID, -1},
            {EV_KEY, BTN_TOOL_FINGER, ACTION_UP},
            {EV_KEY, BTN_TOUCH,       ACTION_UP},
            {EV_SYN, SYN_REPORT,         0},
    };
    for (int i = 0; i < 12; ++i) {
        memset(&event, 0, sizeof(event));
        event.type = event_seq[i][0];
        event.code = event_seq[i][1];
        event.value = event_seq[i][2];
        ret = write(fd, &event, sizeof(event));
        if (i == 8) {
            usleep((rand() & 0xfffffff) % 20 + 40);
        }
        if (ret < (ssize_t) sizeof(event)) {
            LOGI("write event failed, %s\n", strerror(errno));
            return -1;
        }
    }
    return 0;
}

int swipe(struct event_prop *prop, Point *points, int len) {
    int fd;
    ssize_t ret;
    int version;
    struct input_event event;
    fd = open(prop->path, O_RDWR);
    if (fd < 0) {
        LOGI("could not open %s\n", strerror(errno));
        return 1;
    }
    if (ioctl(fd, EVIOCGVERSION, &version)) {
        LOGI("could not get driver version for %s\n", strerror(errno));
        return 1;
    }

    // start swipe
    int tracking_id = generate_random(10, 60000);
    int major = generate_random(prop->min_major, prop->max_major);
    int minor = generate_random(min(prop->min_minor, major), major);
    if (prop->rule == 0) {
        minor = major;
    }

    int event_start[8][3] = {
            {EV_KEY, BTN_TOUCH,       ACTION_DOWN},
            {EV_KEY, BTN_TOOL_FINGER, ACTION_DOWN},
            {EV_ABS, ABS_MT_TRACKING_ID, tracking_id},
            {EV_ABS, ABS_MT_POSITION_X,  points[0].x * prop->x_precision},
            {EV_ABS, ABS_MT_POSITION_Y,  points[0].y * prop->y_precision},
            {EV_ABS, ABS_MT_TOUCH_MAJOR, major},
            {EV_ABS, ABS_MT_TOUCH_MINOR, minor},
            {EV_SYN, SYN_REPORT,         00000000},
    };
    for (int i = 0; i < sizeof(event_start) / sizeof(event_start[0]); ++i) {
        memset(&event, 0, sizeof(event));
        event.type = event_start[i][0];
        event.code = event_start[i][1];
        event.value = event_start[i][2];
        ret = write(fd, &event, sizeof(event));
        if (ret < (ssize_t) sizeof(event)) {
            LOGI("write event failed, %s\n", strerror(errno));
            return -1;
        }
    }
    // swipe
//    {EV_ABS, ABS_MT_POSITION_X,  0x114f},
//    {EV_ABS, ABS_MT_POSITION_Y,  0x3428},
//    {EV_SYN, SYN_REPORT,         00000000},
    for (int i = 0; i < len; ++i) {
        memset(&event, 0, sizeof(event));
        event.type = EV_ABS;
        event.code = ABS_MT_POSITION_X;
        event.value = points[i].x * prop->x_precision;
        ret = write(fd, &event, sizeof(event));

        memset(&event, 0, sizeof(event));
        event.type = EV_ABS;
        event.code = ABS_MT_POSITION_Y;
        event.value = points[i].y * prop->y_precision;
        ret = write(fd, &event, sizeof(event));

        memset(&event, 0, sizeof(event));
        event.type = EV_SYN;
        event.code = SYN_REPORT;
        event.value = 0;
        ret = write(fd, &event, sizeof(event));
        if (ret < (ssize_t) sizeof(event)) {
            LOGI("write event failed, %s\n", strerror(errno));
            return -1;
        }
        usleep(10 * 1000);
    }

    // end
    int event_seq[4][3] = {
            {EV_ABS, ABS_MT_TRACKING_ID, -1},
            {EV_KEY, BTN_TOOL_FINGER, ACTION_UP},
            {EV_KEY, BTN_TOUCH,       ACTION_UP},
            {EV_SYN, SYN_REPORT,         0}
    };
    for (int i = 0; i < sizeof(event_seq) / sizeof(event_seq[0]); ++i) {
        memset(&event, 0, sizeof(event));
        event.type = event_seq[i][0];
        event.code = event_seq[i][1];
        event.value = event_seq[i][2];
        ret = write(fd, &event, sizeof(event));
        if (ret < (ssize_t) sizeof(event)) {
            LOGI("write event failed, %s\n", strerror(errno));
            return -1;
        }
    }

    return 0;
}