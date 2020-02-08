package com.aungkyawpaing.monex.internal.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.internal.data.DbProvider
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.helper.FileHelper
import com.aungkyawpaing.monex.internal.helper.HttpTransactionFormatter
import com.aungkyawpaing.monex.internal.helper.getApplicationName
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.net.URLConnection

internal class FileSharePlugin constructor(
  private val context: Context
) {

  companion object {
    private val DIRECTORY_NAME = "monex_share_texts"
  }

  private val httpTransactionDao = DbProvider.getDb(context).httpTransactionDao()

  private val parentDirectory =
    File(context.cacheDir.path + File.separator + DIRECTORY_NAME + File.separator)

  fun share(httpTransaction: HttpTransaction) {

    val fileName = getFileName(httpTransaction)
    val file = File(parentDirectory, fileName)
    FileHelper.writeToFile(getFileContent(httpTransaction), file)

    if (file.exists()) {

      val fileUri = FileProvider.getUriForFile(context, "com.aungkyawpaing.monex.provider", file)

      val intent = Intent(Intent.ACTION_SEND)
      intent.apply {
        type = URLConnection.guessContentTypeFromName(fileName)
        putExtra(Intent.EXTRA_STREAM, fileUri)
      }

      context.startActivity(Intent.createChooser(intent, "Share"))
    }
  }

  private fun getFileName(httpTransaction: HttpTransaction): String {
    return "${context.getApplicationName()} ${httpTransaction.method} ${httpTransaction.path} ${LocalDateTime.now().format(
      DateTimeFormatter.ISO_DATE_TIME
    )}.txt"
  }

  private fun getFileContent(httpTransaction: HttpTransaction): String {
    val shareText = HttpTransactionFormatter.formatShareText(context, httpTransaction)
    val curlCommand = HttpTransactionFormatter.formatCUrlCommand(httpTransaction)

    val stringBuilder = StringBuilder(shareText.length)

    stringBuilder.append(shareText)
    stringBuilder.append("\n")
    stringBuilder.append("----------${context.getString(R.string.monex_curl)}----------\n")
    stringBuilder.append(curlCommand)

    return stringBuilder.toString()
  }

}