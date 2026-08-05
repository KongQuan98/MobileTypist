package org.example.project.dailystreak.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface StreakEvent {

    @Serializable
    @SerialName("first_play")
    data object FirstPlay : StreakEvent


    @Serializable
    @SerialName("streak_started")
    data class StreakStarted(
        val streak: Int
    ) : StreakEvent


    @Serializable
    @SerialName("streak_continued")
    data class StreakContinued(
        val streak: Int
    ) : StreakEvent


    @Serializable
    @SerialName("streak_broken")
    data class StreakBroken(
        val previousStreak: Int
    ) : StreakEvent


    @Serializable
    @SerialName("welcome_back")
    data class WelcomeBack(
        val daysAway: Int
    ) : StreakEvent


    @Serializable
    @SerialName("milestone")
    data class Milestone(
        val streak: Int
    ) : StreakEvent


    @Serializable
    @SerialName("none")
    data object None : StreakEvent
}