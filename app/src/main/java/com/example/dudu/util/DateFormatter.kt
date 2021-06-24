package com.example.dudu.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    const val DF1 = "dd.MM.yyyy"
    const val DF2 = "d MMMM yyyy"

    fun formatDate(date: Date, format: String): String {
        return SimpleDateFormat(format, Locale("ru", "RU")).format(date)
    }
}