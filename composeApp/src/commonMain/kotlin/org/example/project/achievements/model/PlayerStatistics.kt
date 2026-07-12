package org.example.project.achievements.model

data class PlayerStatistics(
    val highestWpm: Int,
    val highestAccuracy: Int,
    val totalTests: Int,
    val totalKeystrokes: Long,
    val totalDurationSeconds: Long,
    val currentStreak: Int,
    val maxStreak: Int,
    val hasPracticedEarlyBird: Boolean,
    val hasPracticedNightOwl: Boolean,
    val socialShares: Int,
    val maxConsecutivePerfectTests: Int,
    val totalWords: Long
)
