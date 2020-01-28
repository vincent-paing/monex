package com.aungkyawpaing.monex

/**
 * Created by Vincent on 1/24/20
 */
data class MonexGitlabConfig(
  /**
   * Your access token, Follow this @see [doc](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html]) to get one
   */
  val accessToken: String,
  /**
   * Base Url that include up to "/" without the api url for example use "https://gitlab.com/", NOT "https://gitlab.com/api/v4/"
   */
  val baseUrl: String = "https://gitlab.com/"
)