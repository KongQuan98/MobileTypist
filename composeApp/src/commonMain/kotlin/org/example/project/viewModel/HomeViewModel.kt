package org.example.project.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.project.dailystreak.model.StreakEvent
import org.example.project.dailystreak.repository.StreakRepository
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.example.project.data.repo.Difficulty
import org.example.project.data.repo.QuotesRepository
import org.example.project.data.repo.WordsRepository
import org.example.project.data.storage.StorageManager

class HomeViewModel(
    private val storageManager: StorageManager,
    private val streakRepository: StreakRepository
) : ViewModel() {

    val modes = listOf(TypingMode.TIME, TypingMode.WORDS, TypingMode.QUOTES)
    val timeOptions = listOf(15, 30, 60)
    val wordOptions = listOf(25, 50, 100)

    private val _selectedTime = MutableStateFlow(30)
    val selectedTime = _selectedTime.asStateFlow()

    private val _selectedWords = MutableStateFlow(25)
    val selectedWords = _selectedWords.asStateFlow()

    val typingTexts = mutableStateListOf<String>()

    private var _showContent = MutableStateFlow(true)
    val showContent = _showContent.asStateFlow()

    init {
        modes.forEach { _ -> typingTexts.add("") }
        refreshAllTexts()

        viewModelScope.launch {
            selectedWords.collectLatest {
                loadTextsForMode(TypingMode.WORDS)
            }
        }

        viewModelScope.launch {
            selectedTime.collectLatest {
                loadTextsForMode(TypingMode.TIME)
            }
        }
    }

    fun setSelectedTime(time: Int) {
        _selectedTime.value = time
    }

    fun setSelectedWords(words: Int) {
        _selectedWords.value = words
    }

    fun onHomeScreenVisible() {
        refreshAllTexts()
    }

    fun refreshTextForMode(mode: TypingMode) {
        loadTextsForMode(mode)
    }

    fun refreshAllTexts() {
        modes.forEach { mode ->
            loadTextsForMode(mode)
        }
    }

    private fun loadTextsForMode(mode: TypingMode) {
        val index = modes.indexOf(mode)
        if (index == -1) return

        viewModelScope.launch {
            val text = when (mode) {
                TypingMode.TIME -> WordsRepository.getRandomWords(Difficulty.EASY, 200)
                    .joinToString(" ")

                TypingMode.WORDS -> WordsRepository.getRandomWords(
                    Difficulty.EASY,
                    selectedWords.value
                )
                    .joinToString(" ")

                TypingMode.QUOTES -> QuotesRepository.getRandomQuote()
            }
            typingTexts[index] = text
        }
    }

    fun onStartTapped() {
        _showContent.value = false
    }

    fun onBack() {
        _showContent.value = true
        refreshAllTexts()
    }

    fun onTestComplete(result: TypingTestResult) {
        storageManager.saveResult(result)
        viewModelScope.launch {
            streakRepository.recordPlay()
            val event = streakRepository.getPendingEvent()

            if (event != StreakEvent.None) {
                _streakEvent.value = event
            }
        }
    }

    // Streak behaviour region

    fun dismissStreakEvent() {
        viewModelScope.launch {

            streakRepository.consumeEvent()

            _streakEvent.value = null
        }
    }

    private val _streakEvent = MutableStateFlow<StreakEvent?>(null)
    val streakEvent = _streakEvent.asStateFlow()

    // end of region
}
