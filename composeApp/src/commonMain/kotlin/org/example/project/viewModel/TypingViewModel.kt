package org.example.project.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.CharStatus
import org.example.project.data.TypingMode
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TypingViewModel(private val coroutineScope: CoroutineScope) {

    var timeLeft by mutableStateOf(0)
        private set
    var wordsTyped by mutableStateOf(0)
        private set
    var isRunning by mutableStateOf(false)
        private set
    var isFinished by mutableStateOf(false)
        private set
    var input by mutableStateOf("")
        private set
    var correctCount by mutableStateOf(0)
        private set
    var errorCount by mutableStateOf(0)
        private set
    var charStatuses = mutableStateListOf<CharStatus>()
        private set

    private var timerJob: Job? = null
    private var startTime by mutableStateOf(0L)
    var targetText = ""
    private var selectedTime = 0
    private var selectedWords = 0
    private var mode: TypingMode = TypingMode.TIME

    fun initialize(mode: TypingMode, targetText: String, selectedTime: Int, selectedWords: Int) {
        this.mode = mode
        this.targetText = targetText
        this.selectedTime = selectedTime
        this.selectedWords = selectedWords
        this.timeLeft = selectedTime
        charStatuses.clear()
        repeat(targetText.length) { charStatuses.add(CharStatus.Pending) }
    }

    fun onInputChanged(newInput: String) {
        if (!isRunning && !isFinished) {
            startTest()
        }

        // Handle character processing
    }

    fun startTest() {
        isRunning = true
        startTime = Clock.System.now().toEpochMilliseconds()
        if (mode == TypingMode.TIME) {
            startTimer()
        }
    }

    fun resetTest() {
        timerJob?.cancel()
        isRunning = false
        isFinished = false
        input = ""
        correctCount = 0
        errorCount = 0
        wordsTyped = 0
        if (mode == TypingMode.TIME) {
            timeLeft = selectedTime
        }
        charStatuses.forEachIndexed { index, _ -> charStatuses[index] = CharStatus.Pending }
    }

    private fun startTimer() {
        timerJob = coroutineScope.launch {
            while (timeLeft > 0 && isRunning) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0) {
                finishTest()
            }
        }
    }

    private fun finishTest() {
        isRunning = false
        isFinished = true
        val elapsedSeconds =
            ((Clock.System.now().toEpochMilliseconds() - startTime) / 1000).toInt().coerceAtLeast(1)
        val wpm = calculateWpm(correctCount, elapsedSeconds)
        val accuracy = calculateAccuracy(correctCount, errorCount)

        // The result is now handled within the ViewModel
        // onTestComplete(result)
    }

    private fun calculateWpm(correctChars: Int, elapsedSeconds: Int): Int {
        if (elapsedSeconds <= 0) return 0
        val words = correctChars / 5.0
        val minutes = elapsedSeconds / 60.0
        return kotlin.math.round(words / minutes).toInt()
    }

    private fun calculateAccuracy(correct: Int, errors: Int): Int {
        val total = correct + errors
        if (total == 0) return 100
        return kotlin.math.round((correct.toDouble() / total.toDouble()) * 100.0).toInt()
    }
}