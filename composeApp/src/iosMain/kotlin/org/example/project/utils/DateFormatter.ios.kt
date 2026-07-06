package org.example.project.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun formatDate(timestamp: Long): String {
    val date = nsDateFromTimestamp(timestamp)

    val formatter = NSDateFormatter()
    formatter.dateFormat = "MMM dd, yyyy HH:mm"

    return formatter.stringFromDate(date) as String
}

actual fun currentDateKey(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(NSDate()) as String
}

actual fun timestampToDateKey(timestamp: Long): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(nsDateFromTimestamp(timestamp)) as String
}

private fun nsDateFromTimestamp(timestamp: Long): NSDate {
    val secondsSince1970 = timestamp / 1000.0
    val unixEpochToReferenceDate = 978307200.0
    val timeIntervalSinceReferenceDate = secondsSince1970 - unixEpochToReferenceDate
    return NSDate(timeIntervalSinceReferenceDate = timeIntervalSinceReferenceDate)
}
