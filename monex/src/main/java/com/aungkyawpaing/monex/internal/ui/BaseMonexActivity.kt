package com.aungkyawpaing.monex.internal.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Created by Vincent on 1/21/20
 */
internal abstract class BaseMonexActivity<VB : ViewBinding> : AppCompatActivity() {

  protected val binding: VB by lazy {
    bindView()
  }

  abstract fun bindView(): VB

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == android.R.id.home) {
      onBackPressed()
    }
    return super.onOptionsItemSelected(item)
  }

}