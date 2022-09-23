package com.aiven.simplechoose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_paper_record")
data class TestPaperRecord(
    @PrimaryKey(autoGenerate = true)
    val id        : Long = 0,
    val title     : String,
    val timestamp : Long,
    val score     : Float,
    val jsonStr   : String
)