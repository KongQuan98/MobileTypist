package org.example.project.achievements.events

import org.example.project.achievements.model.Achievement

sealed interface AchievementEvent {
    data class Unlocked(val achievement: Achievement) : AchievementEvent
}
