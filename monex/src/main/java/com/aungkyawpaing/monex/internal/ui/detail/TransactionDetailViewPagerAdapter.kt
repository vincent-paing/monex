package com.aungkyawpaing.monex.internal.ui.detail

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.databinding.MonexItemTransactionOverviewBinding
import com.aungkyawpaing.monex.databinding.MonexItemTransactionRequestBinding
import com.aungkyawpaing.monex.databinding.MonexItemTransactionResponseBinding
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.helper.HttpTransactionFormatter
import com.aungkyawpaing.monex.internal.helper.formatByte
import com.aungkyawpaing.monex.internal.ui.detail.TransactionDetailViewPagerAdapter.TransactionDetailViewHolder
import com.aungkyawpaing.monex.internal.ui.detail.TransactionDetailViewPagerAdapter.TransactionDetailViewHolder.OverviewViewHolder
import com.aungkyawpaing.monex.internal.ui.detail.TransactionDetailViewPagerAdapter.TransactionDetailViewHolder.RequestViewHolder
import com.aungkyawpaing.monex.internal.ui.detail.TransactionDetailViewPagerAdapter.TransactionDetailViewHolder.ResponseViewHolder

/**
 * Created by Vincent on 1/22/20
 */
internal class TransactionDetailViewPagerAdapter :
  RecyclerView.Adapter<TransactionDetailViewHolder>() {

  companion object {

    private val VIEW_TYPE_OVERVIEW = 0
    private val VIEW_TYPE_REQUEST = 1
    private val VIEW_TYPE_RESPONSE = 2
  }

  private var _transaction: HttpTransaction? = null

  private val transaction get() = _transaction!!

  fun setItem(transaction: HttpTransaction) {
    this._transaction = transaction
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionDetailViewHolder {
    when (viewType) {
      VIEW_TYPE_OVERVIEW -> {
        val view = LayoutInflater.from(parent.context)
          .inflate(R.layout.monex_item_transaction_overview, parent, false)
        return OverviewViewHolder(view)
      }
      VIEW_TYPE_REQUEST -> {
        val view = LayoutInflater.from(parent.context)
          .inflate(R.layout.monex_item_transaction_request, parent, false)
        return RequestViewHolder(view)
      }
      VIEW_TYPE_RESPONSE -> {
        val view = LayoutInflater.from(parent.context)
          .inflate(R.layout.monex_item_transaction_response, parent, false)
        return ResponseViewHolder(view)
      }

    }
    throw IllegalStateException()
  }

  override fun getItemCount(): Int {
    if (_transaction == null) return 0
    else return 3
  }

  override fun getItemViewType(position: Int): Int {
    when (position) {
      0 -> return VIEW_TYPE_OVERVIEW
      1 -> return VIEW_TYPE_REQUEST
      2 -> return VIEW_TYPE_RESPONSE
    }
    throw IllegalStateException()
  }

  override fun onBindViewHolder(holder: TransactionDetailViewHolder, position: Int) {
    when (holder) {
      is OverviewViewHolder -> {
        holder.binding.apply {
          tvUrl.text = transaction.url
          tvMethod.text = transaction.method

          tvSSL.text = if (transaction.isSSL()) {
            "YES"
          } else {
            "NO"
          }

          tvRequestTime.text = transaction.getReadableRequestDateTime()
          tvRequestSize.text = (transaction.requestContentLength ?: 0).formatByte()

          val response = transaction.response
          if (response != null) {
            tvProtocol.text = response.protocol
            tvResponse.text = response.responseCode.toString()
            tvResponseTime.text = response.getReadableResponseDateTime()
            tvDuration.text = transaction.getDurationAsString()
            tvResponseSize.text = (response.responseContentLength ?: 0).formatByte()
          }

          tvTotalSize.text = transaction.getTotalSizeString()

          if (transaction.error != null) {
            tvError.visibility = View.VISIBLE
            tvError.text = transaction.error.toString()
          } else {
            tvError.visibility = View.GONE
          }
        }
      }
      is RequestViewHolder -> {
        holder.binding.apply {
          tvRequestInfo.text =
            Html.fromHtml(HttpTransactionFormatter.formatHeadersToHttpFormat(transaction.requestHeaders))
          tvRequestBody.text = transaction.getFormattedRequestBody()
        }
      }
      is ResponseViewHolder -> {
        holder.binding.apply {
          val response = transaction.response

          if (response != null) {
            tvResponseInfo.text =
              Html.fromHtml(HttpTransactionFormatter.formatHeadersToHttpFormat(response.responseHeaders))

            tvResponseBody.text = response.getFormattedResponseBody()
          }
        }
      }
    }
  }

  sealed class TransactionDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class OverviewViewHolder(itemView: View) : TransactionDetailViewHolder(itemView) {
      val binding = MonexItemTransactionOverviewBinding.bind(itemView)
    }

    class RequestViewHolder(itemView: View) : TransactionDetailViewHolder(itemView) {
      val binding = MonexItemTransactionRequestBinding.bind(itemView)
    }

    class ResponseViewHolder(itemView: View) : TransactionDetailViewHolder(itemView) {
      val binding = MonexItemTransactionResponseBinding.bind(itemView)
    }
  }

}