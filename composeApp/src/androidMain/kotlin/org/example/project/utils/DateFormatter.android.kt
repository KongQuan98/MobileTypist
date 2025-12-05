package org.example.project.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}


