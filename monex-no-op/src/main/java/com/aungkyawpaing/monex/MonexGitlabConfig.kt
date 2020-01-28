package com.aungkyawpaing.monex

/**
 * Created by Vincent on 1/24/20
 */
data class MonexGitlabConfig(
  val accessToken: String,
  val baseUrl: String = "https://gitlab.com/"
)