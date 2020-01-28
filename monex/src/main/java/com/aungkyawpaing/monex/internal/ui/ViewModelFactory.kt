package com.aungkyawpaing.monex.internal.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aungkyawpaing.monex.internal.data.DbProvider
import com.aungkyawpaing.monex.internal.data.HttpTransactionDao
import com.aungkyawpaing.monex.internal.ui.detail.TransactionDetailViewModel
import com.aungkyawpaing.monex.internal.ui.main.MainViewModel

/**
 * Created by Vincent on 1/21/20
 */
internal class ViewModelFactory constructor(
  val context: Context
) : ViewModelProvider.Factory {

  private val db = DbProvider.getDb(context)

  private val transactionDao =
    db.httpTransactionDao()

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {

    if (modelClass.name == MainViewModel::class.java.name) {
      return modelClass.getConstructor(HttpTransactionDao::class.java).newInstance(transactionDao)
    } else if (modelClass.name == TransactionDetailViewModel::class.java.name) {
      return modelClass.getConstructor(HttpTransactionDao::class.java).newInstance(transactionDao)
    }

    throw IllegalStateException()
  }
}

