package com.aiven.simplechoose.app.task.impl

import com.aiven.simplechoose.app.task.Task
import com.aiven.simplechoose.app.task.TaskApp
import com.tencent.mmkv.MMKV

/**
 * @author  : AivenLi
 * @date    : 2022/8/7 12:03
 * */
class MMKVTask: Task {
    override fun run(app: TaskApp) {
        MMKV.initialize(app)
    }
}