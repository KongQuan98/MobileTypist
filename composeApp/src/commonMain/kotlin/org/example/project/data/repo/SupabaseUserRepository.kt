package org.example.project.data.repo

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.example.project.achievements.model.AchievementProgress
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserAchievement
import org.example.project.data.model.UserProfile
import org.example.project.data.model.UserStats
import org.example.project.data.storage.StorageManager

class SupabaseUserRepository(
    private val supabase: SupabaseClient,
    private val storageManager: StorageManager
) : UserRepository {

    override val userProfile: Flow<UserProfile> = storageManager.userProfileFlow

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
                    progress = it.progress,
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
            val remoteResult = result.copy(userId = user.id)
            supabase.postgrest["typing_results"].insert(remoteResult)

            // Update stats in Supabase too
            updateRemoteStats()
        } catch (e: Exception) {
            // Silently fail for offline support - sync later
        }
    }

    override suspend fun updateProfile(profile: UserProfile) {
        storageManager.saveUserProfile(profile)

        val user = supabase.auth.currentUserOrNull() ?: return
        try {
            supabase.postgrest["profiles"].upsert(profile.copy(id = user.id))
        } catch (e: Exception) {
            // Silently fail
        }
    }

    private suspend fun updateRemoteStats() {
        val user = supabase.auth.currentUserOrNull() ?: return
        val streak = storageManager.getStreakData()
        val stats = UserStats(
            userId = user.id,
            bestWpm = storageManager.getBestWpm(),
            totalTests = storageManager.getTotalTests(),
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak
        )
        supabase.postgrest["user_stats"].upsert(stats)
    }

    override suspend fun syncData() {
        val user = supabase.auth.currentUserOrNull() ?: return

        try {
            // 1. Fetch from Supabase
            val remoteProfile = supabase.postgrest["profiles"]
                .select(Columns.ALL) { filter { eq("id", user.id) } }
                .decodeSingleOrNull<UserProfile>()

            val remoteStats = supabase.postgrest["user_stats"]
                .select(Columns.ALL) { filter { eq("user_id", user.id) } }
                .decodeSingleOrNull<UserStats>()

            val remoteResults = supabase.postgrest["typing_results"]
                .select(Columns.ALL) { filter { eq("user_id", user.id) } }
                .decodeList<TypingTestResult>()

            val remoteUserAchievements = supabase.postgrest["user_achievements"]
                .select(Columns.ALL) { filter { eq("user_id", user.id) } }
                .decodeList<UserAchievement>()

            // 2. Merge logic

            // Profile merge (Supabase wins if exists)
            remoteProfile?.let {
                storageManager.saveUserProfile(it.copy(isLoggedIn = true, email = user.email))
            }

            // Stats merge (Highest wins)
            remoteStats?.let { remote ->
                storageManager.updateStats(remote.bestWpm, remote.totalTests)

                val localStreak = storageManager.getStreakData()
                val mergedStreak = localStreak.copy(
                    currentStreak = maxOf(localStreak.currentStreak, remote.currentStreak),
                    longestStreak = maxOf(localStreak.longestStreak, remote.longestStreak)
                )
                storageManager.saveStreakData(mergedStreak)
            }

            // Results merge (ID check)
            val localResultsMap =
                storageManager.resultsFlow.value.associateBy { it.id }.toMutableMap()
            remoteResults.forEach { remote ->
                if (!localResultsMap.containsKey(remote.id)) {
                    localResultsMap[remote.id] = remote
                }
            }
            storageManager.saveAllResults(localResultsMap.values.toList())

            // Achievements merge (Never remove)
            val localAchievementsMap = storageManager.getAchievementProgress().toMutableMap()
            remoteUserAchievements.forEach { remote ->
                val local = localAchievementsMap[remote.achievementId]
                if (local == null || (!local.unlocked && remote.unlocked) || (remote.progress > local.progress)) {
                    localAchievementsMap[remote.achievementId] = AchievementProgress(
                        achievementId = remote.achievementId,
                        progress = remote.progress,
                        unlocked = remote.unlocked,
                        unlockedAt = remote.unlockedAt
                    )
                }
            }
            storageManager.saveAchievementProgress(localAchievementsMap.values.toList())

            // 3. Push back local-only data to Supabase
            pushLocalDataToSupabase(user.id)

        } catch (e: Exception) {
            // Handle error (offline)
        }
    }

    private suspend fun pushLocalDataToSupabase(userId: String) {
        // Sync results
        val localResults = storageManager.resultsFlow.value
        if (localResults.isNotEmpty()) {
            try {
                // Upsert all results for this user
                supabase.postgrest["typing_results"].upsert(localResults.map { it.copy(userId = userId) })
            } catch (e: Exception) {
            }
        }

        // Sync stats
        updateRemoteStats()

        // Sync achievements
        val localAchievements = storageManager.getAchievementProgress().values.toList()
        if (localAchievements.isNotEmpty()) {
            try {
                supabase.postgrest["user_achievements"].upsert(localAchievements.map {
                    UserAchievement(
                        userId = userId,
                        achievementId = it.achievementId,
                        progress = it.progress,
                        unlocked = it.unlocked,
                        unlockedAt = it.unlockedAt
                    )
                })
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun clearData() {
        storageManager.clearAllData()
    }
}
