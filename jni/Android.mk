LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := SIGN
LOCAL_SRC_FILES := main_sign.c md5.c
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)