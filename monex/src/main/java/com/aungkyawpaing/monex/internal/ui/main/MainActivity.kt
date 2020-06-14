package com.aungkyawpaing.monex.internal.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch

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

  private val viewModel by lazy {
    ViewModelFactory(this)
      .create(MainViewModel::class.java)
  }

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


    viewModel.transactionListLiveData.observe(this, Observer {
      lifecycleScope.launch {
        transactionRecyclerViewAdapter.submitData(it)
      }
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

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.monex_menu_home, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.monex_action_delete -> {
        viewModel.deleteAll()

      }
    }
    return super.onOptionsItemSelected(item)
  }
}

