package com.aungkyawpaing.monex.internal.data

import android.content.Context
import androidx.room.Room

internal object DbProvider {

  private var db: MonexDatabase? = null

  fun getDb(context: Context): MonexDatabase {
    if (db == null) {
      db = Room.databaseBuilder(
        context,
        MonexDatabase::class.java,
        "monex.db"
      ).build()
    }

    return db!!
  }
}