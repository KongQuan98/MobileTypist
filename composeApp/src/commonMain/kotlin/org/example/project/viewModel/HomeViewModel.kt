package org.example.project.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import org.example.project.data.QuotesData
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.TypingTestResult

class HomeViewModel(
    private val storageManager: StorageManager,
    private val coroutineScope: CoroutineScope
) {

    val modes = listOf(TypingMode.TIME, TypingMode.WORDS, TypingMode.QUOTES)
    val timeOptions = listOf(15, 30, 60)
    val wordOptions = listOf(10, 25, 50, 100)

    val typingTexts = modes.map {
        when (it) {
            TypingMode.TIME -> QuotesData.quotes.joinToString(" ")
            TypingMode.WORDS -> QuotesData.getQuoteForWords(wordOptions.first())
            TypingMode.QUOTES -> QuotesData.getRandomQuote()
        }
    }

    var showContent by mutableStateOf(true)
        private set

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