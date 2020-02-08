package com.aungkyawpaing.monex.internal.helper

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal object FileHelper {

  fun parseContentsFromFile(file: File): String {
    val bufferedReader = file.bufferedReader()

    val stringBuilder = StringBuilder()
    var exception: Exception? = null
    try {
      bufferedReader.forEachLine {
        stringBuilder.append(it)
      }
    } catch (ioException: IOException) {
      exception = ioException
    } finally {
      bufferedReader.close()
    }
    if (exception != null) throw exception

    val contents = stringBuilder.toString()
    return contents
  }

  fun writeToFile(content: String, outputFile: File) {
    if (outputFile.parentFile?.exists()?.not() == true) {
      outputFile.parentFile?.mkdirs()
    }

    if (outputFile.exists().not()) {
      outputFile.createNewFile()
    }

    val fileOutputStream = FileOutputStream(outputFile)
    val writer = fileOutputStream.bufferedWriter()
    try {
      writer.write(content)
    } finally {
      writer.close()
      fileOutputStream.close()
    }
  }

}