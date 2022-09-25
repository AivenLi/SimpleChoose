package com.aiven.simplechoose.utils.notch

import android.content.Context

/**
 * @author  : AivenLi
 * @date    : 2022/9/3 19:23
 * @desc    :
 * */
class ViVoNotchUtils {

    companion object {
        private const val VIVO_NOTCH = 0x00000020 // 是否有刘海
        private const val VIVO_FILLET = 0x00000008 // 是否有圆角

        /**
         * 判断是否有刘海屏
         *
         * @param context
         * @return true：有刘海屏；false：没有刘海屏
         */
        fun hasNotch(context: Context): Boolean {
            var ret = false
            try {
                val classLoader = context.classLoader
                val FtFeature = classLoader.loadClass("android.util.FtFeature")
                val method = FtFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
                ret = method.invoke(
                    FtFeature,
                    VIVO_NOTCH
                ) as Boolean
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
    }
}