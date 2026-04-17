package org.example.project.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.model.AppSettings
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserProfile

class StorageManager(private val settings: Settings) {
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val KEY_RESULTS = "typing_test_results"
        private const val KEY_SETTINGS = "app_settings"
        private const val KEY_BEST_WPM = "best_wpm"
        private const val KEY_TOTAL_TESTS = "total_tests"
        private const val KEY_USER_PROFILE = "user_profile"
    }

    private val _settingsFlow = MutableStateFlow(getSettings())
    val settingsFlow = _settingsFlow.asStateFlow()

    private val _userProfileFlow = MutableStateFlow(getUserProfile())
    val userProfileFlow = _userProfileFlow.asStateFlow()
    
    fun saveResult(result: TypingTestResult) {
        val results = getResults().toMutableList()
        results.add(0, result) // Add to beginning
        // Keep only last 100 results
        val limitedResults = results.take(100)
        settings[KEY_RESULTS] = json.encodeToString(limitedResults)
        
        // Update statistics
        updateBestWpm(result.wpm)
        incrementTotalTests()
    }
    
    fun getResults(): List<TypingTestResult> {
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
    
    fun clearAllData() {
        settings.remove(KEY_RESULTS)
        settings.remove(KEY_BEST_WPM)
        settings.remove(KEY_TOTAL_TESTS)
        settings.remove(KEY_USER_PROFILE)
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
