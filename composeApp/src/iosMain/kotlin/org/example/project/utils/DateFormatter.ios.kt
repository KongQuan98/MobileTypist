package org.example.project.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun formatDate(timestamp: Long): String {
    // Convert milliseconds to seconds
    val secondsSince1970 = timestamp / 1000.0
    
    // NSDate reference date is Jan 1, 2001 = 978307200 seconds since Unix epoch
    val unixEpochToReferenceDate = 978307200.0
    val timeIntervalSinceReferenceDate = secondsSince1970 - unixEpochToReferenceDate
    val date = NSDate(timeIntervalSinceReferenceDate)
    
    val formatter = NSDateFormatter()
    formatter.dateFormat = "MMM dd, yyyy HH:mm"
    
    return formatter.stringFromDate(date) as String
}

