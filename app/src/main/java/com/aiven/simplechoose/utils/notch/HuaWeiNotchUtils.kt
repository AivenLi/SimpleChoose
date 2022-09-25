package com.aiven.simplechoose.utils.notch

import android.content.Context
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import java.lang.reflect.InvocationTargetException

/**
 * @author  : AivenLi
 * @date    : 2022/9/3 19:12
 * @desc    :
 * */
class HuaWeiNotchUtils {

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
                val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
                ret = get.invoke(HwNotchSizeUtil) as Boolean
            } catch (e: ClassNotFoundException) {
                //Log.e("test", "hasNotchInScreen ClassNotFoundException")
            } catch (e: NoSuchMethodException) {
               // Log.e("test", "hasNotchInScreen NoSuchMethodException")
            } catch (e: Exception) {
               // Log.e("test", "hasNotchInScreen Exception")
            } finally {
                return ret
            }
        }

        /**
         * 获取刘海尺寸
         *
         * @param context
         * @return int[0]值为刘海宽度 int[1]值为刘海高度
         */
        fun getNotchSize(context: Context): IntArray? {
            var ret = intArrayOf(0, 0)
            try {
                val cl = context.classLoader
                val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                val get = HwNotchSizeUtil.getMethod("getNotchSize")
                ret = get.invoke(HwNotchSizeUtil) as IntArray
            } catch (e: ClassNotFoundException) {
              //  Log.e("test", "getNotchSize ClassNotFoundException")
            } catch (e: NoSuchMethodException) {
              //  Log.e("test", "getNotchSize NoSuchMethodException")
            } catch (e: Exception) {
              //  Log.e("test", "getNotchSize Exception")
            } finally {
                return ret
            }
        }

        /**
         * 设置应用窗口在华为刘海屏手机使用刘海区
         *
         *
         * 通过添加窗口FLAG的方式设置页面使用刘海区显示
         *
         * @param window 应用页面window对象
         */
        fun setFullScreenWindowLayoutInDisplayCutout(window: Window?) {
            if (window == null) {
                return
            }
            val layoutParams = window.attributes
            try {
                val layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx")
                val con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams::class.java)
                val layoutParamsExObj = con.newInstance(layoutParams)
                val method = layoutParamsExCls.getMethod("addHwFlags", Int::class.javaPrimitiveType)
                method.invoke(
                    layoutParamsExObj,
                    NotchUtils.FLAG_NOTCH_SUPPORT_HW
                )
            } catch (e: ClassNotFoundException) {
              //  Log.e("test", "hw add notch screen flag api error")
            } catch (e: NoSuchMethodException) {
            //    Log.e("test", "hw add notch screen flag api error")
            } catch (e: IllegalAccessException) {
              //  Log.e("test", "hw add notch screen flag api error")
            } catch (e: InstantiationException) {
               // Log.e("test", "hw add notch screen flag api error")
            } catch (e: InvocationTargetException) {
              //  Log.e("test", "hw add notch screen flag api error")
            } catch (e: Exception) {
               // Log.e("test", "other Exception")
            }
        }

        /**
         * 恢复应用不使用刘海区显示
         *
         *
         * 通过去除窗口FLAG的方式设置页面不使用刘海区显示
         *
         * @param window 应用页面window对象
         */
        fun setNotFullScreenWindowLayoutInDisplayCutout(window: Window?) {
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
                method.invoke(
                    layoutParamsExObj,
                    NotchUtils.FLAG_NOTCH_SUPPORT_HW
                )
            } catch (e: ClassNotFoundException) {
               // Log.e("test", "hw clear notch screen flag api error")
            } catch (e: NoSuchMethodException) {
              //  Log.e("test", "hw clear notch screen flag api error")
            } catch (e: IllegalAccessException) {
               // Log.e("test", "hw clear notch screen flag api error")
            } catch (e: InstantiationException) {
              //  Log.e("test", "hw clear notch screen flag api error")
            } catch (e: InvocationTargetException) {
               // Log.e("test", "hw clear notch screen flag api error")
            } catch (e: Exception) {
               // Log.e("test", "other Exception")
            }
        }

        /**
         * 获取默认和隐藏刘海区开关值
         *
         * @param context
         * @return 0表示“默认”，1表示“隐藏显示区域”
         */
        fun getIsNotchSwitchOpen(context: Context): Int {
            return Settings.Secure.getInt(
                context.contentResolver,
                NotchUtils.DISPLAY_NOTCH_STATUS,
                0
            )
        }
    }
}