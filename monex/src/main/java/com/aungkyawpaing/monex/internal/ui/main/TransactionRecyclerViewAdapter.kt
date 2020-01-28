package com.aungkyawpaing.monex.internal.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.databinding.MonexListItemTransactionBinding
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.ui.main.TransactionRecyclerViewAdapter.TransactionRecyclerViewHolder

/**
 * Created by Vincent on 1/21/20
 */
internal class TransactionRecyclerViewAdapter(
  private val onTransactionItemClickListener: OnTransactionItemClickListener
) :
  ListAdapter<HttpTransaction, TransactionRecyclerViewHolder>(
    object : DiffUtil.ItemCallback<HttpTransaction>() {
      override fun areItemsTheSame(oldItem: HttpTransaction, newItem: HttpTransaction): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: HttpTransaction, newItem: HttpTransaction): Boolean {
        return oldItem == newItem
      }

    }
  ) {

  internal interface OnTransactionItemClickListener {

    fun onItemClick(transaction: HttpTransaction)

  }

  internal class TransactionRecyclerViewHolder(
    itemView: View,
    private val viewHolderClickListener: ViewHolderClickListener
  ) : ViewHolder(itemView) {
    val binding = MonexListItemTransactionBinding.bind(itemView)

    init {
      itemView.setOnClickListener {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
          viewHolderClickListener.onItemClick(position)
        }
      }
    }
  }

  private val onItemClickListener = object : ViewHolderClickListener {
    override fun onItemClick(position: Int) {
      onTransactionItemClickListener.onItemClick(getItem(position))
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionRecyclerViewHolder {
    val itemView = LayoutInflater.from(parent.context)
      .inflate(R.layout.monex_list_item_transaction, parent, false)
    return TransactionRecyclerViewHolder(itemView, onItemClickListener)
  }

  override fun onBindViewHolder(holder: TransactionRecyclerViewHolder, position: Int) {
    val httpTransaction = getItem(position)
    val status = httpTransaction.getStatus()

    holder.binding.apply {
      if (status == HttpTransaction.Status.COMPLETED && httpTransaction.response != null) {
        tvResponseCode.text = httpTransaction.response.responseCode.toString()
        tvDuration.text = httpTransaction.getDurationAsString()
        tvSize.text = httpTransaction.getTotalSizeString();
      } else {
        tvResponseCode.text = ""
        tvDuration.text = ""
        tvSize.text = ""
      }

      if (status == HttpTransaction.Status.FAILED) {
        tvResponseCode.text = "!!!"
      }

      tvPath.text = "${httpTransaction.method} ${httpTransaction.path}"
      tvHost.text = httpTransaction.host

      tvMetaDataStartTime.text = httpTransaction.getReadableRequestTime()

    }
  }
}

