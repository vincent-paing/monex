package com.aungkyawpaing.monex.internal.helper

/**
 * Created by Vincent on 1/22/20
 */
internal object ByteFormatter {

  /**
   * Copied from https://stackoverflow.com/a/24805871/3125020
   */
  fun formatByte(byte: Long): String {
    if (byte < 1024) return "$byte B"
    val z = (63 - java.lang.Long.numberOfLeadingZeros(byte)) / 10
    return String.format(
      "%.1f %sB",
      byte.toDouble() / (1L shl z * 10),
      " KMGTPE"[z]
    )

  }

}

internal fun Long.formatByte(): String {
  return ByteFormatter.formatByte(this)
}