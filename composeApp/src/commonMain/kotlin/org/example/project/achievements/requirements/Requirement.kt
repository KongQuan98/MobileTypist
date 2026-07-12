package org.example.project.achievements.requirements

import org.example.project.achievements.model.PlayerStatistics

interface Requirement {
    fun evaluate(stats: PlayerStatistics): ProgressResult
}

data class ProgressResult(
    val current: Long,
    val target: Long,
    val completed: Boolean
)
