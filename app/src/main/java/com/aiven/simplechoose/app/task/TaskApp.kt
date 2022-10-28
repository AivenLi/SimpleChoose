package com.aiven.simplechoose.app.task

import android.app.Application
import com.aiven.hfl.FloatApp
import com.aiven.simplechoose.app.task.impl.AppContext
import com.aiven.simplechoose.app.task.impl.MMKVTask
import com.aiven.simplechoose.app.task.impl.RetrofitTask
import com.aiven.simplechoose.net.cache.CacheUtil

/**
 * @author  : AivenLi
 * @date    : 2022/8/6 12:45
 * */
open class TaskApp: FloatApp() {

    override fun onCreate() {
        super.onCreate()
        InitTask(this)
            .add(AppContext)
            .add(MMKVTask())
            .add(CacheUtil)
            .add(RetrofitTask())
            .run()
    }
}