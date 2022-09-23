package com.aiven.simplechoose.app.task.impl

import com.aiven.simplechoose.app.task.Task
import com.aiven.simplechoose.app.task.TaskApp
import com.aiven.simplechoose.db.SimpleDataBase

class DataBaseTask: Task {

    override fun run(app: TaskApp) {
        SimpleDataBase.init(app)
    }
}