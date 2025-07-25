LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CFLAGS := -O0 -Wall -Wno-unused-parameter -Wno-unused-private-field \
                -DANDROID_PLATFORM

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := MultiScreenMirror

LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_ENABLED:= disabled

LOCAL_JNI_SHARED_LIBRARIES := libopen_sles

LOCAL_PRIVATE_PLATFORM := true

LOCAL_SDK_VERSION := current
include $(BUILD_PACKAGE)

include $(call all-makefiles-under, $(LOCAL_PATH))
