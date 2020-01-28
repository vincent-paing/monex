package com.aungkyawpaing.monex.internal.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.data.HttpTransactionDao

/**
 * Created by Vincent on 1/22/20
 */
internal class TransactionDetailViewModel constructor(
  private val transactionDao: HttpTransactionDao
) : ViewModel() {

  fun transactionLiveData(transactionId: Long): LiveData<HttpTransaction> {
    return transactionDao.getById(transactionId)
  }

}