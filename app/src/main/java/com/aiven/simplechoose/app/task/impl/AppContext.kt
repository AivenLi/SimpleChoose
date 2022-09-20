package com.aiven.simplechoose.app.task.impl

import com.aiven.simplechoose.app.task.Task
import com.aiven.simplechoose.app.task.TaskApp

object AppContext: Task {

    var context: TaskApp? = null

    override fun run(app: TaskApp) {
        context = app
    }
}