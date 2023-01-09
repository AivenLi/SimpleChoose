package com.aiven.simplechoose.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration


object DensityUtil {

    //参考设备的宽度,单位dp
    private const val WIDTH = 1920f
    private var appDensity = 0f
    private var appScaleDensity = 0f

    fun setDensity(application: Application, activity: Activity) {
        //获取当前app屏幕信息
        val displayMetrics = application.resources.displayMetrics
        if (appDensity == 0f) {
            //初始化赋值操作
            appDensity = displayMetrics.density
            appScaleDensity = displayMetrics.scaledDensity

            //计算目标值的density，scaleDensity，densityDpi，获取到的单位是px

            //添加字体变化监听回调
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    if (newConfig.fontScale > 0) {
                        appScaleDensity = application.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {
                }



            })

            //替换Activity的density，scaleDensity,densityDpi
            val dm = activity.resources.displayMetrics
            dm.scaledDensity = 1.0f
            dm.xdpi = 25.4f
        }
    }

}