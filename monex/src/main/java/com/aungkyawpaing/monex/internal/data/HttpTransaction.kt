package com.aungkyawpaing.monex.internal.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aungkyawpaing.monex.internal.helper.ByteFormatter
import com.aungkyawpaing.monex.internal.helper.HttpTransactionFormatter
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

/**
 * Data class that holds all the information of a http transaction
 */

@Entity(tableName = "transactions")
internal data class HttpTransaction(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(name = "requested_date_time") val requestedDateTime: LocalDateTime,
  @ColumnInfo(name = "took_duration") val tookDuration: Duration,

  @ColumnInfo(name = "method") val method: String,
  @ColumnInfo(name = "url") val url: String,
  @ColumnInfo(name = "host") val host: String,
  @ColumnInfo(name = "path") val path: String,
  @ColumnInfo(name = "scheme") val scheme: String,

  @ColumnInfo(name = "requestContentLength") val requestContentLength: Long?,
  @ColumnInfo(name = "requestContentType") val requestContentType: String,
  @ColumnInfo(name = "requestHeaders") val requestHeaders: List<HttpHeader>,
  @ColumnInfo(name = "requestBody") val requestBody: String,
  @ColumnInfo(
    name = "requestBodyIsPlainText",
    defaultValue = "true"
  ) val requestBodyIsPlainText: Boolean = true,

  @ColumnInfo(name = "error") val error: String? = null,
  @Embedded(prefix = "response_") val response: HttpTransactionResponse? = null,

  @ColumnInfo(name = "snippet_url") val snippetUrl: String? = null

) {

  enum class Status {
    REQUESTED,
    COMPLETED,
    FAILED
  }

  fun getStatus(): Status {
    if (error != null) {
      return Status.FAILED;
    } else if (response == null) {
      return Status.REQUESTED;
    } else {
      return Status.COMPLETED;
    }
  }

  fun getDurationAsString(): CharSequence {
    return "${this.tookDuration.toMillis()} ms"
  }

  fun getTotalSizeString(): String {
    val reqBytes = requestContentLength ?: 0
    val resBytes = if (response != null) response.responseContentLength ?: 0 else 0.toLong()
    return ByteFormatter.formatByte(reqBytes + resBytes)
  }

  fun getReadableRequestTime(): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.US)
    return requestedDateTime.format(dateTimeFormatter)
  }

  fun getReadableRequestDateTime(): String {
    val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    return requestedDateTime.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
  }

  fun isSSL(): Boolean {
    return scheme.toLowerCase() == "https";
  }

  fun getFormattedRequestBody(): String {
    return HttpTransactionFormatter.formatBody(requestBody, requestContentType)
  }

}