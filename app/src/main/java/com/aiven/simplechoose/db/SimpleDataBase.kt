package com.aiven.simplechoose.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aiven.simplechoose.app.task.TaskApp
import com.aiven.simplechoose.db.dao.InsertUpdateTestDao
import com.aiven.simplechoose.db.dao.TestPaperRecordDao
import com.aiven.simplechoose.db.entity.InsertUpdateTestEntity
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.db.migrations.Migration_2_3
import com.aiven.simplechoose.db.migrations.TestRecordMigration_1_2

@Database(
    entities = [
        TestPaperRecord::class,
        InsertUpdateTestEntity::class,
    ],
    version = 3,
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
                    .addMigrations(TestRecordMigration_1_2())
                    .addMigrations((Migration_2_3()))
                    //.allowMainThreadQueries()
                    .build()
        }

        fun getInstance() : SimpleDataBase = simpleDataBase
    }

    abstract fun testPaperRecordDao() : TestPaperRecordDao

    abstract fun insertUpdateTestDao(): InsertUpdateTestDao
}