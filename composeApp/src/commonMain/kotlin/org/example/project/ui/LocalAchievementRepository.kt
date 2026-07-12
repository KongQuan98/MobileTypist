package org.example.project.ui

import androidx.compose.runtime.staticCompositionLocalOf
import org.example.project.achievements.repository.AchievementRepository

val LocalAchievementRepository = staticCompositionLocalOf<AchievementRepository> {
    error("No AchievementRepository provided")
}
