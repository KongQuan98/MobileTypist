package org.example.project.achievements.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.example.project.achievements.requirements.Requirement
import org.jetbrains.compose.resources.StringResource

data class AchievementDefinition(
    val id: String,
    val title: StringResource,
    val description: StringResource,
    val icon: ImageVector,
    val hidden: Boolean = false,
    val requirement: Requirement
)
