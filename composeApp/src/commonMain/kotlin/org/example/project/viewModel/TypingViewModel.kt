package org.example.project.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.example.project.screens.CharStatus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

sealed class TypingScreenAction {
    data class OnTestComplete(val result: TypingTestResult) : TypingScreenAction()
    object OnNavigateBack : TypingScreenAction()
}

@OptIn(ExperimentalTime::class)
class TypingViewModel(private val coroutineScope: CoroutineScope) {

    var timeLeft by mutableStateOf(0)
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

    // Live stats
    var currentWpm by mutableStateOf(0)
        private set
    var currentAccuracy by mutableStateOf(100)
        private set
    var currentCharIndex by mutableStateOf(0)
        private set

    // Data for the graph
    val wpmHistory = mutableStateListOf<Int>()

    private var timerJob: Job? = null
    private var startTime by mutableStateOf(0L)
    var targetText = ""
    var selectedTime = 0
    private var selectedWords = 0
    var mode: TypingMode = TypingMode.TIME

    fun initialize(mode: TypingMode, targetText: String, selectedTime: Int, selectedWords: Int) {
        this.mode = mode
        this.targetText = targetText
        this.selectedTime = selectedTime
        this.selectedWords = selectedWords
        this.timeLeft = selectedTime
        this.currentCharIndex = 0
        this.currentWpm = 0
        this.currentAccuracy = 100
        charStatuses.clear()
        wpmHistory.clear()
        repeat(targetText.length) { charStatuses.add(CharStatus.Pending) }
    }

    fun onInputChanged(new: String) {
        if (isFinished) return
        
        if (!isRunning && !isFinished) {
            startTest()
        }

        if (new.length > input.length) {
            // Character added
            if (currentCharIndex < targetText.length) {
                val newChar = new.last()
                val targetChar = targetText[currentCharIndex]

                if (newChar == targetChar) {
                    charStatuses[currentCharIndex] = CharStatus.Correct
                    correctCount++
                } else {
                    charStatuses[currentCharIndex] = CharStatus.Incorrect
                    errorCount++
                }
                currentCharIndex++
                input = new
                updateLiveStats()
            }
        } else if (new.length < input.length) {
            // Backspace
            if (currentCharIndex > 0) {
                currentCharIndex--
                if (charStatuses[currentCharIndex] == CharStatus.Correct) {
                    correctCount--
                } else if (charStatuses[currentCharIndex] == CharStatus.Incorrect) {
                    errorCount--
                }
                charStatuses[currentCharIndex] = CharStatus.Pending
                input = new
                updateLiveStats()
            }
        }

        if (currentCharIndex >= targetText.length && mode != TypingMode.TIME) {
            finishTest()
        }
    }

    private fun updateLiveStats() {
        if (startTime == 0L) return
        val now = Clock.System.now().toEpochMilliseconds()
        val elapsedSeconds = ((now - startTime) / 1000).toInt().coerceAtLeast(1)
        currentWpm = calculateWpm(correctCount, elapsedSeconds)
        currentAccuracy = calculateAccuracy(correctCount, errorCount)
    }

    fun startTest() {
        if (isRunning) return
        isRunning = true
        startTime = Clock.System.now().toEpochMilliseconds()
        startDataTracking() // Start recording WPM points
        if (mode == TypingMode.TIME) {
            startTimer()
        }
    }

    private fun startDataTracking() {
        coroutineScope.launch {
            while (isRunning) {
                delay(1000)
                updateLiveStats()
                wpmHistory.add(currentWpm)
            }
        }
    }

    fun resetTest() {
        timerJob?.cancel()
        isRunning = false
        isFinished = false
        input = ""
        correctCount = 0
        errorCount = 0
        currentCharIndex = 0
        currentWpm = 0
        currentAccuracy = 100
        startTime = 0L
        wpmHistory.clear()
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
        timerJob?.cancel()
        updateLiveStats()
        // Ensure final point is captured
        if (wpmHistory.isEmpty() || wpmHistory.last() != currentWpm) {
            wpmHistory.add(currentWpm)
        }
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
