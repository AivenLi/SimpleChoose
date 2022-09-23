package com.aiven.simplechoose.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aiven.simplechoose.db.entity.TestPaperRecord

@Dao
interface TestPaperRecordDao {

    @Insert
    fun insert(testPaperRecord: TestPaperRecord)

    @Query("DELETE FROM test_paper_record WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM test_paper_record LIMIT :page * :size, :size")
    fun selectByPage(size: Int, page: Int) : List<TestPaperRecord>
}