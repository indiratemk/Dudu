package com.example.dudu.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    const val DF1 = "dd.MM.yyyy"
    const val DF2 = "d MMMM yyyy"

    fun formatDate(timestamp: Long, format: String): String {
        val date = Date(timestamp)
        return SimpleDateFormat(format, Locale("ru", "RU")).format(date)
    }

    fun getCurrentDateWithoutTime(): Date {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.time
    }
}