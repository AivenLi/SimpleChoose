package com.aiven.simplechoose.utils.notch

import android.content.Context
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import java.lang.reflect.InvocationTargetException

/**
 * @author  : AivenLi
 * @date    : 2022/9/3 19:15
 * @desc    :
 * */
class NotchUtils {

    companion object {
        /*华为刘海屏全屏显示FLAG*/
        const val FLAG_NOTCH_SUPPORT_HW = 0x00010000
        const val DISPLAY_NOTCH_STATUS = "display_notch_status"
        /*小米刘海屏全屏显示FLAG*/
        const val FLAG_NOTCH_SUPPORT_MI = 0x00000100 // 开启配置
        const val FLAG_NOTCH_PORTRAIT_MI = 0x00000200 // 竖屏配置
        const val FLAG_NOTCH_HORIZONTAL_MI = 0x00000400 // 横屏配置
        /*Vivo手机屏幕属性参数*/
        const val VIVO_NOTCH = 0x00000020 // 是否有刘海
        const val VIVO_FILLET = 0x00000008 // 是否有圆角

        /**
         * 华为手机判断是否有刘海屏
         *
         * @param context
         * @return true：有刘海屏；false：没有刘海屏
         */
        fun hasNotchAtHuawei(context: Context): Boolean {
            var ret = false
            try {
                val cl = context.classLoader
                val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
                ret = get.invoke(HwNotchSizeUtil) as Boolean
            } catch (e: ClassNotFoundException) {
                //Log.e("test", "hasNotchInScreen ClassNotFoundException")
            } catch (e: NoSuchMethodException) {
                //Log.e("test", "hasNotchInScreen NoSuchMethodException")
            } catch (e: Exception) {
                //Log.e("test", "hasNotchInScreen Exception")
            } finally {
                return ret
            }
        }

        /**
         * 华为手机获取刘海尺寸
         *
         * @param context
         * @return int[0]值为刘海宽度 int[1]值为刘海高度
         */
        fun getNotchSizeAtHuawei(context: Context): IntArray? {
            var ret = intArrayOf(0, 0)
            try {
                val cl = context.classLoader
                val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                val get = HwNotchSizeUtil.getMethod("getNotchSize")
                ret = get.invoke(HwNotchSizeUtil) as IntArray
            } catch (e: ClassNotFoundException) {
                //Log.e("test", "getNotchSize ClassNotFoundException")
            } catch (e: NoSuchMethodException) {
                //Log.e("test", "getNotchSize NoSuchMethodException")
            } catch (e: Exception) {
                //Log.e("test", "getNotchSize Exception")
            } finally {
                return ret
            }
        }

        /**
         * 华为手机设置应用窗口在刘海屏手机使用刘海区
         *
         * @param window 应用页面window对象
         */
        fun setFullScreenAtHuawei(window: Window?) {
            if (window == null) {
                return
            }
            val layoutParams = window.attributes
            try {
                val layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx")
                val con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams::class.java)
                val layoutParamsExObj = con.newInstance(layoutParams)
                val method = layoutParamsExCls.getMethod("addHwFlags", Int::class.javaPrimitiveType)
                method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_HW)
            } catch (e: ClassNotFoundException) {
                //Log.e("test", "hw add notch screen flag api error")
            } catch (e: NoSuchMethodException) {
                //Log.e("test", "hw add notch screen flag api error")
            } catch (e: IllegalAccessException) {
                //Log.e("test", "hw add notch screen flag api error")
            } catch (e: InstantiationException) {
                //Log.e("test", "hw add notch screen flag api error")
            } catch (e: InvocationTargetException) {
                //Log.e("test", "hw add notch screen flag api error")
            } catch (e: Exception) {
                //Log.e("test", "other Exception")
            }
        }

        /**
         * 华为手机恢复应用不使用刘海区显示
         *
         * @param window 应用页面window对象
         */
        fun setNotFullScreenAtHuawei(window: Window?) {
            if (window == null) {
                return
            }
            val layoutParams = window.attributes
            try {
                val layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx")
                val con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams::class.java)
                val layoutParamsExObj = con.newInstance(layoutParams)
                val method =
                    layoutParamsExCls.getMethod("clearHwFlags", Int::class.javaPrimitiveType)
                method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_HW)
            } catch (e: ClassNotFoundException) {
                //Log.e("test", "hw clear notch screen flag api error")
            } catch (e: NoSuchMethodException) {
                //Log.e("test", "hw clear notch screen flag api error")
            } catch (e: IllegalAccessException) {
                //Log.e("test", "hw clear notch screen flag api error")
            } catch (e: InstantiationException) {
                //Log.e("test", "hw clear notch screen flag api error")
            } catch (e: InvocationTargetException) {
                //Log.e("test", "hw clear notch screen flag api error")
            } catch (e: Exception) {
                //Log.e("test", "other Exception")
            }
        }

        /**
         * 华为手机获取默认和隐藏刘海区开关值
         *
         * @param context
         * @return 0表示“默认”，1表示“隐藏显示区域”
         */
        fun getIsNotchSwitchOpenAtHuawei(context: Context): Int {
            return Settings.Secure.getInt(context.contentResolver, DISPLAY_NOTCH_STATUS, 0)
        }

        /**
         * 小米手机判断是否有刘海屏
         *
         * @param context
         * @return true：有刘海屏；false：没有刘海屏
         */
        fun hasNotchAtXiaomi(context: Context): Boolean {
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
         * 小米手机获取刘海屏高度
         *
         * @param context
         * @return
         */
        fun getNotHeightAtXiaomi(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("notch_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 小米手机获取刘海屏宽度
         *
         * @param context
         * @return
         */
        fun getNotWidthAtXiaomi(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("notch_width", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 小米手机设置应用窗口在刘海屏手机使用刘海区
         *
         * @param window 应用页面window对象
         */
        fun setFullScreenAtXiaomi(window: Window?) {
            // 竖屏绘制到耳朵区
            val flag = FLAG_NOTCH_SUPPORT_MI or FLAG_NOTCH_PORTRAIT_MI
            try {
                val method = Window::class.java.getMethod(
                    "addExtraFlags",
                    Int::class.javaPrimitiveType
                )
                method.invoke(window, flag)
            } catch (e: Exception) {
                //Log.e("test", "addExtraFlags not found.")
            }
        }

        /**
         * 小米手机判断是否隐藏屏幕刘海
         *
         * @param context
         * @return false：未隐藏刘海区域 true：隐藏了刘海区域
         */
        fun getIsNotchHideOpenAtXiaomi(context: Context): Boolean {
            return Settings.Global.getInt(context.contentResolver, "force_black", 0) == 1
        }

        /**
         * Vivo手机判断是否有刘海屏
         *
         * @param context
         * @return true：有刘海屏；false：没有刘海屏
         */
        fun hasNotchAtVivo(context: Context): Boolean {
            var ret = false
            try {
                val classLoader = context.classLoader
                val FtFeature = classLoader.loadClass("android.util.FtFeature")
                val method = FtFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
                ret = method.invoke(FtFeature, VIVO_NOTCH) as Boolean
            } catch (e: ClassNotFoundException) {
                //Log.e("Notch", "hasNotchAtVivo ClassNotFoundException")
            } catch (e: NoSuchMethodException) {
                //Log.e("Notch", "hasNotchAtVivo NoSuchMethodException")
            } catch (e: Exception) {
                //Log.e("Notch", "hasNotchAtVivo Exception")
            } finally {
                return ret
            }
        }

        /**
         * Oppo手机判断是否有刘海屏
         *
         * @param context
         * @return true：有刘海屏；false：没有刘海屏
         */
        fun hasNotchAtOppo(context: Context): Boolean {
            return context.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
        }

        /**
         * 获取状态栏高度
         *
         * @param context
         * @return
         */
        fun getStatusBarHeight(context: Context): Int {
            var statusBarHeight = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
            return statusBarHeight
        }
    }
}