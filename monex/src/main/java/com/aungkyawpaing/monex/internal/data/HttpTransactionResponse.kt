package com.aungkyawpaing.monex.internal.data

import androidx.room.ColumnInfo
import com.aungkyawpaing.monex.internal.helper.HttpTransactionFormatter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

/**
 * Data class that holds all the information of a http transaction
 */

internal data class HttpTransactionResponse(
  @ColumnInfo(name = "protocol") val protocol: String,

  @ColumnInfo(name = "response_date_time") val responseDateTime: LocalDateTime,
  @ColumnInfo(name = "responseCode") val responseCode: Int,
  @ColumnInfo(name = "responseMessage") val responseMessage: String,

  @ColumnInfo(name = "responseContentLength") val responseContentLength: Long?,
  @ColumnInfo(name = "responseContentType") val responseContentType: String,
  @ColumnInfo(name = "responseHeaders") val responseHeaders: List<HttpHeader>,
  @ColumnInfo(name = "responseBody") val responseBody: String,
  @ColumnInfo(
    name = "responseBodyIsPlainText",
    defaultValue = "true"
  ) val responseBodyIsPlainText: Boolean = true
) {

  fun getReadableResponseTime(): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.US)
    return responseDateTime.format(dateTimeFormatter)
  }

  fun getReadableResponseDateTime(): String {
    val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    return responseDateTime.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
  }

  fun getFormattedResponseBody(): String {
    return HttpTransactionFormatter.formatBody(responseBody, responseContentType)
  }

}