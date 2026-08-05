package org.example.project.di

import com.russhwolf.settings.Settings
import org.example.project.achievements.repository.AchievementRepository
import org.example.project.dailystreak.repository.StreakRepository
import org.example.project.dailystreak.repository.StreakRepositoryImpl
import org.example.project.data.storage.StorageManager

class AppContainer(settings: Settings) {
    val storageManager: StorageManager by lazy {
        StorageManager(settings)
    }

    val streakRepository: StreakRepository by lazy {
        StreakRepositoryImpl(storageManager = storageManager)
    }

    val achievementRepository: AchievementRepository by lazy {
        AchievementRepository(storageManager = storageManager)
    }
}