package org.example.project.achievements.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val hidden: Boolean,
    val progress: Long,
    val target: Long,
    val unlocked: Boolean,
    val unlockedAt: Long?
)
