package com.aiven.simplechoose.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.aiven.simplechoose.db.entity.InsertUpdateTestEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface InsertUpdateTestDao {

    @Insert
    fun insert(data: List<InsertUpdateTestEntity>): Completable

    @Insert
    fun insert(data: InsertUpdateTestEntity): Completable

    @Insert
    fun insertSync(entity: InsertUpdateTestEntity): Unit

    @Update
    fun updateSync(entity: InsertUpdateTestEntity): Unit

    @Query("REPLACE INTO insert_update_test(id,name,age,updateTime, encodeKey) VALUES(:id,:name,:age,:updateTime, :encodeKey)")
    fun insertOrUpdate(
        id: String,
        name: String,
        age: Int,
        updateTime: Long,
        encodeKey: ByteArray?
    ): Unit

    @Update
    fun update(data: List<InsertUpdateTestEntity>): Completable

    @Query("SELECT * FROM insert_update_test")
    fun select(): Observable<List<InsertUpdateTestEntity>>

    @Query("SELECT * FROM insert_update_test WHERE id = :id")
    fun findById(id: String): Single<InsertUpdateTestEntity>

    @Query("SELECT * FROM insert_update_test WHERE id = :id")
    suspend fun findByIdCoroutine(id: String): InsertUpdateTestEntity?

    @Query("SELECT * FROM insert_update_test WHERE id = :id")
    fun findByIdSync(id: String): InsertUpdateTestEntity?
}