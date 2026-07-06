package org.example.project.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

actual fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

actual fun currentDateKey(): String {
    return dateKeyFormat.format(Date())
}

actual fun timestampToDateKey(timestamp: Long): String {
    return dateKeyFormat.format(Date(timestamp))
}


