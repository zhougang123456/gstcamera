//
// Created by zhougang on 2020/1/16.
//

#include "camera.h"
#include <jni.h>
#include <stdio.h>
#include "android/log.h"
#include "gst/gst.h"
#include <gst/video/gstvideometa.h>
#include "stdlib.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>

extern "C" {
JNIEXPORT void
JNICALL Java_com_example_gstcamera_MainActivity_NativeStart(JNIEnv *env,
                                                            jobject obj,
                                                            jobject surface) {

    gchar *desc =
            g_strdup_printf("ahcsrc device=1 ! video/x-raw ! videoflip method=7 ! glimagesink name=sink");
   // GstElement *camerabin = gst_element_factory_make("camerabin", "camerabin");
    GError *err = NULL;
    __android_log_print(6, "zhou", "GStreamer pipeline: %s", desc);

    GstElement *pipeline = gst_parse_launch_full(desc, NULL, GST_PARSE_FLAG_FATAL_ERRORS, &err);
    //GstElement *pipeline = camerabin;
    ANativeWindow *native_window = surface ? ANativeWindow_fromSurface(env, surface) : NULL;

    if (!native_window){
        __android_log_write(6, "zhou", "win handle is null");
    }
    GstVideoOverlay *overlay = GST_VIDEO_OVERLAY(gst_bin_get_by_name(GST_BIN(pipeline), "sink"));
    gst_video_overlay_set_window_handle(GST_VIDEO_OVERLAY(overlay), (guintptr)native_window);
    gst_video_overlay_expose(GST_VIDEO_OVERLAY(overlay));
    g_free(desc);

    if (!pipeline || err) {
        __android_log_print(6, "zhou", "GStreamer error: %s", err->message);
        g_clear_error(&err);
        return;
    }

    if (gst_element_set_state(pipeline, GST_STATE_PLAYING) == GST_STATE_CHANGE_FAILURE) {
        __android_log_write(6, "zhou", "set state failure");
        return;
    }
}
}