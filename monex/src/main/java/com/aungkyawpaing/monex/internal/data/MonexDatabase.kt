package com.aungkyawpaing.monex.internal.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aungkyawpaing.monex.internal.data.typeconverters.HttpHeaderConverter
import com.aungkyawpaing.monex.internal.data.typeconverters.ThreeTenConverter

@Database(entities = [HttpTransaction::class], version = 1)
@TypeConverters(ThreeTenConverter::class, HttpHeaderConverter::class)
internal abstract class MonexDatabase : RoomDatabase() {

  abstract fun httpTransactionDao(): HttpTransactionDao

}
