package com.aungkyawpaing.monex.internal.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.databinding.MonexActivityDetailBinding
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.helper.GitlabSnippetCreator
import com.aungkyawpaing.monex.internal.helper.GitlabSnippetCreator.Result.Failed
import com.aungkyawpaing.monex.internal.helper.GitlabSnippetCreator.Result.Success
import com.aungkyawpaing.monex.internal.helper.HttpTransactionFormatter
import com.aungkyawpaing.monex.internal.share.FileSharePlugin
import com.aungkyawpaing.monex.internal.ui.BaseMonexActivity
import com.aungkyawpaing.monex.internal.ui.MonexViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 1/22/20
 */
internal class TransactionDetailActivity : BaseMonexActivity<MonexActivityDetailBinding>() {

  companion object {
    private const val IE_TRANSACTION_ID = "id"

    fun newIntent(context: Context, transactionId: Long): Intent {
      val intent = Intent(context, TransactionDetailActivity::class.java)
      intent.putExtra(IE_TRANSACTION_ID, transactionId)
      return intent
    }

  }

  private val transactionId by lazy {
    intent.getLongExtra(IE_TRANSACTION_ID, 0)
  }

  private val fileSharePlugin by lazy {
    FileSharePlugin(this)
  }

  override fun bindView(): MonexActivityDetailBinding {
    return MonexActivityDetailBinding.inflate(layoutInflater)
  }

  private val viewPagerAdapter by lazy {
    TransactionDetailViewPagerAdapter()
  }

  private val viewModel by lazy {
    MonexViewModelFactory(this)
      .create(TransactionDetailViewModel::class.java)
  }

  private var transaction: HttpTransaction? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setSupportActionBar(binding.toolBar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    binding.viewPager.adapter = viewPagerAdapter
    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
      when (position) {
        0 -> tab.text = "Overview"
        1 -> tab.text = "Request"
        2 -> tab.text = "Response"
      }
    }.attach()



    viewModel.transactionLiveData(transactionId).observe(this, Observer {
      transaction = it
      supportActionBar?.title = "${it.method} ${it.path}"
      viewPagerAdapter.setItem(it)
    })
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.monex_menu_transaction_detail, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.monex_action_share_text -> {
        transaction?.let {
          shareText(HttpTransactionFormatter.formatShareText(this@TransactionDetailActivity, it))
        }
      }
      R.id.monex_action_share_gitlab_snippet -> {
        shareGitlabLink()
      }
      R.id.monex_action_share_file -> {
        transaction?.let {
          fileSharePlugin.share(it)
        }
      }
      R.id.monex_action_share_curl -> {
        transaction?.let {
          shareText(HttpTransactionFormatter.formatCUrlCommand(it))
        }
      }
    }

    return super.onOptionsItemSelected(item)
  }

  fun shareText(content: String) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, content)
    sendIntent.type = "text/plain"
    startActivity(Intent.createChooser(sendIntent, null))
  }

  private val scope = CoroutineScope(Dispatchers.Main)

  private val gitlabSnippetCreator by lazy {
    GitlabSnippetCreator(this)
  }

  fun shareGitlabLink() {
    transaction?.let { httpTransaction ->
      scope.launch {
        val result = gitlabSnippetCreator.createSnippet(httpTransaction)

        when (result) {
          is Success -> {
            shareText(result.snippetUrl)
          }
          Failed -> {
            Toast.makeText(
              this@TransactionDetailActivity,
              getString(R.string.monex_fail_create_snippet),
              Toast.LENGTH_SHORT
            ).show()
          }
        }

      }
    }

  }

}