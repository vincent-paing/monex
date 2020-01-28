package com.aungkyawpaing.monex.internal.data.typeconverters

import androidx.room.TypeConverter
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

internal class ThreeTenConverter {

  @TypeConverter
  fun fromLocalDateTime(value: LocalDateTime): Long {
    return value.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
  }

  @TypeConverter
  fun toLocalDateTime(value: Long): LocalDateTime {
    return Instant.ofEpochMilli(value).atZone(ZoneOffset.UTC).toLocalDateTime()
  }

  @TypeConverter
  fun fromDuration(value: Duration): Long {
    return value.toMillis()
  }

  @TypeConverter
  fun toDuration(value: Long): Duration {
    return Duration.ofMillis(value)
  }

}

