package com.aungkyawpaing.monex.internal.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.aungkyawpaing.monex.internal.data.HttpTransactionDao
import com.aungkyawpaing.monex.internal.notification.MonexNotificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 1/21/20
 */
internal class MainViewModel constructor(
  private val transactionDao: HttpTransactionDao
) : ViewModel() {

  val transactionListLiveData = Pager(
    PagingConfig(pageSize = 20)
  ) {
    transactionDao.getByDateOrdered()
  }.flow.asLiveData(context = viewModelScope.coroutineContext)


  fun deleteAll() {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        transactionDao.deleteAll()
      }
      MonexNotificationManager.clearBuffer()
    }

  }

}

