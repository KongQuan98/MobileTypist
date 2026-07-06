package org.example.project.utils

expect fun formatDate(timestamp: Long): String

expect fun currentDateKey(): String

expect fun timestampToDateKey(timestamp: Long): String
