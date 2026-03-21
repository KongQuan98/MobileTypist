package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val darkTheme: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showStatistics: Boolean = true
)