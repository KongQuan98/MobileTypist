package org.example.project.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Check
import compose.icons.feathericons.Smile
import org.example.project.MobileTypistTheme
import org.example.project.data.model.UserProfile
import org.example.project.ui.LocalHaptics
import org.example.project.ui.PreviewCompositionLocals
import org.example.project.ui.hapticClickable
import org.example.project.ui.wrap
import org.example.project.utils.AudioPlayer
import org.example.project.utils.SoundEffect
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditProfileScreen(
    audioPlayer: AudioPlayer? = null,
    userProfile: UserProfile,
    onSaveClicked: (UserProfile) -> Unit = {},
    onBackClicked: () -> Unit = {},
    onNavigateToSelectAvatar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf(userProfile.username) }
    var displayName by remember { mutableStateOf(userProfile.displayName) }
    var email by remember { mutableStateOf(userProfile.email) }
    var bio by remember { mutableStateOf(userProfile.bio) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                // Header
                TopHeaderBar(
                    audioPlayer,
                    onBackClicked
                )

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(20.dp))
                    // Avatar Section
                    AvatarProfileIcon(
                        username = username,
                        onNavigateToSelectAvatar = onNavigateToSelectAvatar
                    )

                    Spacer(Modifier.height(48.dp))

                    // Form Fields
                    EditField(
                        label = "USERNAME",
                        value = username,
                        onValueChange = { username = it },
                        prefix = "@ "
                    )
                    Spacer(Modifier.height(24.dp))
                    EditField(
                        label = "DISPLAY NAME",
                        value = displayName,
                        onValueChange = { displayName = it })
                    Spacer(Modifier.height(24.dp))
                    EditField(label = "EMAIL", value = email, onValueChange = { email = it })
                    Spacer(Modifier.height(24.dp))
                    EditField(
                        label = "BIO",
                        value = bio,
                        onValueChange = { bio = it },
                        singleLine = false,
                        minHeight = 100.dp
                    )

                    Spacer(Modifier.height(48.dp))
                }

                Spacer(Modifier.height(40.dp))
            }

            // Action Buttons
            DoubleActionButton(
                audioPlayer = audioPlayer,
                onSaveClicked = onSaveClicked,
                onBackClicked = onBackClicked,
                userProfile = userProfile,
                username = username,
                displayName = displayName,
                email = email,
                bio = bio
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TopHeaderBar(
    audioPlayer: AudioPlayer?,
    onBackClicked: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .hapticClickable {
                    audioPlayer?.play(SoundEffect.BUTTON_CLICK)
                    onBackClicked()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = FeatherIcons.ArrowLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "back",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Text(
            text = "edit profile",
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
private fun AvatarProfileIcon(
    username: String,
    onNavigateToSelectAvatar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (username.length >= 2) username.take(2)
                        .uppercase() else "??",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .hapticClickable { onNavigateToSelectAvatar() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FeatherIcons.Smile,
                    contentDescription = "Change Avatar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "tap icon to change avatar",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
private fun DoubleActionButton(
    audioPlayer: AudioPlayer?,
    onSaveClicked: (UserProfile) -> Unit,
    onBackClicked: () -> Unit,
    userProfile: UserProfile,
    username: String,
    displayName: String,
    email: String,
    bio: String
) {
    val haptics = LocalHaptics.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = {
                haptics.wrap(audioPlayer) {
                    onBackClicked()
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
        ) {
            Text(
                text = "cancel",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Button(
            onClick = {
                haptics.wrap(audioPlayer) {
                    onSaveClicked(
                        userProfile.copy(
                            username = username,
                            displayName = displayName,
                            email = email,
                            bio = bio
                        )
                    )
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = FeatherIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "save",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String = "",
    singleLine: Boolean = true,
    minHeight: androidx.compose.ui.unit.Dp = 56.dp
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            ),
            prefix = if (prefix.isNotEmpty()) {
                {
                    Text(
                        text = prefix,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            } else null,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine
        )
    }
}

@Preview
@Composable
private fun EditProfileScreenPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            EditProfileScreen(
                onBackClicked = {},
                onSaveClicked = {},
                userProfile = UserProfile()
            )
        }
    }
}

@Preview
@Composable
private fun EditProfileScreenLightPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            EditProfileScreen(
                onBackClicked = {},
                onSaveClicked = {},
                userProfile = UserProfile()
            )
        }
    }
}
