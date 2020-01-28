package com.aungkyawpaing.monex.internal.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Vincent on 1/21/20
 */
@JsonClass(generateAdapter = true)
internal data class HttpHeader(
  @Json(name = "name") val name: String,
  @Json(name = "value") val value: String
)