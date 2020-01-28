package com.aungkyawpaing.monex.internal.data.gitlab

import android.content.Context
import androidx.core.content.edit

/**
 * Created by Vincent on 1/23/20
 */
internal class GitlabConfigStore constructor(context: Context) {

  companion object {
    private val KEY_GITLAB_TOKEN = "gitlab_token"
    private val KEY_GITLAB_BASE_URL = "gitlab_base_url"
  }

  private val sharedPreferences = context.getSharedPreferences("monex", Context.MODE_PRIVATE)

  fun putGitlabToken(token: String) {
    sharedPreferences.edit {
      putString(KEY_GITLAB_TOKEN, token)
    }
  }

  fun getGitlabToken(): String {
    return sharedPreferences.getString(KEY_GITLAB_TOKEN, "") ?: ""
  }

  fun putGitlabBaseUrl(gitlabBaseUrl: String) {
    sharedPreferences.edit {
      putString(KEY_GITLAB_BASE_URL, gitlabBaseUrl)
    }
  }

  fun getGitlabBaseUrl(): String {
    return sharedPreferences.getString(KEY_GITLAB_BASE_URL, "https://gitlab.com/")
      ?: "https://gitlab.com/"
  }

}

