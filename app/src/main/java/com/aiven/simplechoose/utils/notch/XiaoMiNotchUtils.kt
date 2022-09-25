package com.aiven.simplechoose.utils.notch

import android.content.Context
import android.provider.Settings
import android.view.Window

/**
 * @author  : AivenLi
 * @date    : 2022/9/3 19:24
 * @desc    :
 * */
class XiaoMiNotchUtils {

    companion object {
        /**
         * 判断是否有刘海屏
         *
         * @param context
         * @return true：有刘海屏；false：没有刘海屏
         */
        fun hasNotch(context: Context): Boolean {
            var ret = false
            try {
                val cl = context.classLoader
                val SystemProperties = cl.loadClass("android.os.SystemProperties")
                val get = SystemProperties.getMethod(
                    "getInt",
                    String::class.java,
                    Int::class.javaPrimitiveType
                )
                ret = get.invoke(SystemProperties, "ro.miui.notch", 0) as Int == 1
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                return ret
            }
        }

        /**
         * 获取刘海屏高度
         *
         * @param context
         * @return
         */
        fun getNotHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("notch_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 获取刘海屏宽度
         *
         * @param context
         * @return
         */
        fun getNotWidth(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("notch_width", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 设置应用窗口在刘海屏手机使用刘海区
         *
         *
         * 通过添加窗口FLAG的方式设置页面使用刘海区显示
         *
         * @param window 应用页面window对象
         */
        fun setFullScreenWindowLayoutInDisplayCutout(window: Window?) {
            // 竖屏绘制到耳朵区
            val flag: Int =
                NotchUtils.FLAG_NOTCH_SUPPORT_MI or NotchUtils.FLAG_NOTCH_PORTRAIT_MI
            try {
                val method = Window::class.java.getMethod(
                    "addExtraFlags",
                    Int::class.javaPrimitiveType
                )
                method.invoke(window, flag)
            } catch (e: Exception) {
              //  Log.e("test", "addExtraFlags not found.")
            }
        }

        /**
         * 判断是否隐藏屏幕刘海
         *
         * @param context
         * @return false：未隐藏刘海区域 true：隐藏了刘海区域
         */
        fun getIsNotchHideOpen(context: Context): Boolean {
            return Settings.Global.getInt(context.contentResolver, "force_black", 0) == 1
        }
    }
}