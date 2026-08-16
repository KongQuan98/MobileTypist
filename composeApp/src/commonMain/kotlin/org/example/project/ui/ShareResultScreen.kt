package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.icons.FeatherIcons
import compose.icons.feathericons.Copy
import compose.icons.feathericons.Download
import compose.icons.feathericons.Share2
import compose.icons.feathericons.X
import kotlinx.coroutines.launch
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.app_icon
import mobiletypist.composeapp.generated.resources.app_name
import mobiletypist.composeapp.generated.resources.back
import mobiletypist.composeapp.generated.resources.result_words_per_minute
import mobiletypist.composeapp.generated.resources.share_result_accuracy
import mobiletypist.composeapp.generated.resources.share_result_app_url
import mobiletypist.composeapp.generated.resources.share_result_button
import mobiletypist.composeapp.generated.resources.share_result_copy_result
import mobiletypist.composeapp.generated.resources.share_result_footer_hint
import mobiletypist.composeapp.generated.resources.share_result_keep_typing
import mobiletypist.composeapp.generated.resources.share_result_message
import mobiletypist.composeapp.generated.resources.share_result_mode_quotes
import mobiletypist.composeapp.generated.resources.share_result_mode_seconds
import mobiletypist.composeapp.generated.resources.share_result_mode_words
import mobiletypist.composeapp.generated.resources.share_result_save_image
import mobiletypist.composeapp.generated.resources.share_result_score
import mobiletypist.composeapp.generated.resources.share_result_streak
import mobiletypist.composeapp.generated.resources.share_result_title
import mobiletypist.composeapp.generated.resources.share_result_words_per_minute_short
import org.example.project.MobileTypistTheme
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserProfile
import org.example.project.di.LocalAppContainer
import org.example.project.utils.PreviewCompositionLocals
import org.example.project.utils.shareImage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ShareResultScreen(
    visible: Boolean,
    result: TypingTestResult,
    userProfile: UserProfile,
    streak: Int,
    onDismiss: () -> Unit
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        ShareResultContent(
            result = result,
            userProfile = userProfile,
            streak = streak,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun ShareResultContent(
    result: TypingTestResult,
    userProfile: UserProfile,
    streak: Int,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val appContainer = LocalAppContainer.current
        val clipboardManager = LocalClipboardManager.current
        val coroutineScope = rememberCoroutineScope()
        val graphicsLayer = rememberGraphicsLayer()

        val shareMessage = stringResource(
            Res.string.share_result_message,
            result.wpm,
            result.accuracy,
            streak,
            stringResource(Res.string.share_result_app_url)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = FeatherIcons.X,
                        contentDescription = stringResource(Res.string.back),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = stringResource(Res.string.share_result_title),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(32.dp))

            // The Card
            Box(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .background(Color.Transparent)
                    .padding(4.dp) // Slight padding to ensure shadow/border isn't cut off
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            ) {
                ShareCard(
                    result = result,
                    userProfile = userProfile,
                    streak = streak
                )
            }

            Spacer(Modifier.weight(1f))

            Spacer(Modifier.height(16.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Share Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            shareImage(bitmap, shareMessage)
                            appContainer.storageManager.incrementSocialShares()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = FeatherIcons.Share2,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(Res.string.share_result_button),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                // Secondary Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SecondaryActionButton(
                        modifier = Modifier.weight(1f),
                        icon = FeatherIcons.Download,
                        text = stringResource(Res.string.share_result_save_image),
                        onClick = {
                            coroutineScope.launch {
                                val bitmap = graphicsLayer.toImageBitmap()
                                shareImage(bitmap, "My Typely Result")
                            }
                        }
                    )
                    SecondaryActionButton(
                        modifier = Modifier.weight(1f),
                        icon = FeatherIcons.Copy,
                        text = stringResource(Res.string.share_result_copy_result),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(shareMessage))
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FeatherIcons.Share2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.share_result_footer_hint),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ShareCard(
    result: TypingTestResult,
    userProfile: UserProfile,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val score = result.correctChars + result.errorCount // Simplified score

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                RoundedCornerShape(32.dp)
            )
    ) {
        // Glow effect at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(200f, 0f),
                        radius = 400f
                    )
                )
        )

        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(
                                1.5.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.app_icon),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(Res.string.app_name).uppercase(),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (result.mode) {
                            TypingMode.TIME -> stringResource(
                                Res.string.share_result_mode_seconds,
                                result.duration
                            )

                            TypingMode.WORDS -> stringResource(
                                Res.string.share_result_mode_words,
                                result.wordsTyped
                            )

                            TypingMode.QUOTES -> stringResource(Res.string.share_result_mode_quotes)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // User Info
            Text(
                text = "@${userProfile.username}",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(Modifier.height(48.dp))

            // Main Stats
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = result.wpm.toString(),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 100.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(Res.string.share_result_words_per_minute_short),
                        modifier = Modifier.padding(bottom = 20.dp),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Text(
                    text = stringResource(Res.string.result_words_per_minute),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(48.dp))

            // Bottom Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.share_result_accuracy),
                    value = "${result.accuracy}%"
                )
                StatBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.share_result_score),
                    value = score.toString()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Streak Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = stringResource(Res.string.share_result_streak, streak),
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            // Typist Letters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                "TYPIST".forEach { char ->
                    LetterBox(char.toString())
                }
            }

            Spacer(Modifier.height(24.dp))

            // Footer Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.share_result_keep_typing),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = stringResource(Res.string.share_result_app_url),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun LetterBox(letter: String) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier.height(80.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
private fun SecondaryActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewShareResultScreenDarkTheme() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            ShareResultContent(
                result = TypingTestResult(
                    mode = TypingMode.TIME,
                    wpm = 75,
                    accuracy = 95,
                    correctChars = 100,
                    errorCount = 5,
                ),
                userProfile = UserProfile(
                    username = "johndoe",
                ),
                streak = 3,
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun PreviewShareResultScreenLightTheme() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            ShareResultContent(
                result = TypingTestResult(
                    mode = TypingMode.TIME,
                    wpm = 75,
                    accuracy = 95,
                    correctChars = 100,
                    errorCount = 5,
                ),
                userProfile = UserProfile(
                    username = "johndoe",
                ),
                streak = 3,
                onDismiss = {}
            )
        }
    }
}
