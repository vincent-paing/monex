package com.aungkyawpaing.monex.internal.helper

import android.content.Context
import com.aungkyawpaing.monex.R
import com.aungkyawpaing.monex.internal.data.DbProvider
import com.aungkyawpaing.monex.internal.data.HttpTransaction
import com.aungkyawpaing.monex.internal.data.gitlab.GitlabConfigStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException

/**
 * Helper class that create gitlab snippet
 */
internal class GitlabSnippetCreator(
  private val context: Context
) {

  private val gitlabConfigStore = GitlabConfigStore(context)

  private val okHttpClient = OkHttpClient.Builder().build()

  private val httpTransactionDao = DbProvider.getDb(context).httpTransactionDao()

  companion object {
    private const val FILE_NAME = "data.txt"
    private const val FILE_DESCRIPTION = "Monex automatically generated content"
    /**
     * Set to internal for default, maybe change it to configurable later if needed?
     */
    private const val visibility = "internal"
  }

  suspend fun checkIfSnippetExist(httpTransaction: HttpTransaction): Boolean {
    return withContext(Dispatchers.IO) {
      val snippetUrl = httpTransaction.snippetUrl ?: return@withContext false
      val snippetId = snippetUrl.substringAfterLast("/")

      val url = "${gitlabConfigStore.getGitlabBaseUrl()}api/v4/snippets/${snippetId}"
      val accessToken = gitlabConfigStore.getGitlabToken()

      val requestBuilder = Request.Builder()
      requestBuilder.url(url)
      requestBuilder.header("PRIVATE-TOKEN", accessToken)
      requestBuilder.header("Content-Type", "application/json")
      requestBuilder.get()

      val call = okHttpClient.newCall(requestBuilder.build())
      val response = call.execute()

      if (response.isSuccessful) {
        return@withContext true
      } else if (response.code == 404) {
        return@withContext false
      }

      throw IOException("Error getting status")
    }
  }

  suspend fun createSnippet(httpTransaction: HttpTransaction): Result {
    return withContext(Dispatchers.IO) {

      val doesSnippetExistAlready: Boolean
      try {
        doesSnippetExistAlready = checkIfSnippetExist(httpTransaction)
      } catch (exception: Exception) {
        return@withContext Result.Failed
      }

      if (doesSnippetExistAlready) {
        return@withContext Result.Success(httpTransaction.snippetUrl!!)
      }

      val url = "${gitlabConfigStore.getGitlabBaseUrl()}api/v4/snippets"
      val accessToken = gitlabConfigStore.getGitlabToken()

      val requestBuilder = Request.Builder()
      requestBuilder.url(url)
      requestBuilder.header("PRIVATE-TOKEN", accessToken)
      requestBuilder.header("Content-Type", "application/json")

      val title = formatGitlabSnippetTitle(httpTransaction)
      val content = getFileContent(httpTransaction)

      val requestBodyBuilder = FormBody.Builder()
        .add("title", title)
        .add("file_name", FILE_NAME)
        .add("description", FILE_DESCRIPTION)
        .add("visibility", visibility)
        .add("content", content)

      requestBuilder.post(requestBodyBuilder.build())
      val request = requestBuilder.build()

      val call = okHttpClient.newCall(request)

      try {
        val response = call.execute()
        if (response.isSuccessful) {
          val jsonObject = JSONObject(response.body!!.string())
          val snippetUrl = jsonObject.getString("web_url")

          httpTransactionDao.update(httpTransaction.copy(snippetUrl = snippetUrl))
          return@withContext Result.Success(snippetUrl)
        } else {
          return@withContext Result.Failed
        }
      } catch (exception: Exception) {
        return@withContext Result.Failed
      }
    }
  }

  private fun formatGitlabSnippetTitle(httpTransaction: HttpTransaction): String {
    return "${context.getApplicationName()} ${httpTransaction.method} ${httpTransaction.path} ${LocalDateTime.now().format(
      DateTimeFormatter.ISO_DATE_TIME
    )}"
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

  sealed class Result {
    data class Success(val snippetUrl: String) : Result()
    object Failed : Result()
  }
}