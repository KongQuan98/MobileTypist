package org.example.project.achievements.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.example.project.achievements.requirements.Requirement

data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val hidden: Boolean = false,
    val requirement: Requirement
)
