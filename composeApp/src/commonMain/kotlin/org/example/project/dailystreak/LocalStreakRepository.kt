package org.example.project.dailystreak

import androidx.compose.runtime.staticCompositionLocalOf
import org.example.project.dailystreak.repository.StreakRepository

val LocalStreakRepository = staticCompositionLocalOf<StreakRepository> {
    error("No StreakRepository provided")
}
