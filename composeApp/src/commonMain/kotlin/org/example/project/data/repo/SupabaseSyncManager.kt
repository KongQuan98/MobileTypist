package org.example.project.data.repo

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant
import org.example.project.achievements.model.AchievementProgress
import org.example.project.data.model.DailyActivity
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserAchievement
import org.example.project.data.model.UserProfile
import org.example.project.data.model.UserStats
import org.example.project.data.storage.StorageManager
import kotlin.time.Clock

class SupabaseSyncManager(
    private val supabase: SupabaseClient,
    private val storageManager: StorageManager
) : SyncRepository {

    private val _isSyncing = MutableStateFlow(false)
    override val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    override suspend fun syncAll() {
        val user = supabase.auth.currentUserOrNull() ?: return
        if (_isSyncing.value) return

        println("SYNC START for user: ${user.id}")
        _isSyncing.value = true
        try {
            // 1. Fetch all cloud data
            println("SYNC FETCHING CLOUD DATA")
            val remoteProfile = supabase.postgrest["profiles"]
                .select(Columns.ALL) { filter { eq("id", user.id) } }
                .decodeSingleOrNull<UserProfile>()

            val remoteStats = supabase.postgrest["user_stats"]
                .select(Columns.ALL) { filter { eq("user_id", user.id) } }
                .decodeSingleOrNull<UserStats>()

            val remoteResults = supabase.postgrest["typing_results"]
                .select(Columns.ALL) { filter { eq("user_id", user.id) } }
                .decodeList<TypingTestResult>()

            val remoteAchievements = supabase.postgrest["user_achievements"]
                .select(Columns.ALL) { filter { eq("user_id", user.id) } }
                .decodeList<UserAchievement>()

            val remoteActivity = supabase.postgrest["daily_activity"]
                .select(Columns.ALL) { filter { eq("user_id", user.id) } }
                .decodeList<DailyActivity>()

            // 2. Merge logic (Safe Merge Rules)

            // Profile
            println("SYNC PROFILE MERGE")
            val localProfile = storageManager.getUserProfile()
            if (remoteProfile != null) {
                val merged = remoteProfile.copy(
                    id = user.id,
                    isLoggedIn = true,
                    email = user.email,
                    username = remoteProfile.username.ifBlank { localProfile.username },
                    avatarId = remoteProfile.avatarId.ifBlank { localProfile.avatarId }
                )
                storageManager.saveUserProfile(merged)
            } else {
                storageManager.saveUserProfile(
                    localProfile.copy(
                        id = user.id,
                        isLoggedIn = true,
                        email = user.email
                    )
                )
            }

            // Stats
            println("SYNC STATS MERGE")
            remoteStats?.let { cloud ->
                storageManager.updateStats(cloud.bestWpm, cloud.totalTests)
                val localStreak = storageManager.getStreakData()
                storageManager.saveStreakData(
                    localStreak.copy(
                        longestStreak = maxOf(localStreak.longestStreak, cloud.longestStreak)
                    )
                )
            }

            // Results
            println("SYNC TYPING RESULTS MERGE")
            val localResultsMap =
                storageManager.resultsFlow.value.associateBy { it.id }.toMutableMap()
            remoteResults.forEach { cloud ->
                if (!localResultsMap.containsKey(cloud.id)) {
                    localResultsMap[cloud.id] = cloud
                }
            }
            storageManager.saveAllResults(localResultsMap.values.toList())

            // Achievements
            println("SYNC ACHIEVEMENTS MERGE")
            val localAchievementsMap = storageManager.getAchievementProgress().toMutableMap()
            remoteAchievements.forEach { cloud ->
                val local = localAchievementsMap[cloud.achievementId]
                if (local == null || (cloud.unlockedAt != null && (local.unlockedAt == null || cloud.unlockedAt!! < local.unlockedAt!!)) || (cloud.progress > local.progress)) {
                    localAchievementsMap[cloud.achievementId] = AchievementProgress(
                        achievementId = cloud.achievementId,
                        progress = cloud.progress,
                        unlocked = cloud.unlockedAt != null,
                        unlockedAt = cloud.unlockedAt
                    )
                }
            }
            storageManager.saveAchievementProgress(localAchievementsMap.values.toList())

            // Daily Activity
            println("SYNC DAILY ACTIVITY MERGE")
            val localActivityMap = storageManager.getDailyActivity().toMutableMap()
            remoteActivity.forEach { cloud ->
                val local = localActivityMap[cloud.date]
                if (local == null || (cloud.updatedAt ?: 0) > (local.updatedAt ?: 0)) {
                    localActivityMap[cloud.date] = cloud
                } else {
                    localActivityMap[cloud.date] = local.copy(
                        testsCompleted = maxOf(local.testsCompleted, cloud.testsCompleted),
                        wordsTyped = maxOf(local.wordsTyped, cloud.wordsTyped),
                        charactersTyped = maxOf(local.charactersTyped, cloud.charactersTyped),
                        playTime = maxOf(local.playTime, cloud.playTime),
                        updatedAt = maxOf(local.updatedAt ?: 0, cloud.updatedAt ?: 0)
                    )
                }
            }
            storageManager.writeDailyActivity(localActivityMap)

            // 3. Push missing local data back to cloud
            pushToCloud(user.id)

            println("SYNC SUCCESS")
            storageManager.setSyncPending(false)
        } catch (e: Exception) {
            println("SYNC ERROR: ${e.message}")
            e.printStackTrace()
            storageManager.setSyncPending(true)
        } finally {
            _isSyncing.value = false
        }
    }

    override suspend fun syncPending() {
        if (storageManager.isSyncPending()) {
            syncAll()
        }
    }

    private suspend fun pushToCloud(userId: String) {
        val now = Clock.System.now().toEpochMilliseconds()

        // Results
        println("SYNC PUSH TYPING RESULTS")
        val localResults = storageManager.resultsFlow.value
        if (localResults.isNotEmpty()) {
            try {
                val resultsToPush = localResults.map { result ->
                    mapOf(
                        "id" to result.id,
                        "user_id" to userId,
                        "wpm" to result.wpm,
                        "accuracy" to result.accuracy,
                        "duration" to result.duration,
                        "word_count" to result.wordsTyped,
                        "character_count" to result.characterCount,
                        "language" to result.language,
                        "difficulty" to result.difficulty,
                        "created_at" to Instant.fromEpochMilliseconds(result.timestamp).toString()
                    )
                }
                supabase.postgrest["typing_results"].upsert(resultsToPush)
            } catch (e: Exception) {
                println("SYNC PUSH RESULTS ERROR: ${e.message}")
            }
        }

        // Stats
        println("SYNC PUSH STATS")
        val streak = storageManager.getStreakData()
        val stats = storageManager.getUserStats(userId)
        try {
            val statsMap = mapOf(
                "user_id" to userId,
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
            println("SYNC PUSH STATS ERROR: ${e.message}")
        }

        // Achievements
        println("SYNC PUSH ACHIEVEMENTS")
        val localAchievements = storageManager.getAchievementProgress().values.toList()
        if (localAchievements.isNotEmpty()) {
            try {
                val achievementsToPush = localAchievements.map {
                    mapOf(
                        "user_id" to userId,
                        "achievement_id" to it.achievementId,
                        "progress" to it.progress,
                        "unlocked_at" to it.unlockedAt?.let { ts ->
                            Instant.fromEpochMilliseconds(ts).toString()
                        },
                        "updated_at" to Instant.fromEpochMilliseconds(now).toString()
                    )
                }
                supabase.postgrest["user_achievements"].upsert(achievementsToPush)
            } catch (e: Exception) {
                println("SYNC PUSH ACHIEVEMENTS ERROR: ${e.message}")
            }
        }

        // Daily Activity
        println("SYNC PUSH DAILY ACTIVITY")
        val localActivity = storageManager.getDailyActivity().values.toList()
        if (localActivity.isNotEmpty()) {
            try {
                val activityToPush = localActivity.map {
                    mapOf(
                        "user_id" to userId,
                        "activity_date" to it.date,
                        "tests_completed" to it.testsCompleted,
                        "words_typed" to it.wordsTyped,
                        "characters_typed" to it.charactersTyped,
                        "play_time" to it.playTime,
                        "updated_at" to (it.updatedAt?.let { ts ->
                            Instant.fromEpochMilliseconds(ts).toString()
                        } ?: Instant.fromEpochMilliseconds(now).toString())
                    )
                }
                supabase.postgrest["daily_activity"].upsert(activityToPush)
            } catch (e: Exception) {
                println("SYNC PUSH DAILY ACTIVITY ERROR: ${e.message}")
            }
        }

        // Profile
        println("SYNC PUSH PROFILE")
        val localProfile = storageManager.getUserProfile()
        try {
            val profileMap = mapOf(
                "id" to userId,
                "username" to localProfile.username,
                "avatar" to localProfile.avatarId,
                "updated_at" to Instant.fromEpochMilliseconds(now).toString()
            )
            supabase.postgrest["profiles"].upsert(profileMap)
        } catch (e: Exception) {
            println("SYNC PUSH PROFILE ERROR: ${e.message}")
        }
    }
}
