package org.example.project.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UserAchievement(
    @SerialName("user_id")
    val userId: String,
    @SerialName("achievement_id")
    val achievementId: String,
    @SerialName("progress")
    val progress: Long,
    @SerialName("unlocked")
    val unlocked: Boolean,
    @SerialName("unlocked_at")
    @JsonNames("unlockedAt", "unlocked_at")
    @Serializable(with = NullableTimestampSerializer::class)
    val unlockedAt: Long?
)
