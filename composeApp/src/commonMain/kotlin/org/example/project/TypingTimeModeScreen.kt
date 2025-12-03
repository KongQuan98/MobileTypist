package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun TypingTimeModeScreen(
    modifier: Modifier = Modifier,
) {
    // Dark theme colors with dark blue accents
    val primary = Color(0xFF1E3A8A) // Dark blue
    val primaryVariant = Color(0xFF1E40AF) // Lighter dark blue
    val onBg = Color(0xFFE5E7EB) // Light gray for text
    val outline = Color(0xFF374151) // Dark gray for borders
    val surface = Color(0xFF1F2937) // Dark surface
    val background = Color(0xFF111827) // Very dark background
    val onSurface = Color(0xFFF9FAFB) // Light text on surface

    var timeOptions = listOf(15, 30, 60)
    var selectedTime by remember { mutableIntStateOf(30) }
    var timeLeft by remember { mutableIntStateOf(selectedTime) }
    var isRunning by remember { mutableStateOf(false) }
    var isTyping by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }
    var showTypingScreen by remember { mutableStateOf(false) }
    var showResultsScreen by remember { mutableStateOf(false) }

    var input by remember { mutableStateOf("") }
    val targetText = remember {
        // Longer text content that can last for time constraints
        "The quick brown fox jumps over the lazy dog. This is a comprehensive typing test designed to challenge your speed and accuracy. " +
                "Practice makes perfect when it comes to improving your typing skills. The more you type, the faster and more accurate you become. " +
                "Consistency is key in developing muscle memory for efficient typing. Focus on accuracy first, then gradually increase your speed. " +
                "Remember to maintain proper posture and finger positioning while typing. Take breaks to avoid strain and maintain productivity. " +
                "Technology has revolutionized the way we communicate through written text. Digital literacy includes mastering keyboard skills. " +
                "Many professions require excellent typing abilities for success in today's workplace. Start with basic exercises and progress gradually. " +
                "Challenge yourself with different types of content including numbers, symbols, and special characters. Regular practice will yield results. " +
                "Set realistic goals and track your progress over time. Celebrate small improvements and stay motivated throughout your learning journey."
    }

    var correctCount by remember { mutableIntStateOf(0) }
    var errorCount by remember { mutableIntStateOf(0) }
    // Tracks text the user has already committed (e.g., after pressing space)
    var cumulativeInput by remember { mutableStateOf("") }
    var currentCharIndex by remember { mutableIntStateOf(0) }
    var currentWordStartIndex by remember { mutableIntStateOf(0) } // Start index of current word
    var hasStartedCurrentWord by remember { mutableStateOf(false) } // Track if user started typing current word
    val charStatuses = remember(targetText) { 
        mutableStateListOf<CharStatus>().apply {
            repeat(targetText.length) { add(CharStatus.Pending) }
        }
    }
    
    // Helper function to find word boundaries
    fun findWordBoundaries(text: String, startIndex: Int): Pair<Int, Int> {
        var wordStart = startIndex
        var wordEnd = startIndex
        
        // Move back to find word start
        while (wordStart > 0 && text[wordStart - 1] != ' ') {
            wordStart--
        }
        
        // Move forward to find word end (space or end of text)
        while (wordEnd < text.length && text[wordEnd] != ' ') {
            wordEnd++
        }
        
        return wordStart to wordEnd
    }
    
    // Helper function to find next word start
    fun findNextWordStart(text: String, currentIndex: Int): Int {
        var index = currentIndex
        // Skip current word if we're in the middle
        while (index < text.length && text[index] != ' ') {
            index++
        }
        // Skip spaces
        while (index < text.length && text[index] == ' ') {
            index++
        }
        return index
    }

    val scope = rememberCoroutineScope()
    var timerJob by remember { mutableStateOf<Job?>(null) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun resetTest(newTime: Int = selectedTime) {
        timerJob?.cancel()
        selectedTime = newTime
        timeLeft = newTime
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

    // We render the full target text with natural wrapping; no manual line splitting

    fun startTest() {
        hasStarted = true
        scope.launch {
            delay(500) // Wait for fade out animation
            showTypingScreen = true
            isTyping = true
            // Ensure keyboard opens and focus moves to the hidden input
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun startTimer() {
        if (isRunning) return
        isRunning = true
        timerJob = scope.launch {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            isRunning = false
            isTyping = false
            isFinished = true
            delay(500) // Wait for fade out animation
            showResultsScreen = true
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
                // Don't add IME padding to keep content fixed when keyboard appears
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Start Menu
            StartMenu(
                visible = !hasStarted && !isFinished,
                timeOptions = timeOptions,
                selectedTime = selectedTime,
                onTimeSelected = { resetTest(it) },
                onStart = { startTest() },
                onSurface = onSurface,
                onBg = onBg,
                primary = primary,
                outline = outline
            )
            // Typing mode with slide animation
            AnimatedVisibility(
                visible = showTypingScreen && isTyping,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500)),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    // Timer at top
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "${timeLeft}s",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = onSurface
                            )
                        )
                    }

                    // Typing area in center
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CleanTypingArea(
                            targetText = targetText,
                            cumulativeInput = cumulativeInput,
                            input = input,
                            onInputChange = { }, // Handled by GlobalHiddenInputOverlay
                            enabled = timeLeft > 0 && !(!isRunning && timeLeft == 0),
                            charStatuses = charStatuses,
                        )
                    }

                    // Reset button at bottom
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

            // Scrollable Bottom Sheet for Results
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
                    selectedTime = selectedTime,
                    onReset = { resetTest() },
                    onSurface = onSurface,
                    onBg = onBg,
                    primary = primary
                )
            }
        }

        // Invisible input overlay that moves with IME; must be outside the AnimatedVisibility to always host focus
        GlobalHiddenInputOverlay(
            value = input,
            onValueChange = { new ->
                if (!isRunning && timeLeft > 0) startTimer()

                // Character-by-character typing
                if (new.length > input.length) {
                    // Character added
                    val newChar = new.last()
                    if (newChar == ' ') {
                        // Space pressed - handle word completion or skipping
                        if (hasStartedCurrentWord) {
                            // User started typing this word but pressed space before finishing
                            // Mark entire current word as skipped (red) - but preserve characters already marked as correct
                            val (wordStart, wordEnd) = findWordBoundaries(targetText, currentWordStartIndex)
                            
                            // Check if we've typed all characters in the word
                            val typedUpTo = currentCharIndex
                            val wordFullyTyped = typedUpTo >= wordEnd
                            
                            if (!wordFullyTyped) {
                                // Word not fully typed - mark remaining characters as skipped (red)
                                for (i in typedUpTo until wordEnd) {
                                    if (charStatuses[i] == CharStatus.Pending) {
                                        charStatuses[i] = CharStatus.Incorrect // Mark as skipped/incorrect (red)
                                        errorCount++
                                    }
                                }
                            }
                            // If word was fully typed, characters are already marked correctly (blue)
                        }
                        
                        // Move to next word
                        currentCharIndex = findNextWordStart(targetText, currentCharIndex)
                        currentWordStartIndex = currentCharIndex
                        hasStartedCurrentWord = false
                        input = ""
                    } else if (currentCharIndex < targetText.length) {
                        // Regular character typing
                        if (!hasStartedCurrentWord) {
                            // Starting a new word
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
                    }
                } else if (new.length < input.length) {
                    // Character removed (backspace)
                    if (currentCharIndex > 0) {
                        currentCharIndex--
                        // If we backspaced to before word start, reset word start tracking
                        val (wordStart, _) = findWordBoundaries(targetText, currentWordStartIndex)
                        if (currentCharIndex < wordStart) {
                            currentWordStartIndex = currentCharIndex
                            hasStartedCurrentWord = false
                        }
                        // Reset status to pending, but only if it wasn't already correct in a completed word
                        if (charStatuses[currentCharIndex] != CharStatus.Correct) {
                            charStatuses[currentCharIndex] = CharStatus.Pending
                        }
                        input = new
                    }
                }
            },
            enabled = timeLeft > 0 && !(!isRunning && timeLeft == 0),
            focusRequester = focusRequester,
        )
    }
}

