package com.aungkyawpaing.monex.internal.helper

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi.Builder
import okio.Buffer

/**
 * Created by Vincent on 1/22/20
 */
internal object JsonPrettifier {

  /***
   * Copied from https://github.com/square/moshi/issues/288#issuecomment-296533836
   */
  fun prettifyJsonString(json: String): String {
    val buffer = Buffer().writeUtf8(json)
    val jsonReader = JsonReader.of(buffer)
    val value = jsonReader.readJsonValue()
    val adapter: JsonAdapter<Any> =
      Builder().build().adapter(Any::class.java).indent("    ")
    val result = adapter.toJson(value)
    return result
  }
}