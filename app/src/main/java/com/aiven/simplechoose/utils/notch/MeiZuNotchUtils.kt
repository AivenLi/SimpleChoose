package com.aiven.simplechoose.utils.notch

import android.content.Context
import android.provider.Settings
import android.view.Window

/**
 * @author  : AivenLi
 * @date    : 2022/9/3 19:14
 * @desc    :
 * */
class MeiZuNotchUtils {

    companion object {
        /**
         * 判断是否有刘海屏
         *
         * @param context
         * @return true：有刘海屏；false：没有刘海屏
         */
        fun hasNotch(context: Context?): Boolean {
            var ret = false
            try {
                val clazz = Class.forName("flyme.config.FlymeFeature")
                val field = clazz.getDeclaredField("IS_FRINGE_DEVICE")
                ret = field[null] as Boolean
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
            val fhid = context.resources.getIdentifier("fringe_height", "dimen", "android")
            if (fhid > 0) {
                result = context.resources.getDimensionPixelSize(fhid)
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
            val fwid = context.resources.getIdentifier("fringe_width", "dimen", "android")
            if (fwid > 0) {
                result = context.resources.getDimensionPixelSize(fwid)
            }
            return result
        }

        /**
         * 获取默认和隐藏刘海区开关值
         *
         * @param context
         * @return false表示“默认”，true表示“隐藏显示区域”
         */
        fun getIsNotchSwitchOpen(context: Context): Boolean {
            return Settings.Global.getInt(context.contentResolver, "mz_fringe_hide", 0) == 1
        }

        /**
         * 设置应用窗口在刘海屏手机使用刘海区
         *
         *
         * 通过添加窗口FLAG的方式设置页面使用刘海区显示
         *
         * @param window 应用页面window对象
         */
        fun setFullScreenWindowLayoutInDisplayCutout(window: Window?) {}
    }
}