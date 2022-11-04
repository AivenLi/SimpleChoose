package com.aiven.simplechoose.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class TestRecordMigration_1_2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE test_paper_record ADD COLUMN usetime INTEGER NOT NULL DEFAULT 0")
    }
}