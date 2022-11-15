package com.aiven.simplechoose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insert_update_test")
data class InsertUpdateTestEntity(
    @PrimaryKey
    val id: String = "",

    val name: String = "",

    val age: Int = 0,

    val updateTime: Long = 0
)
