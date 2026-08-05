package org.example.project.dailystreak.model

import kotlinx.serialization.Serializable

@Serializable
data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastPlayedEpochDay: Long? = null,
    val firstPlayedEpochDay: Long? = null,
    val pendingEvent: StreakEvent? = null
)
