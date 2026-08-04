package org.example.project.achievements.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

data class Achievement(
    val id: String,
    val title: StringResource,
    val description: StringResource,
    val icon: ImageVector,
    val hidden: Boolean,
    val progress: Long,
    val target: Long,
    val unlocked: Boolean,
    val unlockedAt: Long?
)
