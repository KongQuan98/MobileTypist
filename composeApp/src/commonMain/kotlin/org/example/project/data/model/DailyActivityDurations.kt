package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyActivityDurations(
    val durations: Map<String, Int> = emptyMap(),
)
