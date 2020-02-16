package com.aungkyawpaing.monex

import android.content.Context
import android.net.Uri
import android.util.Log
import com.aungkyawpaing.monex.internal.data.DbProvider
import com.aungkyawpaing.monex.internal.data.HttpHeader
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.data.HttpTransactionResponse
import com.aungkyawpaing.monex.internal.data.gitlab.GitlabConfigStore
import com.aungkyawpaing.monex.internal.decay.DataDecayManager
import com.aungkyawpaing.monex.internal.notification.MonexNotificationManager
import com.jakewharton.threetenabp.AndroidThreeTen
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.BufferedSource
import okio.GzipSource
import okio.buffer
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import java.io.EOFException
import java.nio.charset.Charset

/**
 * Created by Vincent on 1/21/20
 */
class MonexInterceptor constructor(
  private val context: Context,
  /**
   * Determine whether notification should be shown or not, set to false if you don't want to show notification.
   */
  private val showNotification: Boolean = true,
  /**
   * Gitlab access token for your personal account
   */
  private val gitlabConfig: MonexGitlabConfig? = null,
  /**
   * Decay Time, example, will delete everything older than 20 minutes..etc.
   * Put 0 to set it to NEVER
   */
  private val decayTimeInMiliSeconds: Long = DECAY_TIME_NEVER
) : Interceptor {

  companion object {
    const val DECAY_TIME_NEVER = 0L

    private const val LOG_TAG = "MonexInterceptor"
    private const val maxContentLength = 250000L
  }

  private val monexNotificationManager by lazy {
    MonexNotificationManager(context)
  }

  private val db by lazy {
    DbProvider.getDb(context)
  }

  private val transactionDao by lazy {
    db.httpTransactionDao()
  }

  private val dataDecayManager by lazy {
    DataDecayManager(context)
  }

  init {
    AndroidThreeTen.init(context)

    if (gitlabConfig != null) {
      val gitlabTokenStore = GitlabConfigStore(context)
      gitlabTokenStore.putGitlabToken(gitlabConfig.accessToken)
      gitlabTokenStore.putGitlabBaseUrl(gitlabConfig.baseUrl)
    }

    if (decayTimeInMiliSeconds > 0) {
      val decayDuration = Duration.ofMillis(decayTimeInMiliSeconds)
      dataDecayManager.decayDuration = decayDuration
    }

  }

  override fun intercept(chain: Chain): Response {
    /**
     * Clean up data
     */
    dataDecayManager.cleanUp()

    val request = chain.request()

    /**
     * Request stuffs
     */
    val requestBody = request.body
    val requestDateTime = LocalDateTime.now()
    val method = request.method
    val url = request.url.toString()
    val uri = Uri.parse(url)
    val host = uri.host ?: ""
    val path = uri.path + if (uri.query != null) "?" + uri.query else ""
    val scheme = uri.scheme ?: ""

    var requestHeaders: List<HttpHeader> = request.headers.map {
      HttpHeader(it.first, it.second)
    }

    var requestContentType: String? = null
    var contentLength: Long? = null
    if (requestBody != null) {
      requestContentType = requestBody.contentType()?.toString()
      if (requestBody.contentLength() != -1L) {
        contentLength = requestBody.contentLength()
      }
    }

    var isRequestBodyInPlainText = !bodyHasUnsupportedEncoding(request.headers)
    var httpTransactionRequestBody = ""
    if (requestBody != null && isRequestBodyInPlainText) {
      val source = getNativeSource(Buffer(), isBodyGzipped(request.headers))
      val buffer = source.buffer
      requestBody!!.writeTo(buffer)

      val contentType = requestBody.contentType()
      val charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8

      if (isPlaintext(buffer)) {
        httpTransactionRequestBody = readFromBuffer(buffer, charset)
      } else {
        isRequestBodyInPlainText = false

      }
    }

    var httpTransaction = HttpTransaction(
      id = 0L,
      requestedDateTime = requestDateTime,
      tookDuration = Duration.ZERO,
      method = method,
      url = url,
      host = host,
      path = path,
      scheme = scheme,
      requestBody = httpTransactionRequestBody,
      requestContentLength = contentLength,
      requestContentType = requestContentType ?: "",
      requestBodyIsPlainText = isRequestBodyInPlainText,
      requestHeaders = requestHeaders
    )

    val insertedId = transactionDao.insert(httpTransaction)
    httpTransaction = httpTransaction.copy(id = insertedId)
    showNotification(httpTransaction)
    /**
     * Response stuffs
     */
    val startTime = Instant.now()
    val response: Response
    try {
      response = chain.proceed(request)
    } catch (e: Exception) {
      val error = e.toString()
      httpTransaction = httpTransaction.copy(error = error)
      transactionDao.update(httpTransaction)
      showNotification(httpTransaction)
      throw e
    }

    val timeTaken = Duration.between(startTime, Instant.now())
    val responseDateTime = LocalDateTime.now()

    val responseBody = response.body
    requestHeaders = response.request.headers.map {
      HttpHeader(it.first, it.second)
    }
    val protocol = response.protocol.toString()
    val responseCode = response.code
    val responseMessage = response.message
    val responseHeaders = response.headers.map {
      HttpHeader(it.first, it.second)
    }

    var responseContentType: String? = null
    var responseContentLength: Long? = null
    if (responseBody != null) {
      responseContentType = responseBody.contentType()?.toString()
      if (responseBody.contentLength() != -1L) {
        responseContentLength = responseBody.contentLength()
      }
    }

    var isResponseBodyInPlainText = !bodyHasUnsupportedEncoding(response.headers)

    var httpTransactionResponseBody = ""

    if (response.promisesBody() && isResponseBodyInPlainText && responseBody != null) {
      val source = getNativeSource(response)
      source.request(Long.MAX_VALUE)
      val buffer = source.buffer

      val contentType = responseBody.contentType()
      val charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8

      if (isPlaintext(buffer)) {
        httpTransactionResponseBody = readFromBuffer(buffer.clone(), charset)
      } else {
        isResponseBodyInPlainText = false
      }
      responseContentLength = buffer.size
    }

    val httpTransactionResponse = HttpTransactionResponse(
      protocol = protocol,
      responseDateTime = responseDateTime,
      responseCode = responseCode,
      responseMessage = responseMessage,
      responseContentLength = responseContentLength,
      responseContentType = responseContentType ?: "",
      responseHeaders = responseHeaders,
      responseBody = httpTransactionResponseBody,
      responseBodyIsPlainText = isResponseBodyInPlainText
    )

    httpTransaction = httpTransaction.copy(
      requestHeaders = requestHeaders,
      response = httpTransactionResponse,
      tookDuration = timeTaken
    )

    transactionDao.update(httpTransaction)

    showNotification(httpTransaction)

    return response
  }

  private fun showNotification(httpTransaction: HttpTransaction) {
    if (showNotification) {
      monexNotificationManager.show(httpTransaction)
    }
  }

  private fun bodyHasUnsupportedEncoding(headers: Headers): Boolean {
    val contentEncoding = headers["Content-Encoding"]
    return contentEncoding != null &&
        !contentEncoding.equals("identity", ignoreCase = true) &&
        !contentEncoding.equals("gzip", ignoreCase = true)
  }

  private fun getNativeSource(
    input: BufferedSource,
    isGzipped: Boolean
  ): BufferedSource {
    return if (isGzipped) {
      val source = GzipSource(input)
      source.buffer()
    } else {
      input
    }
  }

  private fun isBodyGzipped(headers: Headers): Boolean {
    val contentEncoding = headers["Content-Encoding"]
    return "gzip".equals(contentEncoding, ignoreCase = true)
  }

  private fun getNativeSource(response: Response): BufferedSource {
    if (isBodyGzipped(response.headers)) {
      val source = response.peekBody(maxContentLength).source()
      if (source.buffer().size < maxContentLength) {
        return getNativeSource(source, true)
      } else {
        Log.w(LOG_TAG, "gzip encoded response was too long")
      }
    }
    return response.body!!.source()
  }

  /**
   * Returns true if the body in question probably contains human readable text. Uses a small sample
   * of code points to detect unicode control characters commonly used in binary file signatures.
   */
  private fun isPlaintext(buffer: Buffer): Boolean {
    return try {
      val prefix = Buffer()
      val byteCount = if (buffer.size < 64) buffer.size else 64.toLong()
      buffer.copyTo(prefix, 0, byteCount)
      for (i in 0..15) {
        if (prefix.exhausted()) {
          break
        }
        val codePoint = prefix.readUtf8CodePoint()
        if (Character.isISOControl(codePoint) && !Character.isWhitespace(
            codePoint
          )
        ) {
          return false
        }
      }
      true
    } catch (e: EOFException) {
      false // Truncated UTF-8 sequence.
    }
  }

  private fun readFromBuffer(buffer: Buffer, charset: Charset): String {
    val bufferSize = buffer.size
    val maxBytes = Math.min(bufferSize, maxContentLength)
    var body = ""
    try {
      body = buffer.readString(maxBytes, charset)
    } catch (e: EOFException) {
      body += context.getString(R.string.monex_body_unexpected_eof)
    }
    if (bufferSize > maxContentLength) {
      body += context.getString(R.string.monex_body_content_truncated)
    }
    return body
  }

}

