package org.example.project.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.data.Difficulty
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.TypingTestResult
import org.example.project.data.WordsRepository

class HomeViewModel(
    private val storageManager: StorageManager,
    private val coroutineScope: CoroutineScope
) {

    val modes = listOf(TypingMode.TIME, TypingMode.WORDS, TypingMode.QUOTES)
    val timeOptions = listOf(15, 30, 60)
    val wordOptions = listOf(10, 25, 50, 100)

    val typingTexts = mutableStateListOf<String>()

    var showContent by mutableStateOf(true)
        private set

    init {
        modes.forEach { _ -> typingTexts.add("") }
        loadTexts()
    }

    private fun loadTexts() {
        coroutineScope.launch {
            modes.forEachIndexed { index, mode ->
                val text = when (mode) {
                    TypingMode.TIME -> WordsRepository.getRandomWords(Difficulty.EASY, 100)
                        .joinToString(" ")

                    TypingMode.WORDS -> WordsRepository.getRandomWords(Difficulty.EASY, 100)
                        .joinToString(" ")

                    TypingMode.QUOTES -> WordsRepository.getRandomWords(Difficulty.EASY, 100)
                        .joinToString(" ")
                }
                typingTexts[index] = text
            }
        }
    }

    fun onStartTapped() {
        showContent = false
    }

    fun onBack() {
        showContent = true
    }

    fun onTestComplete(result: TypingTestResult) {
        storageManager.saveResult(result)
    }
}