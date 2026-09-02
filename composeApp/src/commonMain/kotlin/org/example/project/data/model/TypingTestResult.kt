package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class TypingTestResult(
    @SerialName("id")
    val id: String = Clock.System.now().toEpochMilliseconds().toString(),
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("mode")
    val mode: TypingMode,
    @SerialName("wpm")
    val wpm: Int,
    @SerialName("accuracy")
    val accuracy: Int,
    val correctChars: Int = 0,
    val errorCount: Int = 0,
    @SerialName("created_at")
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    @SerialName("duration")
    val duration: Int = 0, // in seconds
    @SerialName("word_count")
    val wordsTyped: Int = 0,
    @SerialName("character_count")
    val characterCount: Int = 0,
    @SerialName("language")
    val language: String = "english",
    @SerialName("difficulty")
    val difficulty: String = "normal"
)

enum class TypingMode {
    TIME, WORDS, QUOTES
}
