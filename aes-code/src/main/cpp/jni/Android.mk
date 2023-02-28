# 每个Android.mk文件必须以定义LOCAL_PATH为开始
# 宏my-dir则由Build System提供，返回包含Android.mk的目录路径
LOCAL_PATH := $(call my-dir)

# 清除所有LOCAL_开头的变量，除了LOCAL_PATH
include $(CLEAR_VARS)

# 模块名称，需要保证模块名称的唯一性
LOCAL_MODULE := openfile-lib

# 指定将要打包的源码，多个文件用空格隔开，如果需要换行，在换行处添加“\”
LOCAL_SRC_FILES := code_util.cpp

# 负责收集自从上次调用 include $(CLEAR_VARS) 后的所有LOCAL_XXX信息，并决定编译成什么
# BUILD_STATIC_LIBRARY：编译为静态库
# BUILD_SHARED_LIBRARY：编译为动态库
# BUILD_EXECUTABLE：编译为可执行程序
include $(BUILD_SHARED_LIBRARY)