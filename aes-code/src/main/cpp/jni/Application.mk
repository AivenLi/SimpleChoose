# 确定CPU架构
APP_ABI := all

# 支持所有CPU架构：armeabi-v7a arm64-v8a x86_64 等等
# APP_ABI := all

# 一般对应Android SDK的最低版本
APP_PLATFORM := android-23

# c++_static 静态链接
# c++_shared 动态链接
# system 系统默认
# 如果生成的so库包含静态.a文件，这个属性要写成c++_static，否则可以不用写

# 用来指定C++功能
APP_CPP_FEATURES += exceptions rtti