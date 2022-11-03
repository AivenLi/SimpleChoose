package com.aiven.simplechoose.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.aiven.simplechoose.db.entity.TestPaperRecord
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface TestPaperRecordDao {

    @Insert
    fun insert(testPaperRecord: TestPaperRecord): Completable

    @Query("DELETE FROM test_paper_record WHERE id = :id")
    fun deleteById(id: Long): Completable

    @Delete
    fun delete(testPaperRecord: TestPaperRecord): Completable

    @Query("SELECT * FROM test_paper_record LIMIT :page * :size, :size")
    fun selectByPage(size: Int, page: Int) : Observable<List<TestPaperRecord>>

    @Query("SELECT * FROM test_paper_record WHERE id = :id")
    fun selectById(id: Long): Single<TestPaperRecord>
}