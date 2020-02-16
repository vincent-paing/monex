package com.aungkyawpaing.monex

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

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
  private val decayTimeInMiliSeconds: Long = 0
) : Interceptor {

  override fun intercept(chain: Chain): Response {
    return chain.proceed(chain.request())
  }

}

