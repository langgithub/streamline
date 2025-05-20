

#ifndef MAGICR_AUTO_EVENT_H
#define MAGICR_AUTO_EVENT_H

#include <sys/ioctl.h>
#include <time.h>

typedef unsigned short __u16;
typedef __signed__ int __s32;

struct input_event {
    struct timeval time;
    __u16 type;
    __u16 code;
    __s32 value;
};

typedef struct {
    int x;
    int y;
} Point;

struct event_prop {
    char *model;
    char *path;
    float x_precision;
    float y_precision;
    int min_major;
    int max_major;
    int min_minor;
    int rule;   // 0表示 == 1 表示<=
};

#define EVIOCGVERSION _IOR('E', 0x01, int)

#define ACTION_DOWN        1
#define ACTION_UP        0

#define ABS_MT_SLOT        0x2f    /* MT slot being modified */
#define ABS_MT_TOUCH_MAJOR    0x30    /* Major axis of touching ellipse */
#define ABS_MT_TOUCH_MINOR    0x31    /* Minor axis (omit if circular) */
#define ABS_MT_WIDTH_MAJOR    0x32    /* Major axis of approaching ellipse */
#define ABS_MT_WIDTH_MINOR    0x33    /* Minor axis (omit if circular) */
#define ABS_MT_ORIENTATION    0x34    /* Ellipse orientation */
#define ABS_MT_POSITION_X    0x35    /* Center X touch position */
#define ABS_MT_POSITION_Y    0x36    /* Center Y touch position */
#define ABS_MT_TOOL_TYPE    0x37    /* Type of touching device */
#define ABS_MT_BLOB_ID        0x38    /* Group a set of packets as a blob */
#define ABS_MT_TRACKING_ID    0x39    /* Unique ID of initiated contact */
#define ABS_MT_PRESSURE        0x3a    /* Pressure on contact area */
#define ABS_MT_DISTANCE        0x3b    /* Contact hover distance */
#define ABS_MT_TOOL_X        0x3c    /* Center X tool position */
#define ABS_MT_TOOL_Y        0x3d    /* Center Y tool position */

#define BTN_DIGI        0x140
#define BTN_TOOL_PEN        0x140
#define BTN_TOOL_RUBBER        0x141
#define BTN_TOOL_BRUSH        0x142
#define BTN_TOOL_PENCIL        0x143
#define BTN_TOOL_AIRBRUSH    0x144
#define BTN_TOOL_FINGER        0x145
#define BTN_TOOL_MOUSE        0x146
#define BTN_TOOL_LENS        0x147
#define BTN_TOOL_QUINTTAP    0x148    /* Five fingers on trackpad */
#define BTN_STYLUS3        0x149
#define BTN_TOUCH        0x14a
#define BTN_STYLUS        0x14b
#define BTN_STYLUS2        0x14c
#define BTN_TOOL_DOUBLETAP    0x14d
#define BTN_TOOL_TRIPLETAP    0x14e
#define BTN_TOOL_QUADTAP    0x14f    /* Four fingers on trackpad */


/*
 * Event types
 */

#define EV_SYN            0x00
#define EV_KEY            0x01
#define EV_REL            0x02
#define EV_ABS            0x03
#define EV_MSC            0x04
#define EV_SW            0x05
#define EV_LED            0x11
#define EV_SND            0x12
#define EV_REP            0x14
#define EV_FF            0x15
#define EV_PWR            0x16
#define EV_FF_STATUS        0x17
#define EV_MAX            0x1f
#define EV_CNT            (EV_MAX+1)

/*
 * Synchronization events.
 */

#define SYN_REPORT        0
#define SYN_CONFIG        1
#define SYN_MT_REPORT        2
#define SYN_DROPPED        3
#define SYN_MAX            0xf
#define SYN_CNT            (SYN_MAX+1)

int swipe(struct event_prop *prop, Point *points, int len);

int click(struct event_prop *prop, int x, int y);

#endif //MAGICR_AUTO_EVENT_H