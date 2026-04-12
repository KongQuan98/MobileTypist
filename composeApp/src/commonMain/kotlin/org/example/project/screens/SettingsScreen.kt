package org.example.project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.app_name
import mobiletypist.composeapp.generated.resources.settings_title
import mobiletypist.composeapp.generated.resources.version
import org.example.project.MobileTypistTheme
import org.example.project.data.model.AppSettings
import org.example.project.ui.LocalHaptics
import org.example.project.ui.hapticClickable
import org.example.project.ui.wrap
import org.example.project.utils.Haptics
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed interface SettingsScreenAction {
    object Back : SettingsScreenAction
    object ClearAllData : SettingsScreenAction
    data class SaveSettings(val settings: AppSettings) : SettingsScreenAction
}

@Composable
fun SettingsScreen(
    action: (SettingsScreenAction) -> Unit = {},
    appSettings: AppSettings = AppSettings(),
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(40.dp))

            // Header
            Text(
                text = stringResource(Res.string.settings_title),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(Modifier.height(40.dp))

            // Appearance Section
            SettingSectionHeader("// appearance")

            SettingNavRow(label = "theme", value = "dark minimal")
            SettingNavRow(label = "font size", value = "medium")
            SettingNavRow(label = "font family", value = "jetbrains mono")

            SettingToggleRow(
                label = "dark theme",
                checked = appSettings.darkTheme,
                onCheckedChange = {
                    action(SettingsScreenAction.SaveSettings(appSettings.copy(darkTheme = it)))
                }
            )

            Spacer(Modifier.height(40.dp))

            // Gameplay Section
            SettingSectionHeader("// gameplay")

            SettingToggleRow(
                label = "sound effects",
                checked = appSettings.soundEnabled,
                onCheckedChange = {
                    action(SettingsScreenAction.SaveSettings(appSettings.copy(soundEnabled = it)))
                }
            )

            SettingToggleRow(
                label = "haptic feedback",
                checked = appSettings.vibrationEnabled,
                onCheckedChange = {
                    action(SettingsScreenAction.SaveSettings(appSettings.copy(vibrationEnabled = it)))
                }
            )

            Spacer(Modifier.height(40.dp))

            // Account Section
            SettingSectionHeader("// account")

            SettingNavRow(label = "language", value = "english")

            Spacer(Modifier.height(40.dp))

            Text(
                text = "reset statistics",
                modifier = Modifier
                    .fillMaxWidth()
                    .hapticClickable { action(SettingsScreenAction.ClearAllData) }
                    .padding(vertical = 12.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "sign out",
                modifier = Modifier
                    .fillMaxWidth()
                    .hapticClickable { /* Handle Sign Out */ }
                    .padding(vertical = 12.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(64.dp))

            // Footer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "${stringResource(Res.string.app_name)} ${stringResource(Res.string.version)}",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingNavRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
        )
        Row(
            modifier = Modifier
                .hapticClickable {

                }
                .clip(RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.End,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = ">",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    )
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val haptics = LocalHaptics.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                haptics.wrap {
                    onCheckedChange(it)
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    CompositionLocalProvider(
        LocalHaptics provides PreviewHaptics
    ) {
        MobileTypistTheme(darkTheme = false) {
            SettingsScreen()
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreviewDarkTheme() {
    CompositionLocalProvider(
        LocalHaptics provides PreviewHaptics
    ) {
        MobileTypistTheme(darkTheme = true) {
            SettingsScreen()
        }
    }
}

// for preview purpose
object PreviewHaptics : Haptics {
    override fun buttonClick() {}
    override fun typingKey() {}
    override fun notificationSuccess() {}
    override fun selectionChange() {}
}