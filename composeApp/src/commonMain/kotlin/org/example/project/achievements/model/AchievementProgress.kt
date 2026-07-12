package org.example.project.achievements.model

import kotlinx.serialization.Serializable

@Serializable
data class AchievementProgress(
    val achievementId: String,
    val progress: Long,
    val unlocked: Boolean,
    val unlockedAt: Long?
)
