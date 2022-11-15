package com.aiven.simplechoose.app

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.aiven.simplechoose.R
import com.aiven.simplechoose.app.task.Task
import com.aiven.simplechoose.app.task.TaskApp
import com.aiven.simplechoose.utils.ActivityManager
import com.aiven.simplechoose.utils.ThemeUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator

class App : TaskApp(), ActivityLifecycleCallbacks {

    init {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.normal_refresh_bg, R.color.normal_refresh_txt)
            ClassicsHeader(context)
        })
        SmartRefreshLayout.setDefaultRefreshFooterCreator(DefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        })
    }

//    override fun onCreate() {
//        super.onCreate()
//       // registerActivityLifecycleCallbacks(this)
//    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityManager.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityManager.remove(activity)
    }
}