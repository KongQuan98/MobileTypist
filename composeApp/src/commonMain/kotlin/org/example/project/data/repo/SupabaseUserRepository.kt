package org.example.project.data.repo

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserAchievement
import org.example.project.data.model.UserProfile
import org.example.project.data.model.UserStats
import org.example.project.data.storage.StorageManager
import kotlin.time.Clock

class SupabaseUserRepository(
    private val supabase: SupabaseClient,
    private val storageManager: StorageManager
) : UserRepository {

    override val userProfile: Flow<UserProfile> = combine(
        storageManager.userProfileFlow,
        org.example.project.auth.AuthModule.repository.authState
    ) { localProfile, authState ->
        if (authState is org.example.project.auth.AuthState.Authenticated) {
            localProfile.copy(
                id = authState.user.id,
                email = authState.user.email ?: localProfile.email,
                isLoggedIn = true
            )
        } else {
            localProfile.copy(
                isLoggedIn = false
            )
        }
    }

    override val userStats: Flow<UserStats?> = combine(
        storageManager.bestWpmFlow,
        storageManager.totalTestsFlow,
        storageManager.streakFlow
    ) { bestWpm, totalTests, streak ->
        val user = supabase.auth.currentUserOrNull() ?: return@combine null
        UserStats(
            userId = user.id,
            bestWpm = bestWpm,
            totalTests = totalTests,
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak
        )
    }

    override val userAchievements: Flow<List<UserAchievement>> =
        storageManager.achievementProgressFlow.map { progressMap ->
            val user = supabase.auth.currentUserOrNull() ?: return@map emptyList()
            progressMap.values.map {
                UserAchievement(
                    userId = user.id,
                    achievementId = it.achievementId,
                    progress = it.progress.toLong(),
                    unlocked = it.unlocked,
                    unlockedAt = it.unlockedAt
                )
            }
        }

    override val typingResults: Flow<List<TypingTestResult>> = storageManager.resultsFlow

    override suspend fun saveResult(result: TypingTestResult) {
        // Save locally first
        storageManager.saveResult(result)

        // Sync to Supabase if logged in
        val user = supabase.auth.currentUserOrNull() ?: return
        try {
            val resultToPush = mapOf(
                "id" to result.id,
                "user_id" to user.id,
                "wpm" to result.wpm,
                "accuracy" to result.accuracy,
                "duration" to result.duration,
                "word_count" to result.wordsTyped,
                "character_count" to result.characterCount,
                "language" to result.language,
                "difficulty" to result.difficulty,
                "created_at" to Instant.fromEpochMilliseconds(result.timestamp).toString()
            )
            supabase.postgrest["typing_results"].upsert(resultToPush)

            // Update stats in Supabase too
            updateRemoteStats()
        } catch (e: Exception) {
            println("SAVE RESULT REMOTE ERROR: ${e.message}")
        }
    }

    override suspend fun updateProfile(profile: UserProfile) {
        storageManager.saveUserProfile(profile)

        val user = supabase.auth.currentUserOrNull() ?: return
        try {
            val profileMap = mapOf(
                "id" to user.id,
                "username" to profile.username,
                "avatar" to profile.avatarId,
                "updated_at" to Instant.fromEpochMilliseconds(
                    Clock.System.now().toEpochMilliseconds()
                ).toString()
            )
            supabase.postgrest["profiles"].upsert(profileMap)
        } catch (e: Exception) {
            println("UPDATE PROFILE REMOTE ERROR: ${e.message}")
        }
    }

    private suspend fun updateRemoteStats() {
        val user = supabase.auth.currentUserOrNull() ?: return
        val streak = storageManager.getStreakData()
        val stats = storageManager.getUserStats(user.id)
        try {
            val statsMap = mapOf(
                "user_id" to user.id,
                "best_wpm" to stats.bestWpm,
                "best_accuracy" to stats.bestAccuracy,
                "total_tests" to stats.totalTests,
                "total_words" to stats.totalWords,
                "total_characters" to stats.totalCharacters,
                "total_play_time" to stats.totalPlayTime,
                "current_streak" to streak.currentStreak,
                "longest_streak" to streak.longestStreak,
                "updated_at" to Instant.fromEpochMilliseconds(stats.updatedAt).toString()
            )
            supabase.postgrest["user_stats"].upsert(statsMap)
        } catch (e: Exception) {
            println("UPDATE REMOTE STATS ERROR: ${e.message}")
        }
    }

    override suspend fun syncData() {
        // Redirect to global sync manager if needed
    }

    override suspend fun clearData() {
        storageManager.clearAllData()
    }
}
