package com.aiven.simplechoose.app.task.impl

import com.aiven.simplechoose.app.task.Task
import com.aiven.simplechoose.app.task.TaskApp
import com.aiven.simplechoose.net.RetrofitUtil


/**
 * @author  : AivenLi
 * @date    : 2022/8/6 14:42
 * */
class RetrofitTask : Task {
    override fun run(app: TaskApp) {
        RetrofitUtil.run(app)
    }
}