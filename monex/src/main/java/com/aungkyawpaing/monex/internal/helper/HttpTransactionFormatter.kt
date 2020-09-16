package com.aungkyawpaing.monex.internal.helper

import android.content.Context
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.internal.data.HttpHeader
import com.aungkyawpaing.monex.internal.data.HttpTransaction

/**
 * helper class that convertto different format
 */
internal object HttpTransactionFormatter {

//  fun formatHttpTransactionBody(httpTransaction: HttpTransaction): String {
//    val stringBuilder = StringBuilder()
//
//    httpTransaction.forEach {
//      stringBuilder.append("<b>${it.name}</b>: ${it.value}\n")
//    }
//    return stringBuilder.toString()
//  }

  fun formatHeadersToHttpFormat(httpHeader: List<HttpHeader>): String {
    val stringBuilder = StringBuilder()

    httpHeader.forEach {
      stringBuilder.append("<b>${it.name}</b>: ${it.value} <br/>")
    }
    return stringBuilder.toString()
  }

  fun formatHeaders(httpHeader: List<HttpHeader>): String {
    val stringBuilder = StringBuilder()

    httpHeader.forEach {
      stringBuilder.append("${it.name}: ${it.value} \n")
    }
    return stringBuilder.toString()
  }

  fun formatBody(body: String, contentType: String): String {
    return if (contentType.toLowerCase().contains("json")) {
      JsonPrettifier.prettifyJsonString(body)
    } else if (contentType.toLowerCase().contains("xml")) {
//      FormatUtils.formatXml(body)
      //TODO: XML SUPPORT
      body
    } else {
      body
    }
  }

  fun formatShareText(context: Context, transaction: HttpTransaction): String {
    val stringBuilder = StringBuilder()

    val response = transaction.response
    stringBuilder.append("${context.getString(R.string.monex_detail_title_url)} : ${transaction.url} \n")
    stringBuilder.append("${context.getString(R.string.monex_detail_title_method)} : ${transaction.method} \n")
    stringBuilder.append(
      "${context.getString(R.string.monex_detail_title_protocol)} : ${response?.protocol ?: ""} \n"
    )

    val status = transaction.getStatus()
    if (status == HttpTransaction.Status.FAILED) {
      stringBuilder.append(
        "${context.getString(R.string.monex_detail_title_response)} : ${
          transaction.error
            ?: ""
        } \n"
      )
    } else if (response != null) {
      stringBuilder.append("${context.getString(R.string.monex_detail_title_response)} : ${response.responseCode} ${response.responseMessage} \n")
    } else {
      stringBuilder.append("${context.getString(R.string.monex_detail_title_response)} : \n")
    }

    stringBuilder.append(
      "${context.getString(R.string.monex_detail_title_ssl)} : ${
        if (transaction.isSSL()) context.getString(R.string.monex_yes)
        else context.getString(R.string.monex_no)
      }\n"
    )

    stringBuilder.append("\n")

    stringBuilder.append("${context.getString(R.string.monex_detail_title_request_time)} : ${transaction.getReadableRequestDateTime()} \n")
    stringBuilder.append("${context.getString(R.string.monex_detail_title_response_time)} : ${response?.getReadableResponseDateTime()} \n")
    stringBuilder.append("${context.getString(R.string.monex_detail_title_duration)} : ${transaction.getDurationAsString()} \n")

    stringBuilder.append("\n")

    stringBuilder.append("${context.getString(R.string.monex_detail_title_request_size)} : ${transaction.requestContentLength?.formatByte()} \n")
    stringBuilder.append("${context.getString(R.string.monex_detail_title_response_size)} : ${response?.responseContentLength?.formatByte()} \n")
    stringBuilder.append("${context.getString(R.string.monex_detail_title_total_size)} : ${transaction.getTotalSizeString()} \n")

    stringBuilder.append("\n")

    stringBuilder.append("----------${context.getString(R.string.monex_request)}----------\n")
    if (transaction.requestHeaders.isNotEmpty()) {
      stringBuilder.append("${formatHeaders(transaction.requestHeaders)} \n")
    }

    if (transaction.requestBodyIsPlainText) {
      stringBuilder.append("${transaction.getFormattedRequestBody()} \n")
    } else {
      stringBuilder.append("${context.getString(R.string.monex_omitted_body)} \n")
    }
    stringBuilder.append("\n")

    stringBuilder.append("----------${context.getString(R.string.monex_response)}----------\n")
    if (response != null) {
      if (response.responseHeaders.isNotEmpty()) {
        stringBuilder.append("${formatHeaders(response.responseHeaders)} \n")
      }

      stringBuilder.append("\n")
      if (response.responseBodyIsPlainText) {
        stringBuilder.append("${response.getFormattedResponseBody()} \n")
      } else {
        stringBuilder.append("${context.getString(R.string.monex_omitted_body)} \n")
      }
    }
    stringBuilder.append("\n")

    return stringBuilder.toString()

  }

  /***
   * Format the transaction into reproducible cUrl command
   * @param httpTransaction A [HttpTransaction] object to be formatted
   * @return a curl command
   */
  fun formatCUrlCommand(httpTransaction: HttpTransaction): String {
    val stringBuilder = StringBuilder()
    var compressed = false
    stringBuilder.append("curl")
    stringBuilder.append(" -X ${httpTransaction.method}")
    httpTransaction.requestHeaders.forEach { header ->
      if ("Accept-Encoding".equals(header.name, ignoreCase = true) &&
        "gzip".equals(header.value, ignoreCase = true)
      ) {
        compressed = true
      }
      stringBuilder.append(" -H \"${header.name}: ${header.value}\"")
    }

    val requestBody = httpTransaction.requestBody
    if (requestBody.isNotEmpty()) {
      stringBuilder.append(" --data $'${requestBody.replace("\n", "\\n")}'")
    }

    if (compressed) {
      stringBuilder.append(" --compressed")
    }

    stringBuilder.append(" ")

    stringBuilder.append(httpTransaction.url)

    return stringBuilder.toString()
  }

}