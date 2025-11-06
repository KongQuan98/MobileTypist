package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultBottomSheet(
    visible: Boolean,
    correctCount: Int,
    errorCount: Int,
    selectedTime: Int,
    onReset: () -> Unit,
    onSurface: Color,
    onBg: Color,
    primary: Color,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    // Animate scroll position when appearing/disappearing
    val scrollOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else scrollState.maxValue.toFloat(),
        animationSpec = tween(600),
        label = "scrollOffset"
    )
    
    // Trigger scroll animation when visibility changes
    LaunchedEffect(visible) {
        if (visible) {
            isVisible = true
            // Small delay to ensure the component is rendered before scrolling
            kotlinx.coroutines.delay(50)
            scrollState.animateScrollTo(0)
        } else {
            scrollState.animateScrollTo(scrollState.maxValue)
            kotlinx.coroutines.delay(300) // Wait for scroll animation
            isVisible = false
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(600)
        ) + fadeIn(animationSpec = tween(600)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(600)
        ) + fadeOut(animationSpec = tween(600))
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ),
            color = Color(0xFF1F2937), // Dark surface
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Handle bar
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(onBg.copy(alpha = 0.3f))
                )
                
                Spacer(Modifier.height(24.dp))
                
                Text(
                    text = "Test Complete!",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                )
                
                Spacer(Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val finalWpm = calculateWpm(correctCount, selectedTime)
                    val finalAccuracy = calculateAccuracy(correctCount, errorCount)
                    
                    StatBox(
                        title = "WPM", 
                        value = finalWpm.toString(), 
                        titleColor = onBg, 
                        valueColor = onSurface
                    )
                    StatBox(
                        title = "Accuracy", 
                        value = "$finalAccuracy%", 
                        titleColor = onBg, 
                        valueColor = onSurface
                    )
                    StatBox(
                        title = "Correct", 
                        value = correctCount.toString(), 
                        titleColor = onBg, 
                        valueColor = onSurface
                    )
                    StatBox(
                        title = "Errors", 
                        value = errorCount.toString(), 
                        titleColor = onBg, 
                        valueColor = onSurface
                    )
                }
                
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = onReset,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        contentColor = onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Try Again",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatBox(title: String, value: String, titleColor: Color, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 12.sp,
                color = titleColor
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
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
