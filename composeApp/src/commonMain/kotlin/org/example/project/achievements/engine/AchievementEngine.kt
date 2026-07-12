package org.example.project.achievements.engine

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.example.project.achievements.model.AchievementProgress
import org.example.project.achievements.model.PlayerStatistics
import org.example.project.achievements.provider.AchievementDefinitions
import org.example.project.data.model.TypingTestResult
import org.example.project.utils.timestampToDateKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)

class AchievementEngine {

    fun calculateStatistics(
        results: List<TypingTestResult>,
        socialShares: Int = 0
    ): PlayerStatistics {
        val highestWpm = results.maxOfOrNull { it.wpm } ?: 0
        val highestAccuracy = results.maxOfOrNull { it.accuracy } ?: 0
        val totalTests = results.size
        val totalKeystrokes = results.sumOf { it.correctChars.toLong() + it.errorCount }
        val totalDurationSeconds = results.sumOf { it.duration.toLong() }
        val totalWords = results.sumOf { it.wordsTyped.toLong() }

        // Streak calculation
        val dailyDurations = results
            .filter { it.duration > 0 }
            .groupBy { timestampToDateKey(it.timestamp) }

        val sortedDates = dailyDurations.keys.map { LocalDate.parse(it) }.sortedDescending()

        var currentStreak = 0
        if (sortedDates.isNotEmpty()) {
            val today = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

            val yesterday = today.minus(1, DateTimeUnit.DAY)

            val dateSet = sortedDates.toSet()

            if (today !in dateSet && yesterday !in dateSet) {
                // Streak broken
            } else {
                var cursor = if (today in dateSet) today else yesterday

                while (cursor in dateSet) {
                    currentStreak++
                    cursor = cursor.minus(1, DateTimeUnit.DAY)
                }
            }
        }

        // Max streak
        var maxStreak = 0
        if (sortedDates.isNotEmpty()) {
            val allDatesSorted = sortedDates.sorted()
            var tempStreak = 1
            maxStreak = 1
            for (i in 1 until allDatesSorted.size) {
                if (allDatesSorted[i] == allDatesSorted[i - 1].plus(1, DateTimeUnit.DAY)) {
                    tempStreak++
                } else if (allDatesSorted[i] != allDatesSorted[i - 1]) {
                    tempStreak = 1
                }
                if (tempStreak > maxStreak) maxStreak = tempStreak
            }
        }

        // Early Bird (before 6am) / Night Owl (after 10pm)
        val hasPracticedEarlyBird = results.any {
            val time = Instant.fromEpochMilliseconds(it.timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault()).time
            time.hour < 6
        }
        val hasPracticedNightOwl = results.any {
            val time = Instant.fromEpochMilliseconds(it.timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault()).time
            time.hour >= 22
        }

        // Consecutive Perfect Tests
        var maxConsecutivePerfect = 0
        var currentConsecutivePerfect = 0
        results.sortedBy { it.timestamp }.forEach {
            if (it.accuracy == 100 && it.errorCount == 0) {
                currentConsecutivePerfect++
                if (currentConsecutivePerfect > maxConsecutivePerfect) {
                    maxConsecutivePerfect = currentConsecutivePerfect
                }
            } else {
                currentConsecutivePerfect = 0
            }
        }

        return PlayerStatistics(
            highestWpm = highestWpm,
            highestAccuracy = highestAccuracy,
            totalTests = totalTests,
            totalKeystrokes = totalKeystrokes,
            totalDurationSeconds = totalDurationSeconds,
            currentStreak = currentStreak,
            maxStreak = maxStreak,
            hasPracticedEarlyBird = hasPracticedEarlyBird,
            hasPracticedNightOwl = hasPracticedNightOwl,
            socialShares = socialShares,
            maxConsecutivePerfectTests = maxConsecutivePerfect,
            totalWords = totalWords
        )
    }

    fun evaluate(
        stats: PlayerStatistics,
        currentProgress: Map<String, AchievementProgress>
    ): List<AchievementProgress> {
        val updatedProgress = mutableListOf<AchievementProgress>()

        AchievementDefinitions.all.forEach { definition ->
            val saved = currentProgress[definition.id]
                ?: AchievementProgress(
                    achievementId = definition.id,
                    progress = 0,
                    unlocked = false,
                    unlockedAt = null
                )

            if (saved.unlocked) return@forEach

            val result = definition.requirement.evaluate(stats)

            if (result.current != saved.progress || result.completed) {
                updatedProgress.add(
                    saved.copy(
                        progress = result.current,
                        unlocked = result.completed,
                        unlockedAt = if (result.completed) Clock.System.now()
                            .toEpochMilliseconds() else null
                    )
                )
            }
        }

        return updatedProgress
    }
}
