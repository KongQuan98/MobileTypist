package org.example.project.di

import com.russhwolf.settings.Settings
import org.example.project.achievements.repository.AchievementRepository
import org.example.project.auth.AuthModule
import org.example.project.auth.SupabaseProvider
import org.example.project.dailystreak.repository.StreakRepository
import org.example.project.dailystreak.repository.StreakRepositoryImpl
import org.example.project.data.repo.SupabaseUserRepository
import org.example.project.data.repo.UserRepository
import org.example.project.data.storage.StorageManager
import org.example.project.viewModel.UserViewModel

class AppContainer(settings: Settings) {
    val storageManager: StorageManager by lazy {
        StorageManager(settings)
    }

    val userRepository: UserRepository by lazy {
        SupabaseUserRepository(
            supabase = SupabaseProvider.client,
            storageManager = storageManager
        )
    }

    val streakRepository: StreakRepository by lazy {
        StreakRepositoryImpl(storageManager = storageManager)
    }

    val achievementRepository: AchievementRepository by lazy {
        AchievementRepository(storageManager = storageManager)
    }

    val userViewModel: UserViewModel by lazy {
        UserViewModel(
            authRepository = AuthModule.repository,
            userRepository = userRepository
        )
    }
}
