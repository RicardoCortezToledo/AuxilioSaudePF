package com.example.auxiliosaudepf.util

expect fun getEpochMillis(day: Int, month: Int, year: Int): Long
expect fun formatEpochMillis(millis: Long, pattern: String): String
expect fun getCurrentTimeMillis(): Long
expect fun showToast(message: String)
