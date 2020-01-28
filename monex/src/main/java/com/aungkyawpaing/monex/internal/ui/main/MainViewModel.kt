package com.aungkyawpaing.monex.internal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.data.HttpTransactionDao

/**
 * Created by Vincent on 1/21/20
 */
internal class MainViewModel constructor(transactionDao: HttpTransactionDao) : ViewModel() {

  val transactionListLiveData: LiveData<PagedList<HttpTransaction>> =
    transactionDao.getByDateOrdered().toLiveData(pageSize = 20)

}

