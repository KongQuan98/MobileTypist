package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.example.project.achievements.model.Achievement
import org.example.project.achievements.repository.AchievementRepository

class AchievementsViewModel(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    val achievements: StateFlow<List<Achievement>> = achievementRepository.achievements
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
