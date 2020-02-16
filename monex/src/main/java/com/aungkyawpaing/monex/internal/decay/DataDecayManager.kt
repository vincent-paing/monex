package com.aungkyawpaing.monex.internal.decay

import android.content.Context
import com.aungkyawpaing.monex.internal.data.DbProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime

/**
 * Handles the decay logic
 */
internal class DataDecayManager constructor(
  context: Context
) {

  private val db by lazy {
    DbProvider.getDb(context)
  }

  private val transactionDao by lazy {
    db.httpTransactionDao()
  }

  var decayDuration: Duration? = null

  fun cleanUp() {
    CoroutineScope(Dispatchers.Main).launch {

      withContext(Dispatchers.IO) {
        if (decayDuration != null) {
          //Minus the duration to get the timestamp to delete with
          val dateTime = LocalDateTime.now().minus(decayDuration)

          transactionDao.deleteByTimestamp(dateTime)
        }
      }
    }
  }

}