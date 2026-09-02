package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val unlockedAt: Long?
)
