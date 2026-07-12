package org.example.project.achievements.requirements

import org.example.project.achievements.model.PlayerStatistics

class ReachWpmRequirement(private val targetWpm: Int) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.highestWpm.toLong(),
            target = targetWpm.toLong(),
            completed = stats.highestWpm >= targetWpm
        )
    }
}

class AccuracyRequirement(private val targetAccuracy: Int) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.highestAccuracy.toLong(),
            target = targetAccuracy.toLong(),
            completed = stats.highestAccuracy >= targetAccuracy
        )
    }
}

class TotalTestsRequirement(private val targetTests: Int) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.totalTests.toLong(),
            target = targetTests.toLong(),
            completed = stats.totalTests >= targetTests
        )
    }
}

class TotalKeystrokesRequirement(private val targetKeystrokes: Long) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.totalKeystrokes,
            target = targetKeystrokes,
            completed = stats.totalKeystrokes >= targetKeystrokes
        )
    }
}

class DurationRequirement(private val targetSeconds: Long) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.totalDurationSeconds,
            target = targetSeconds,
            completed = stats.totalDurationSeconds >= targetSeconds
        )
    }
}

class StreakRequirement(private val targetStreak: Int) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.maxStreak.toLong(),
            target = targetStreak.toLong(),
            completed = stats.maxStreak >= targetStreak
        )
    }
}

class EarlyBirdRequirement : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = if (stats.hasPracticedEarlyBird) 1 else 0,
            target = 1,
            completed = stats.hasPracticedEarlyBird
        )
    }
}

class NightOwlRequirement : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = if (stats.hasPracticedNightOwl) 1 else 0,
            target = 1,
            completed = stats.hasPracticedNightOwl
        )
    }
}

class PerfectTestsRequirement(private val targetConsecutive: Int) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.maxConsecutivePerfectTests.toLong(),
            target = targetConsecutive.toLong(),
            completed = stats.maxConsecutivePerfectTests >= targetConsecutive
        )
    }
}

class SocialShareRequirement(private val targetShares: Int) : Requirement {
    override fun evaluate(stats: PlayerStatistics): ProgressResult {
        return ProgressResult(
            current = stats.socialShares.toLong(),
            target = targetShares.toLong(),
            completed = stats.socialShares >= targetShares
        )
    }
}
