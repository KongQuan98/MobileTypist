package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.example.project.data.model.TypingTestResult
import org.example.project.data.storage.StorageManager

data class StatisticsUiState(
    val results: List<TypingTestResult> = emptyList(),
    val bestWpm: Int = 0,
    val totalTests: Int = 0,
    val dailyActivity: Map<String, org.example.project.data.model.DailyActivity> = emptyMap(),
    val isLoading: Boolean = false
)

class StatisticsViewModel(
    private val storageManager: StorageManager
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = combine(
        storageManager.resultsFlow,
        storageManager.bestWpmFlow,
        storageManager.totalTestsFlow,
        storageManager.dailyActivityFlow
    ) { results, bestWpm, totalTests, dailyActivity ->
        StatisticsUiState(
            results = results,
            bestWpm = bestWpm,
            totalTests = totalTests,
            dailyActivity = dailyActivity,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState(isLoading = true)
    )

    fun refreshData() {
        storageManager.refreshStats()
    }
}
