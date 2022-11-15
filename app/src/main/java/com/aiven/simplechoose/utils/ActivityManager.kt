package com.aiven.simplechoose.utils

import android.app.Activity
import android.util.Log
import com.aiven.simplechoose.pages.BaseActivity
import java.util.*
import kotlin.collections.HashMap

object ActivityManager {

    private val activityList = ArrayList<Activity>()

    fun add(activity: Activity) {
        if (activity is BaseActivity<*>) {
            Log.d("ActivityManager-Debug", "添加：${activity.getDebugTAG()}")
        } else {
            Log.d("ActivityManager-Debug", "添加：${activity}")
        }
        activityList.add(activity)
    }

    fun remove(activity: Activity) {
        if (activity is BaseActivity<*>) {
            Log.d("ActivityManager-Debug", "移除：${activity.getDebugTAG()}")
        } else {
            Log.d("ActivityManager-Debug", "移除：${activity}")
        }
        activityList.remove(activity)
    }

    /**
     * finish所有除了传入的activity的activity
     * @param activity
     * */
    fun finishAll(activity: Activity) {
        for (a in activityList) {
            if (a != activity) {
                Log.d("-Debug", "移除Activity")
                a.finish()
            }
        }
        activityList.clear()
        activityList.add(activity)
    }
}