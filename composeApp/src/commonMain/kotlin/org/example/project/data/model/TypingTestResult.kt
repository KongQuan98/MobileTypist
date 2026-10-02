package org.example.project.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlin.time.Clock

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TypingTestResult(
    @SerialName("id")
    val id: String = generateUUID(),
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
    @JsonNames("timestamp", "created_at")
    @Serializable(with = TimestampSerializer::class)
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

private fun generateUUID(): String {
    val chars = "0123456789abcdef"
    return buildString {
        repeat(8) { append(chars.random()) }
        append("-")
        repeat(4) { append(chars.random()) }
        append("-4")
        repeat(3) { append(chars.random()) }
        append("-")
        append(chars[(8..11).random()])
        repeat(3) { append(chars.random()) }
        append("-")
        repeat(12) { append(chars.random()) }
    }
}
