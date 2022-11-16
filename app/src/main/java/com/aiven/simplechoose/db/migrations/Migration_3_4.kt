package com.aiven.simplechoose.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration_3_4: Migration(3, 4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE insert_update_test ADD COLUMN encodeKey BLOB")
    }
}