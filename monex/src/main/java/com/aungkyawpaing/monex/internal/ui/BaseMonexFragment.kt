package com.aungkyawpaing.monex.internal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Created by Vincent on 1/21/20
 */
internal abstract class BaseMonexFragment<VB : ViewBinding> : Fragment() {

  protected val binding: VB get() = _binding!!

  private var _binding: VB? = null

  abstract fun bindView(inflater: LayoutInflater): VB

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = bindView(inflater)
    return binding.root
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }
}