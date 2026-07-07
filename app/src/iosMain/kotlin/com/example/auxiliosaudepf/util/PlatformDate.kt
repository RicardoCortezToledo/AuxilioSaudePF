package com.example.auxiliosaudepf.util

import platform.Foundation.*
import platform.UIKit.*

actual fun getEpochMillis(day: Int, month: Int, year: Int): Long {
    val calendar = NSCalendar.currentCalendar
    val components = NSDateComponents().apply {
        this.day = day.toLong()
        this.month = month.toLong()
        this.year = year.toLong()
        this.hour = 0
        this.minute = 0
        this.second = 0
    }
    val date = calendar.dateFromComponents(components) ?: return 0
    return (date.timeIntervalSince1970 * 1000).toLong()
}

actual fun formatEpochMillis(millis: Long, pattern: String): String {
    val date = NSDate.dateWithTimeIntervalSince1970(millis / 1000.0)
    val formatter = NSDateFormatter().apply {
        // Convert Android-like patterns if needed, but standard ones match NSDateFormatter patterns
        this.dateFormat = pattern
        this.locale = NSLocale.localeWithLocaleIdentifier("pt_BR")
    }
    return formatter.stringFromDate(date)
}

actual fun getCurrentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun showToast(message: String) {
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController != null) {
        val alert = UIAlertController.alertControllerWithTitle(
            title = null,
            message = message,
            preferredStyle = UIAlertControllerStyleAlert
        )
        alert.addAction(
            UIAlertAction.actionWithTitle("OK", UIAlertActionStyleDefault, handler = null)
        )
        rootViewController.presentViewController(alert, animated = true, completion = null)
    }
}
