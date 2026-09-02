package org.example.project.data.repo

import kotlinx.coroutines.flow.Flow
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserAchievement
import org.example.project.data.model.UserProfile
import org.example.project.data.model.UserStats

interface UserRepository {
    val userProfile: Flow<UserProfile>
    val userStats: Flow<UserStats?>
    val userAchievements: Flow<List<UserAchievement>>
    val typingResults: Flow<List<TypingTestResult>>

    suspend fun saveResult(result: TypingTestResult)
    suspend fun updateProfile(profile: UserProfile)
    suspend fun syncData()
    suspend fun clearData()
}
