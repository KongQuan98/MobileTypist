package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class UserStats(
    @SerialName("user_id")
    val userId: String,
    @SerialName("best_wpm")
    val bestWpm: Int = 0,
    @SerialName("best_accuracy")
    val bestAccuracy: Int = 0,
    @SerialName("total_tests")
    val totalTests: Int = 0,
    @SerialName("total_words")
    val totalWords: Int = 0,
    @SerialName("total_characters")
    val totalCharacters: Int = 0,
    @SerialName("total_play_time")
    val totalPlayTime: Int = 0,
    @SerialName("current_streak")
    val currentStreak: Int = 0,
    @SerialName("longest_streak")
    val longestStreak: Int = 0,
    @SerialName("updated_at")
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)
