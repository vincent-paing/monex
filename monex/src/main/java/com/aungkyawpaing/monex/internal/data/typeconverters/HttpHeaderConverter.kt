package com.aungkyawpaing.monex.internal.data.typeconverters

import androidx.room.TypeConverter
import com.aungkyawpaing.monex.internal.data.HttpHeader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * Created by Vincent on 1/21/20
 */
internal class HttpHeaderConverter {

  private val moshi = Moshi.Builder()
    .build()

  private val listType = Types.newParameterizedType(List::class.java, HttpHeader::class.java)
  private val listAdapter = moshi.adapter<List<HttpHeader>>(listType)

  @TypeConverter
  fun fromHttpHeaderList(value: List<HttpHeader>): String {
    return listAdapter.toJson(value)!!
  }

  @TypeConverter
  fun toHttpHeaderList(value: String): List<HttpHeader> {
    return listAdapter.fromJson(value)!!
  }

}