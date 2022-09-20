package com.aiven.simplechoose.app.task


/**
 * 用于初始化任务
 * */
class InitTask(private val app: TaskApp) {

    /**
     * 存放初始化任务的容器
     * */
    private val tasks: MutableList<Task> = mutableListOf()

    /**
     * 添加任务
     *
     * @param task 需要执行的任务
     * */
    fun add(task: Task) : InitTask {
        tasks.add(task)
        return this
    }

    /**
     * 执行任务
     * */
    fun run() {
        for (task in tasks) {
            task.run(app)
        }
    }
}