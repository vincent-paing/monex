package com.aungkyawpaing.monex.internal.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.databinding.MonexActivityMainBinding
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.ui.BaseMonexActivity
import com.aungkyawpaing.monex.internal.ui.ViewModelFactory
import com.aungkyawpaing.monex.internal.ui.detail.TransactionDetailActivity
import com.aungkyawpaing.monex.internal.ui.main.TransactionRecyclerViewAdapter.OnTransactionItemClickListener
import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * Created by Vincent on 1/21/20
 */
internal class MainActivity : BaseMonexActivity<MonexActivityMainBinding>(),
  OnTransactionItemClickListener {

  private val transactionRecyclerViewAdapter by lazy {
    TransactionRecyclerViewAdapter(this)
  }

  override fun bindView(): MonexActivityMainBinding =
    MonexActivityMainBinding.inflate(layoutInflater)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidThreeTen.init(this)

    setSupportActionBar(binding.toolBar)
    supportActionBar?.title = getString(R.string.monex_name)
    supportActionBar?.subtitle = getApplicationName()

    binding.rvTransaction.apply {
      adapter = transactionRecyclerViewAdapter
      layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
      addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
    }

    val viewModel = ViewModelFactory(this)
      .create(MainViewModel::class.java)
    viewModel.transactionListLiveData.observe(this, Observer {
      transactionRecyclerViewAdapter.submitList(it)
    })
  }

  private fun getApplicationName(): String? {
    val applicationInfo = applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
  }

  override fun onItemClick(transaction: HttpTransaction) {
    val intent = TransactionDetailActivity.newIntent(this, transaction.id)
    startActivity(intent)
  }
}

