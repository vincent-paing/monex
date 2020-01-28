package com.aungkyawpaing.monex.internal.helper

import android.content.Context

/**
 * Created by Vincent on 1/24/20
 */
fun Context.getApplicationName(): String {
  val applicationInfo = applicationInfo
  val stringId = applicationInfo.labelRes
  return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
}