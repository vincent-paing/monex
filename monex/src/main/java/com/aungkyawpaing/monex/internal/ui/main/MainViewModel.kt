package com.aungkyawpaing.monex.internal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.data.HttpTransactionDao
import com.aungkyawpaing.monex.internal.notification.MonexNotificationManager
import com.aungkyawpaing.monex.internal.notification.MonexNotificationManager.Companion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 1/21/20
 */
internal class MainViewModel constructor(
  private val transactionDao: HttpTransactionDao
) : ViewModel() {

  val transactionListLiveData: LiveData<PagedList<HttpTransaction>> =
    transactionDao.getByDateOrdered().toLiveData(pageSize = 20)

  fun deleteAll() {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        transactionDao.deleteAll()
      }
      MonexNotificationManager.clearBuffer()
    }

  }

}

