package org.example.project.data.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.model.AppSettings
import org.example.project.data.model.DailyActivityDurations
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserProfile
import org.example.project.data.repo.ActivityHeatmapRepository

class StorageManager(private val settings: Settings) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val KEY_RESULTS = "typing_test_results"
        private const val KEY_SETTINGS = "app_settings"
        private const val KEY_BEST_WPM = "best_wpm"
        private const val KEY_TOTAL_TESTS = "total_tests"
        private const val KEY_USER_PROFILE = "user_profile"
        private const val KEY_DAILY_ACTIVITY = "daily_activity_durations"
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

    private val _dailyActivityFlow = MutableStateFlow(getDailyActivityDurations())
    val dailyActivityFlow = _dailyActivityFlow.asStateFlow()

    fun saveResult(result: TypingTestResult) {
        val results = getResults().toMutableList()
        results.add(0, result)
        val limitedResults = results.take(100)
        settings[KEY_RESULTS] = json.encodeToString(limitedResults)

        // Update statistics in storage
        updateBestWpm(result.wpm)
        incrementTotalTests()
        addDailyActivity(result)

        // Trigger reactive updates for the current instance
        refreshStats()
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
        _dailyActivityFlow.value = getDailyActivityDurations()
    }

    private fun getResults(): List<TypingTestResult> {
        val jsonString = settings.getStringOrNull(KEY_RESULTS) ?: return emptyList()
        return try {
            json.decodeFromString<List<TypingTestResult>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
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

    fun getDailyActivityDurations(): Map<String, Int> {
        val stored = readDailyActivityDurations()
        if (stored.isNotEmpty()) {
            return stored
        }

        val migrated = ActivityHeatmapRepository.aggregateFromResults(getResults())
        if (migrated.isNotEmpty()) {
            writeDailyActivityDurations(migrated)
        }
        return migrated
    }

    private fun readDailyActivityDurations(): Map<String, Int> {
        val jsonString = settings.getStringOrNull(KEY_DAILY_ACTIVITY) ?: return emptyMap()
        return try {
            json.decodeFromString<DailyActivityDurations>(jsonString).durations
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun writeDailyActivityDurations(durations: Map<String, Int>) {
        settings[KEY_DAILY_ACTIVITY] = json.encodeToString(DailyActivityDurations(durations))
    }

    private fun addDailyActivity(result: TypingTestResult) {
        if (result.duration <= 0) return

        val durations = getDailyActivityDurations().toMutableMap()
        val dateKey = ActivityHeatmapRepository.dateKeyFromTimestamp(result.timestamp)
        durations[dateKey] = (durations[dateKey] ?: 0) + result.duration
        writeDailyActivityDurations(durations)
    }

    fun clearAllData() {
        settings.remove(KEY_RESULTS)
        settings.remove(KEY_BEST_WPM)
        settings.remove(KEY_TOTAL_TESTS)
        settings.remove(KEY_DAILY_ACTIVITY)
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