// Global invisible input overlay that follows the IME, keeping visible UI fixed
@Composable
fun GlobalHiddenInputOverlay(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    focusRequester: FocusRequester,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(0.dp)
                .windowInsetsPadding(WindowInsets.ime)
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontSize = 1.sp,
                color = Color.Transparent
            ),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Transparent,
                focusedTextColor = Color.Transparent,
                unfocusedTextColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}


@Composable
fun CleanTypingArea(
    targetText: String,
    cumulativeInput: String,
    input: String,
    onInputChange: (String) -> Unit,
    enabled: Boolean,
    charStatuses: List<CharStatus>,
) {
    val outline = Color(0xFF374151) // Dark gray for borders
    val onBg = Color(0xFFE5E7EB) // Light gray for text
    val surface = Color(0xFF1F2937) // Dark surface
    val primary = Color(0xFF1E3A8A) // Dark blue

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, outline, RoundedCornerShape(12.dp))
                .background(surface)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            // Character-level coloring: default grey, red for incorrect, blue for correct
            val annotated = buildAnnotatedString {
                append(targetText)
                // Base: mark all content as grey (untyped)
                addStyle(SpanStyle(color = onBg.copy(alpha = 0.4f)), 0, targetText.length)
                // Override characters based on their status
                for (i in charStatuses.indices) {
                    if (i < targetText.length) {
                        val color = when (charStatuses[i]) {
                            CharStatus.Correct -> primary
                            CharStatus.Incorrect -> Color(0xFFE57373)
                            CharStatus.Pending -> onBg.copy(alpha = 0.4f)
                        }
                        addStyle(SpanStyle(color = color), i, i + 1)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp) // extra horizontal padding inside viewer
            ) {
                Text(
                    text = annotated,
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 24.sp
                    )
                )
            }
        }

        // No local hidden input; handled by GlobalHiddenInputOverlay
    }
}

enum class CharStatus { Pending, Correct, Incorrect }

private fun compareText(target: String, typed: String): Pair<Int, Int> {
    var correct = 0
    var errors = 0
    val minLen = minOf(target.length, typed.length)
    for (i in 0 until minLen) {
        if (target[i] == typed[i]) correct++ else errors++
    }
    // Extra typed characters beyond target count as errors
    if (typed.length > target.length) {
        errors += (typed.length - target.length)
    }
    return correct to errors
}



