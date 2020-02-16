package com.aungkyawpaing.monex.internal.helper

import android.app.IntentService
import android.content.Intent
import com.aungkyawpaing.monex.internal.data.DbProvider
import com.aungkyawpaing.monex.internal.notification.MonexNotificationManager

internal class ClearTransactionService : IntentService(SERVICE_NAME) {

  companion object {
    private const val SERVICE_NAME = "monex_clear_transaction_service"
  }

  private val db by lazy {
    DbProvider.getDb(applicationContext)
  }

  private val transactionDao by lazy {
    db.httpTransactionDao()
  }

  override fun onHandleIntent(intent: Intent?) {
    transactionDao.deleteAll()
    MonexNotificationManager.clearBuffer()
    MonexNotificationManager(applicationContext).dismiss()
  }

}