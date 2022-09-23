package com.aiven.simplechoose.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aiven.simplechoose.app.task.TaskApp
import com.aiven.simplechoose.db.dao.TestPaperRecordDao
import com.aiven.simplechoose.db.entity.TestPaperRecord

@Database(
    entities = [
        TestPaperRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SimpleDataBase : RoomDatabase() {

    companion object {
        private lateinit var simpleDataBase: SimpleDataBase
        private var init = false
        fun init(app: TaskApp) {
            if (init) {
                return
            }
            init = true
            simpleDataBase =
                Room.databaseBuilder(
                    app,
                    SimpleDataBase::class.java,
                    "SIMPLE_CHOOSE_DB"
                )
                    .allowMainThreadQueries()
                    .build()
        }

        fun getInstance() : SimpleDataBase = simpleDataBase
    }

    abstract fun testPaperRecordDao() : TestPaperRecordDao
}