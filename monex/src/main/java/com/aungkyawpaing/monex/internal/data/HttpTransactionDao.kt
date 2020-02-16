package com.aungkyawpaing.monex.internal.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.threeten.bp.LocalDateTime

@Dao
internal interface HttpTransactionDao {

  @Query("SELECT * FROM transactions")
  fun getAll(): List<HttpTransaction>

  @Query("SELECT * FROM transactions ORDER BY transactions.requested_date_time DESC")
  fun getByDateOrdered(): DataSource.Factory<Int, HttpTransaction>

  @Query("SELECT * FROM transactions WHERE transactions.id = :id")
  fun getById(id: Long): LiveData<HttpTransaction>

  @Insert
  fun insert(httpTransaction: HttpTransaction): Long

  @Update
  fun update(vararg httpTransaction: HttpTransaction)

  @Delete
  fun delete(httpTransaction: HttpTransaction)

  @Query("DELETE FROM transactions")
  fun deleteAll()

  @Query("DELETE FROM transactions WHERE transactions.requested_date_time < :timestamp")
  fun deleteByTimestamp(timestamp: LocalDateTime)

}