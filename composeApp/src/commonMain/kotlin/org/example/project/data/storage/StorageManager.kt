package org.example.project.data.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.example.project.achievements.model.AchievementProgress
import org.example.project.dailystreak.model.StreakData
import org.example.project.data.model.AppSettings
import org.example.project.data.model.DailyActivity
import org.example.project.data.model.DailyActivityDurations
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserProfile
import org.example.project.data.model.UserStats
import org.example.project.data.repo.ActivityHeatmapRepository
import kotlin.time.Clock

class StorageManager(private val settings: Settings) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val KEY_RESULTS = "typing_test_results"
        private const val KEY_SETTINGS = "app_settings"
        private const val KEY_BEST_WPM = "best_wpm"
        private const val KEY_TOTAL_TESTS = "total_tests"
        private const val KEY_USER_PROFILE = "user_profile"
        private const val KEY_DAILY_ACTIVITY = "daily_activity_durations"
        private const val KEY_ACHIEVEMENT_PROGRESS = "achievement_progress"
        private const val KEY_SOCIAL_SHARES = "social_shares"
        private const val KEY_STREAK = "streak_data"
        private const val KEY_SYNC_PENDING = "sync_pending"
    }

    private val _settingsFlow = MutableStateFlow(getSettings())
    val settingsFlow = _settingsFlow.asStateFlow()

    private val _userProfileFlow = MutableStateFlow(getUserProfile())
    val userProfileFlow = _userProfileFlow.asStateFlow()

    // Add reactive flows for statistics
    private val _resultsFlow = MutableStateFlow(getResults())
    val resultsFlow = _resultsFlow.asStateFlow()

    private val _bestWpmFlow = MutableStateFlow(getBestWpm())
    val bestWpmFlow = _bestWpmFlow.asStateFlow()

    private val _totalTestsFlow = MutableStateFlow(getTotalTests())
    val totalTestsFlow = _totalTestsFlow.asStateFlow()

    private val _dailyActivityFlow = MutableStateFlow(getDailyActivity())
    val dailyActivityFlow = _dailyActivityFlow.asStateFlow()

    private val _syncPendingFlow = MutableStateFlow(isSyncPending())
    val syncPendingFlow = _syncPendingFlow.asStateFlow()

    private val _achievementProgressFlow = MutableStateFlow(getAchievementProgress())
    val achievementProgressFlow = _achievementProgressFlow.asStateFlow()

    private val _streakFlow = MutableStateFlow(getStreakData())
    val streakFlow = _streakFlow.asStateFlow()

    fun saveResult(result: TypingTestResult) {
        // Update statistics in storage first
        updateBestWpm(result.wpm)
        incrementTotalTests()
        addDailyActivity(result)

        // Save result to history after updating daily activity to avoid double counting during migration
        val results = getResults().toMutableList()
        results.add(0, result)
        val limitedResults = results.take(100)
        saveAllResults(limitedResults)
    }

    fun saveAllResults(results: List<TypingTestResult>) {
        val sortedResults = results.sortedByDescending { it.timestamp }.take(100)
        settings[KEY_RESULTS] = json.encodeToString(sortedResults)
        _resultsFlow.value = sortedResults
    }

    fun updateStats(bestWpm: Int, totalTests: Int) {
        if (bestWpm > getBestWpm()) {
            settings[KEY_BEST_WPM] = bestWpm
            _bestWpmFlow.value = bestWpm
        }
        if (totalTests > getTotalTests()) {
            settings[KEY_TOTAL_TESTS] = totalTests
            _totalTestsFlow.value = totalTests
        }
    }

    /**
     * Call this to force a refresh from the disk.
     * Useful for iOS where different tabs might have modified the data.
     */
    fun refreshStats() {
        _resultsFlow.value = getResults()
        _bestWpmFlow.value = getBestWpm()
        _totalTestsFlow.value = getTotalTests()
        _userProfileFlow.value = getUserProfile()
        _settingsFlow.value = getSettings()
        _dailyActivityFlow.value = getDailyActivity()
        _achievementProgressFlow.value = getAchievementProgress()
    }

    fun getAchievementProgress(): Map<String, AchievementProgress> {
        val jsonString = settings.getStringOrNull(KEY_ACHIEVEMENT_PROGRESS) ?: return emptyMap()
        return try {
            json.decodeFromString<List<AchievementProgress>>(jsonString)
                .associateBy { it.achievementId }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun saveAchievementProgress(progress: List<AchievementProgress>) {
        val current = getAchievementProgress().toMutableMap()
        progress.forEach {
            current[it.achievementId] = it
        }
        settings[KEY_ACHIEVEMENT_PROGRESS] = json.encodeToString(current.values.toList())
        _achievementProgressFlow.value = current
    }

    fun getSocialShares(): Int {
        return settings.getIntOrNull(KEY_SOCIAL_SHARES) ?: 0
    }

    fun incrementSocialShares() {
        val current = getSocialShares()
        settings[KEY_SOCIAL_SHARES] = current + 1
        refreshStats()
    }

    private fun getResults(): List<TypingTestResult> {
        val jsonString = settings.getStringOrNull(KEY_RESULTS) ?: return emptyList()
        return try {
            val decoded = json.decodeFromString<List<TypingTestResult>>(jsonString)
            ensureDistinctTimestamps(decoded)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun ensureDistinctTimestamps(results: List<TypingTestResult>): List<TypingTestResult> {
        if (results.isEmpty()) return results
        var modified = false
        val sorted = results.toMutableList()
        val seenTimestamps = mutableSetOf<Long>()
        for (i in sorted.indices) {
            var ts = sorted[i].timestamp
            if (seenTimestamps.contains(ts)) {
                while (seenTimestamps.contains(ts)) {
                    ts -= 1000L
                }
                sorted[i] = sorted[i].copy(timestamp = ts)
                modified = true
            }
            seenTimestamps.add(ts)
        }
        if (modified) {
            saveAllResults(sorted)
        }
        return sorted
    }

    fun getBestWpm(): Int {
        return settings.getIntOrNull(KEY_BEST_WPM) ?: 0
    }

    private fun updateBestWpm(wpm: Int) {
        val currentBest = getBestWpm()
        if (wpm > currentBest) {
            settings[KEY_BEST_WPM] = wpm
        }
    }

    fun getTotalTests(): Int {
        return settings.getIntOrNull(KEY_TOTAL_TESTS) ?: 0
    }

    fun getUserStats(userId: String): UserStats {
        val streak = getStreakData()
        return UserStats(
            userId = userId,
            bestWpm = getBestWpm(),
            totalTests = getTotalTests(),
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak,
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )
    }

    private fun incrementTotalTests() {
        val current = getTotalTests()
        settings[KEY_TOTAL_TESTS] = current + 1
    }

    fun getSettings(): AppSettings {
        val jsonString = settings.getStringOrNull(KEY_SETTINGS) ?: return AppSettings()
        return try {
            json.decodeFromString<AppSettings>(jsonString)
        } catch (e: Exception) {
            AppSettings()
        }
    }

    fun saveSettings(settings: AppSettings) {
        this.settings[KEY_SETTINGS] = json.encodeToString(settings)
        _settingsFlow.value = settings
    }

    fun getUserProfile(): UserProfile {
        val jsonString = settings.getStringOrNull(KEY_USER_PROFILE) ?: return UserProfile()
        return try {
            json.decodeFromString<UserProfile>(jsonString)
        } catch (e: Exception) {
            UserProfile()
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        this.settings[KEY_USER_PROFILE] = json.encodeToString(profile)
        _userProfileFlow.value = profile
    }

    fun getDailyActivity(): Map<String, DailyActivity> {
        val stored = readDailyActivity()
        val resultsAggregated = ActivityHeatmapRepository.aggregateFromResults(getResults())
        if (stored.isEmpty()) {
            if (resultsAggregated.isNotEmpty()) {
                persistDailyActivity(resultsAggregated)
            }
            return resultsAggregated
        }
        if (resultsAggregated.isEmpty()) {
            return stored
        }
        val merged = resultsAggregated.toMutableMap()
        for ((date, activity) in stored) {
            val existing = merged[date]
            if (existing == null) {
                merged[date] = activity
            } else {
                merged[date] = DailyActivity(
                    date = date,
                    testsCompleted = maxOf(existing.testsCompleted, activity.testsCompleted),
                    wordsTyped = maxOf(existing.wordsTyped, activity.wordsTyped),
                    charactersTyped = maxOf(existing.charactersTyped, activity.charactersTyped),
                    playTime = maxOf(existing.playTime, activity.playTime),
                    updatedAt = maxOf(existing.updatedAt ?: 0, activity.updatedAt ?: 0)
                )
            }
        }
        return merged
    }

    private fun readDailyActivity(): Map<String, DailyActivity> {
        val jsonString = settings.getStringOrNull(KEY_DAILY_ACTIVITY) ?: return emptyMap()
        return try {
            json.decodeFromString<DailyActivityDurations>(jsonString).durations
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun persistDailyActivity(activity: Map<String, DailyActivity>) {
        settings[KEY_DAILY_ACTIVITY] = json.encodeToString(DailyActivityDurations(activity))
    }

    fun writeDailyActivity(activity: Map<String, DailyActivity>) {
        persistDailyActivity(activity)
        _dailyActivityFlow.value = activity
    }

    private fun addDailyActivity(result: TypingTestResult) {
        val activity = getDailyActivity().toMutableMap()
        val dateKey = ActivityHeatmapRepository.dateKeyFromTimestamp(result.timestamp)
        val current = activity[dateKey] ?: DailyActivity(date = dateKey)

        activity[dateKey] = current.copy(
            testsCompleted = current.testsCompleted + 1,
            wordsTyped = current.wordsTyped + result.wordsTyped,
            charactersTyped = current.charactersTyped + result.characterCount,
            playTime = current.playTime + result.duration,
            updatedAt = result.timestamp
        )
        writeDailyActivity(activity)
    }

    fun isSyncPending(): Boolean {
        return settings.getBoolean(KEY_SYNC_PENDING, false)
    }

    fun setSyncPending(pending: Boolean) {
        settings[KEY_SYNC_PENDING] = pending
        _syncPendingFlow.value = pending
    }

    fun getStreakData(): StreakData {
        val jsonString = settings.getStringOrNull(KEY_STREAK)
            ?: return StreakData()

        return try {
            json.decodeFromString<StreakData>(jsonString)
        } catch (e: Exception) {
            StreakData()
        }
    }

    fun saveStreakData(data: StreakData) {
        settings[KEY_STREAK] = json.encodeToString(data)
        _streakFlow.value = data
    }

    fun clearAllData() {
        settings.remove(KEY_RESULTS)
        settings.remove(KEY_BEST_WPM)
        settings.remove(KEY_TOTAL_TESTS)
        settings.remove(KEY_DAILY_ACTIVITY)
        settings.remove(KEY_ACHIEVEMENT_PROGRESS)
        settings.remove(KEY_SOCIAL_SHARES)
        settings.remove(KEY_STREAK)
        settings.remove(KEY_USER_PROFILE)
        refreshStats()
    }

    private fun Settings.getStringOrNull(key: String): String? {
        return try {
            get<String>(key)
        } catch (e: Exception) {
            null
        }
    }

    private fun Settings.getIntOrNull(key: String): Int? {
        return try {
            get<Int>(key)
        } catch (e: Exception) {
            null
        }
    }
}
