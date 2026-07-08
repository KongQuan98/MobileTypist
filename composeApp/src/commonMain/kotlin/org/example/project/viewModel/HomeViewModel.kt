package org.example.project.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.example.project.data.repo.Difficulty
import org.example.project.data.repo.QuotesRepository
import org.example.project.data.repo.WordsRepository
import org.example.project.data.storage.StorageManager

class HomeViewModel(
    private val storageManager: StorageManager,
    private val coroutineScope: CoroutineScope,
) {

    val modes = listOf(TypingMode.TIME, TypingMode.WORDS, TypingMode.QUOTES)
    val timeOptions = listOf(15, 30, 60)
    val wordOptions = listOf(25, 50, 100)

    var selectedTime by mutableStateOf(30)
    var selectedWords by mutableStateOf(25)

    val typingTexts = mutableStateListOf<String>()

    var showContent by mutableStateOf(true)
        private set

    init {
        modes.forEach { _ -> typingTexts.add("") }
        refreshAllTexts()

        coroutineScope.launch {
            snapshotFlow { selectedWords }
                .drop(1)
                .collectLatest {
                    loadTextsForMode(TypingMode.WORDS)
                }
        }

        coroutineScope.launch {
            snapshotFlow { selectedTime }
                .drop(1)
                .collectLatest {
                    loadTextsForMode(TypingMode.TIME)
                }
        }
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

        coroutineScope.launch {
            val text = when (mode) {
                TypingMode.TIME -> WordsRepository.getRandomWords(Difficulty.EASY, 200)
                    .joinToString(" ")

                TypingMode.WORDS -> WordsRepository.getRandomWords(Difficulty.EASY, selectedWords)
                    .joinToString(" ")

                TypingMode.QUOTES -> QuotesRepository.getRandomQuote()
            }
            typingTexts[index] = text
        }
    }

    fun onStartTapped() {
        showContent = false
    }

    fun onBack() {
        showContent = true
        refreshAllTexts()
    }

    fun onTestComplete(result: TypingTestResult) {
        storageManager.saveResult(result)
    }
}
