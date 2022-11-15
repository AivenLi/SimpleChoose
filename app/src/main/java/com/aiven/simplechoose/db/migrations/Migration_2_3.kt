package com.aiven.simplechoose.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration_2_3: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS insert_update_test(" +
                    "id TEXT NOT NULL PRIMARY KEY," +
                    "name TEXT NOT NULL DEFAULT \"\"," +
                    "age INTEGER NOT NULL DEFAULT 0," +
                    "updateTime INTEGER NOT NULL DEFAULT 0" +
                    ")"
        )
    }
}