package com.example.auxiliosaudepf.util

import android.widget.Toast
import com.example.auxiliosaudepf.AndroidApp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

actual fun getEpochMillis(day: Int, month: Int, year: Int): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month - 1)
        set(Calendar.YEAR, year)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

actual fun formatEpochMillis(millis: Long, pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale("pt", "BR"))
    return sdf.format(Date(millis))
}

actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun showToast(message: String) {
    AndroidApp.context?.let {
        Toast.makeText(it, message, Toast.LENGTH_LONG).show()
    }
}
