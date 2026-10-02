package org.example.project.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class DailyActivity(
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("activity_date")
    @JsonNames("date", "activity_date")
    val date: String, // ISO date string YYYY-MM-DD
    @SerialName("tests_completed")
    val testsCompleted: Int = 0,
    @SerialName("words_typed")
    val wordsTyped: Int = 0,
    @SerialName("characters_typed")
    val charactersTyped: Int = 0,
    @SerialName("play_time")
    val playTime: Int = 0, // in seconds
    @SerialName("updated_at")
    @JsonNames("updatedAt", "updated_at")
    @Serializable(with = NullableTimestampSerializer::class)
    val updatedAt: Long? = null
)
