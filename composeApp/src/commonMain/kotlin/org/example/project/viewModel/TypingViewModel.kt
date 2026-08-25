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
import org.example.project.data.storage.StorageManager
import org.example.project.screens.CharStatus
import org.example.project.utils.AudioPlayerApi
import org.example.project.utils.Haptics
import org.example.project.utils.SoundEffect
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

sealed class TypingScreenAction {
    data class OnTestComplete(val result: TypingTestResult) : TypingScreenAction()
    object OnNavigateBack : TypingScreenAction()
}

@OptIn(ExperimentalTime::class)
class TypingViewModel(
    private val coroutineScope: CoroutineScope,
    private val audioPlayer: AudioPlayerApi? = null,
    private val hapticFeedback: Haptics? = null,
    private val storageManager: StorageManager? = null,
) {

    var timeLeft by mutableStateOf(0)
        private set
    var isRunning by mutableStateOf(false)
        private set
    var isFinished by mutableStateOf(false)
        private set
    var isProcessing by mutableStateOf(false)
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

    var completedResult by mutableStateOf<TypingTestResult?>(null)
        private set

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

                hapticFeedback?.typingKey()
                if (newChar == ' ' && targetChar != ' ') {
                    // Standard logic: Jump to next word on space press
                    audioPlayer?.play(SoundEffect.KEY_PRESS_3) // Space often has a deeper sound
                    while (currentCharIndex < targetText.length && targetText[currentCharIndex] != ' ') {
                        charStatuses[currentCharIndex] = CharStatus.Incorrect
                        errorCount++
                        currentCharIndex++
                    }
                    // Also process the space itself if we found it
                    if (currentCharIndex < targetText.length && targetText[currentCharIndex] == ' ') {
                        charStatuses[currentCharIndex] = CharStatus.Correct
                        correctCount++
                        currentCharIndex++
                    }
                    input = new // Keep input sync
                } else {
                    // Normal character process
                    val randomKeySound = when ((0..2).random()) {
                        0 -> SoundEffect.KEY_PRESS_1
                        1 -> SoundEffect.KEY_PRESS_2
                        else -> SoundEffect.KEY_PRESS_3
                    }
                    audioPlayer?.play(randomKeySound)

                    if (newChar == targetChar) {
                        charStatuses[currentCharIndex] = CharStatus.Correct
                        correctCount++
                    } else {
                        charStatuses[currentCharIndex] = CharStatus.Incorrect
                        errorCount++
                    }
                    currentCharIndex++
                    input = new
                }
                updateLiveStats()
            }
        } else if (new.length < input.length) {
            // Backspace logic: Handle potential jumps
            // Standard backspace usually just goes back one character.
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
        completedResult = null
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

                if (timeLeft in 1..5) {
                    audioPlayer?.play(SoundEffect.TIMER_TICK)
                }
            }
            if (timeLeft == 0) {
                finishTest()
            }
        }
    }

    private fun finishTest() {
        isRunning = false
        timerJob?.cancel()
        updateLiveStats()

        coroutineScope.launch {
            isProcessing = true

            // Artificial delay to prevent misclicks and allow calculations to "settle"
            delay(800.milliseconds)

            // Ensure final point is captured
            if (wpmHistory.isEmpty() || wpmHistory.last() != currentWpm) {
                wpmHistory.add(currentWpm)
            }

            val finishedAt = Clock.System.now().toEpochMilliseconds()
            val durationSeconds = elapsedSeconds(finishedAt)
            val finalWpm = calculateWpm(correctCount, durationSeconds)

            // Check for new record
            val previousBest = storageManager?.getBestWpm() ?: 0
            val isNewRecord = finalWpm > previousBest

            hapticFeedback?.notificationSuccess()
            if (isNewRecord) {
                audioPlayer?.play(SoundEffect.NEW_RECORD)
            } else {
                audioPlayer?.play(SoundEffect.GAME_FINISH)
            }

            completedResult = TypingTestResult(
                mode = mode,
                wpm = finalWpm,
                accuracy = calculateAccuracy(correctCount, errorCount),
                correctChars = correctCount,
                errorCount = errorCount,
                timestamp = finishedAt,
                duration = durationSeconds,
                wordsTyped = correctCount / 5,
            )

            isFinished = true
            isProcessing = false
        }
    }

    private fun elapsedSeconds(finishedAt: Long): Int {
        if (startTime <= 0L) return 0
        return ((finishedAt - startTime) / 1000).toInt().coerceAtLeast(1)
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
