package org.example.project.data.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class TypingTestResult(
    val id: String = Clock.System.now().toEpochMilliseconds().toString(),
    val mode: TypingMode,
    val wpm: Int,
    val accuracy: Int,
    val correctChars: Int,
    val errorCount: Int,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val duration: Int = 0, // in seconds
    val wordsTyped: Int = 0
)

enum class TypingMode {
    TIME, WORDS, QUOTES
}
