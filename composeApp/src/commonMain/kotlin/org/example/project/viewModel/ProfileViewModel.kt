package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.example.project.achievements.model.Achievement
import org.example.project.achievements.repository.AchievementRepository
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserProfile
import org.example.project.data.storage.StorageManager

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val recentTestResult: List<TypingTestResult> = emptyList(),
    val averageWpm: Int = 0,
    val bestWpm: Int = 0,
    val totalTests: Int = 0,
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val storageManager: StorageManager,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        storageManager.userProfileFlow,
        storageManager.resultsFlow,
        storageManager.bestWpmFlow,
        storageManager.totalTestsFlow,
        achievementRepository.achievements
    ) { userProfile, results, bestWpm, totalTests, achievements ->
        ProfileUiState(
            userProfile = userProfile,
            recentTestResult = results,
            bestWpm = bestWpm,
            totalTests = totalTests,
            achievements = achievements,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    fun refreshData() {
        storageManager.refreshStats()
    }
}
