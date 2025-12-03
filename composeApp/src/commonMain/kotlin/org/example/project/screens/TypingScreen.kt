package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.RefreshCw
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.CharStatus
import org.example.project.CleanTypingArea
import org.example.project.GlobalHiddenInputOverlay
import org.example.project.ResultBottomSheet
import org.example.project.StartMenu
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.TypingTestResult
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TypingScreen(
    mode: TypingMode,
    targetText: String,
    timeOptions: List<Int>? = null,
    wordOptions: List<Int>? = null,
    onTestComplete: (TypingTestResult) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = Color(0xFF1E3A8A)
    val onBg = Color(0xFFE5E7EB)
    val outline = Color(0xFF374151)
    val surface = Color(0xFF1F2937)
    val background = Color(0xFF111827)
    val onSurface = Color(0xFFF9FAFB)

    var selectedTime by remember { mutableIntStateOf(timeOptions?.firstOrNull() ?: 30) }
    var selectedWords by remember { mutableIntStateOf(wordOptions?.firstOrNull() ?: 10) }
    var timeLeft by remember { mutableIntStateOf(selectedTime) }
    var wordsTyped by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isTyping by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }
    var showTypingScreen by remember { mutableStateOf(false) }
    var showResultsScreen by remember { mutableStateOf(false) }

    var input by remember { mutableStateOf("") }
    var correctCount by remember { mutableIntStateOf(0) }
    var errorCount by remember { mutableIntStateOf(0) }
    var cumulativeInput by remember { mutableStateOf("") }
    var currentCharIndex by remember { mutableIntStateOf(0) }
    var currentWordStartIndex by remember { mutableIntStateOf(0) }
    var hasStartedCurrentWord by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    
    val charStatuses = remember(targetText) {
        mutableStateListOf<CharStatus>().apply {
            repeat(targetText.length) { add(CharStatus.Pending) }
        }
    }

    fun findWordBoundaries(text: String, startIndex: Int): Pair<Int, Int> {
        var wordStart = startIndex
        var wordEnd = startIndex
        while (wordStart > 0 && text[wordStart - 1] != ' ') {
            wordStart--
        }
        while (wordEnd < text.length && text[wordEnd] != ' ') {
            wordEnd++
        }
        return wordStart to wordEnd
    }

    fun findNextWordStart(text: String, currentIndex: Int): Int {
        var index = currentIndex
        while (index < text.length && text[index] != ' ') {
            index++
        }
        while (index < text.length && text[index] == ' ') {
            index++
        }
        return index
    }

    val scope = rememberCoroutineScope()
    var timerJob by remember { mutableStateOf<Job?>(null) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun resetTest() {
        timerJob?.cancel()
        if (mode == TypingMode.TIME) {
            timeLeft = selectedTime
        }
        wordsTyped = 0
        isRunning = false
        isTyping = false
        isFinished = false
        hasStarted = false
        showTypingScreen = false
        showResultsScreen = false
        input = ""
        cumulativeInput = ""
        correctCount = 0
        errorCount = 0
        currentCharIndex = 0
        currentWordStartIndex = 0
        hasStartedCurrentWord = false
        charStatuses.clear()
        keyboardController?.hide()
        repeat(targetText.length) { charStatuses.add(CharStatus.Pending) }
    }

    fun startTest() {
        hasStarted = true
        scope.launch {
            delay(500)
            showTypingScreen = true
            isTyping = true
            startTime = Clock.System.now().toEpochMilliseconds()
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun finishTest() {
        isRunning = false
        isTyping = false
        isFinished = true
        val elapsedSeconds = ((Clock.System.now().toEpochMilliseconds() - startTime) / 1000).toInt().coerceAtLeast(1)
        val wpm = calculateWpm(correctCount, elapsedSeconds)
        val accuracy = calculateAccuracy(correctCount, errorCount)
        
        val result = TypingTestResult(
            mode = mode,
            wpm = wpm,
            accuracy = accuracy,
            correctChars = correctCount,
            errorCount = errorCount,
            duration = elapsedSeconds,
            wordsTyped = wordsTyped
        )
        onTestComplete(result)
        
        scope.launch {
            delay(500)
            showResultsScreen = true
        }
    }

    fun startTimer() {
        if (isRunning) return
        isRunning = true
        timerJob = scope.launch {
            while (timeLeft > 0 && isRunning) {
                delay(1000)
                timeLeft -= 1
            }
            if (timeLeft == 0) {
                finishTest()
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            when (mode) {
                TypingMode.TIME -> {
                    StartMenu(
                        visible = !hasStarted && !isFinished,
                        timeOptions = timeOptions ?: listOf(15, 30, 60),
                        selectedTime = selectedTime,
                        onTimeSelected = { selectedTime = it; resetTest() },
                        onStart = { startTest() },
                        onSurface = onSurface,
                        onBg = onBg,
                        primary = primary,
                        outline = outline
                    )
                }
                TypingMode.WORDS -> {
                    WordsStartMenu(
                        visible = !hasStarted && !isFinished,
                        wordOptions = wordOptions ?: listOf(10, 25, 50, 100),
                        selectedWords = selectedWords,
                        onWordsSelected = { selectedWords = it; resetTest() },
                        onStart = { startTest() },
                        onSurface = onSurface,
                        onBg = onBg,
                        primary = primary,
                        outline = outline
                    )
                }
                TypingMode.QUOTES -> {
                    QuotesStartMenu(
                        visible = !hasStarted && !isFinished,
                        onStart = { startTest() },
                        onSurface = onSurface,
                        onBg = onBg,
                        primary = primary,
                        outline = outline
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = showTypingScreen && isTyping,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(500)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500)),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = androidx.compose.animation.core.tween(500)
                ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(500))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    // Top bar with timer/progress and back button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.TextButton(
                            onClick = { onBack() }
                        ) {
                            Text(
                                text = "← Back",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = onSurface
                                )
                            )
                        }
                        when (mode) {
                            TypingMode.TIME -> {
                                Text(
                                    text = "${timeLeft}s",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = onSurface
                                    )
                                )
                            }
                            TypingMode.WORDS -> {
                                Text(
                                    text = "$wordsTyped / $selectedWords words",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = onSurface
                                    )
                                )
                            }
                            TypingMode.QUOTES -> {
                                val progress = if (targetText.isNotEmpty()) {
                                    (currentCharIndex.toFloat() / targetText.length * 100).toInt()
                                } else 0
                                Text(
                                    text = "$progress%",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = onSurface
                                    )
                                )
                            }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CleanTypingArea(
                            targetText = targetText,
                            cumulativeInput = cumulativeInput,
                            input = input,
                            onInputChange = { },
                            enabled = !isFinished,
                            charStatuses = charStatuses.toList(),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(
                            onClick = { resetTest() },
                            modifier = Modifier.size(56.dp),
                            containerColor = background,
                            contentColor = onSurface
                        ) {
                            Icon(
                                imageVector = FeatherIcons.RefreshCw,
                                contentDescription = "Reset",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                ResultBottomSheet(
                    visible = showResultsScreen && isFinished,
                    correctCount = correctCount,
                    errorCount = errorCount,
                    selectedTime = ((Clock.System.now().toEpochMilliseconds() - startTime) / 1000).toInt().coerceAtLeast(1),
                    onReset = { resetTest() },
                    onSurface = onSurface,
                    onBg = onBg,
                    primary = primary
                )
            }
        }

        GlobalHiddenInputOverlay(
            value = input,
            onValueChange = { new ->
                if (!isRunning && !isFinished) {
                    if (mode == TypingMode.TIME) {
                        startTimer()
                    } else {
                        isRunning = true
                    }
                }

                if (new.length > input.length) {
                    val newChar = new.last()
                    if (newChar == ' ') {
                        if (hasStartedCurrentWord) {
                            val (wordStart, wordEnd) = findWordBoundaries(targetText, currentWordStartIndex)
                            val typedUpTo = currentCharIndex
                            val wordFullyTyped = typedUpTo >= wordEnd

                            if (!wordFullyTyped) {
                                for (i in typedUpTo until wordEnd) {
                                    if (charStatuses[i] == CharStatus.Pending) {
                                        charStatuses[i] = CharStatus.Incorrect
                                        errorCount++
                                    }
                                }
                            }
                            
                            wordsTyped++
                            if (mode == TypingMode.WORDS && wordsTyped >= selectedWords) {
                                finishTest()
                                return@GlobalHiddenInputOverlay
                            }
                        }
                        currentCharIndex = findNextWordStart(targetText, currentCharIndex)
                        currentWordStartIndex = currentCharIndex
                        hasStartedCurrentWord = false
                        input = ""
                    } else if (currentCharIndex < targetText.length) {
                        if (!hasStartedCurrentWord) {
                            currentWordStartIndex = currentCharIndex
                            hasStartedCurrentWord = true
                        }
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
                        
                        if (mode == TypingMode.QUOTES && currentCharIndex >= targetText.length) {
                            finishTest()
                            return@GlobalHiddenInputOverlay
                        }
                    }
                } else if (new.length < input.length) {
                    if (currentCharIndex > 0) {
                        currentCharIndex--
                        val (wordStart, _) = findWordBoundaries(targetText, currentWordStartIndex)
                        if (currentCharIndex < wordStart) {
                            currentWordStartIndex = currentCharIndex
                            hasStartedCurrentWord = false
                        }
                        if (charStatuses[currentCharIndex] != CharStatus.Correct) {
                            charStatuses[currentCharIndex] = CharStatus.Pending
                        }
                        input = new
                    }
                }
            },
            enabled = !isFinished,
            focusRequester = focusRequester,
        )
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

@Composable
private fun WordsStartMenu(
    visible: Boolean,
    wordOptions: List<Int>,
    selectedWords: Int,
    onWordsSelected: (Int) -> Unit,
    onStart: () -> Unit,
    onSurface: Color,
    onBg: Color,
    primary: Color,
    outline: Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500)),
        exit = androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(500))
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Words Mode",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(32.dp))
            Text(
                text = "Select word count:",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = onBg
                )
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                wordOptions.forEach { w ->
                    val selected = w == selectedWords
                    androidx.compose.material3.OutlinedButton(
                        onClick = { onWordsSelected(w) },
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) primary else Color.Transparent,
                            contentColor = if (selected) onSurface else onBg
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (selected) primary else outline
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("$w words")
                    }
                }
            }
            androidx.compose.foundation.layout.Spacer(Modifier.height(32.dp))
            androidx.compose.material3.Button(
                onClick = onStart,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium))
            }
        }
    }
}

@Composable
private fun QuotesStartMenu(
    visible: Boolean,
    onStart: () -> Unit,
    onSurface: Color,
    onBg: Color,
    primary: Color,
    outline: Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500)),
        exit = androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(500))
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quotes Mode",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
            Text(
                text = "Type inspiring quotes and passages",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = onBg
                )
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(32.dp))
            androidx.compose.material3.Button(
                onClick = onStart,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium))
            }
        }
    }
}

