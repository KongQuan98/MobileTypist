package org.example.project.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.MobileTypistTheme
import org.example.project.data.model.AppSettings
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class SettingsScreenAction {
    object Back : SettingsScreenAction()
    object ClearAllData : SettingsScreenAction()
    data class SaveSettings(val settings: AppSettings) : SettingsScreenAction()
}

@Composable
fun SettingsScreen(
    action: (SettingsScreenAction) -> Unit = {},
    appSettings: AppSettings = AppSettings(),
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Transparent,
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
                text = "settings",
                style = TextStyle(
                    fontSize = 32.sp,
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

            Spacer(Modifier.height(32.dp))

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

            SettingToggleRow(label = "blind mode", checked = false, onCheckedChange = {})
            SettingToggleRow(label = "strict space", checked = false, onCheckedChange = {})

            Spacer(Modifier.height(32.dp))

            // Account Section
            SettingSectionHeader("// account")

            SettingNavRow(label = "language", value = "english")

            Text(
                text = "reset statistics",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { action(SettingsScreenAction.ClearAllData) }
                    .padding(vertical = 12.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
            )

            Text(
                text = "sign out",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle Sign Out */ }
                    .padding(vertical = 12.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(Modifier.height(64.dp))

            // Footer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "typekey v1.0.0",
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
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingNavRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate */ }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
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
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    MobileTypistTheme(darkTheme = false) {
        SettingsScreen()
    }
}

@Preview
@Composable
private fun SettingsScreenPreviewDarkTheme() {
    MobileTypistTheme(darkTheme = true) {
        SettingsScreen()
    }
}
