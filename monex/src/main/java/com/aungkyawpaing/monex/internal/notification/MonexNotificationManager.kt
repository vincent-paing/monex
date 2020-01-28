package com.aungkyawpaing.monex.internal.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.collection.LongSparseArray
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.aungkyawpaing.monex.Monex
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.data.HttpTransaction.Status
import com.aungkyawpaing.monex.internal.data.HttpTransaction.Status.FAILED
import com.aungkyawpaing.monex.internal.data.HttpTransaction.Status.REQUESTED
import kotlin.math.absoluteValue

/**
 * Created by Vincent on 1/21/20
 */
internal class MonexNotificationManager constructor(
  private val context: Context
) {

  companion object {
    private const val CHANNEL_ID = "monex"
    private const val CHANNEL_NAME = "Monex Notification"
    private const val NOTIFICATION_ID = 14524
    private const val MAX_BUFFER_SIZE = 10

    private val transactionBuffer: LongSparseArray<HttpTransaction> = LongSparseArray()
    private var transactionCount = 0

    @Synchronized fun clearBuffer() {
      transactionBuffer.clear()
      transactionCount = 0
    }

    @Synchronized private fun addToBuffer(transaction: HttpTransaction) {
      if (transaction.getStatus() == Status.REQUESTED) {
        transactionCount++
      }
      transactionBuffer.put(transaction.id, transaction)
      if (transactionBuffer.size() > MAX_BUFFER_SIZE) {
        transactionBuffer.removeAt(0)
      }
    }

  }

  private val notificationManager by lazy {
    //    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    NotificationManagerCompat.from(context)
  }

  init {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      val notificationChannel = NotificationChannel(
        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
      )

      notificationManager.createNotificationChannel(notificationChannel)
    }

  }

  @Synchronized fun show(httpTransaction: HttpTransaction) {
    addToBuffer(httpTransaction)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
    val pendingIntent = PendingIntent.getActivity(context, 0, Monex.getLaunchIntent(context), 0)
    builder.setContentIntent(pendingIntent)
    builder.setLocalOnly(true)
    builder.setSmallIcon(R.drawable.monex_ic_notification_24dp)
    builder.color = ContextCompat.getColor(context, R.color.monex_primary)
    builder.setContentTitle(context.getString(R.string.monex_notification_title))

    val inboxStyle = NotificationCompat.InboxStyle()

    val totalSize = transactionBuffer.size()
    (totalSize - 1 downTo 0).forEach { index ->
      val count = (totalSize - 1 - index).absoluteValue
      if (count < MAX_BUFFER_SIZE) {
        val transaction = transactionBuffer.valueAt(index)
        if (count == 0) {
          builder.setContentText(getNotificationText(transaction))
        }
        inboxStyle.addLine(getNotificationText(transaction))
      }
    }
    builder.setAutoCancel(false)
    builder.setStyle(inboxStyle)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      builder.setSubText(transactionCount.toString())
    } else {
      builder.setNumber(transactionCount)
    }

    notificationManager.notify(NOTIFICATION_ID, builder.build());
  }

  private fun getNotificationText(transaction: HttpTransaction): CharSequence {
    val status = transaction.getStatus()

    return when (status) {
      FAILED -> {
        val spannableString = SpannableString("! ! ! ${transaction.path}")
        spannableString.setSpan(
          ForegroundColorSpan(ContextCompat.getColor(context, R.color.monex_notification_failed)),
          0,
          5,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString
      }
      REQUESTED -> {
        " . . . ${transaction.path}"
      }
      else -> {
        val responseCode = transaction.response?.responseCode ?: 0
        val spannableString = SpannableString("$responseCode\t${transaction.path}")

        if (responseCode < 200 || responseCode > 299) {
          spannableString.setSpan(
            ForegroundColorSpan(
              ContextCompat.getColor(
                context,
                R.color.monex_notification_error_response
              )
            ),
            0,
            responseCode.toString().length - 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
          )
        }

        spannableString
      }

    }
  }
}



