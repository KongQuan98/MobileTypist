package org.example.project.dailystreak.repository

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.dailystreak.model.StreakData
import org.example.project.dailystreak.model.StreakEvent
import org.example.project.data.storage.StorageManager
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface StreakRepository {

    suspend fun recordPlay()

    suspend fun getCurrentStreak(): Int

    suspend fun getData(): StreakData

    suspend fun getPendingEvent(): StreakEvent

    suspend fun consumeEvent()
}

class StreakRepositoryImpl(
    private val storageManager: StorageManager,
) : StreakRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun recordPlay() {

        val current = storageManager.getStreakData()

        val todayEpochDay = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .toEpochDays().toLong()

        // First ever play
        if (current.lastPlayedEpochDay == null) {

            storageManager.saveStreakData(
                current.copy(
                    currentStreak = 1,
                    longestStreak = 1,
                    firstPlayedEpochDay = todayEpochDay,
                    lastPlayedEpochDay = todayEpochDay,
                    pendingEvent = StreakEvent.FirstPlay
                )
            )

            return
        }

        val daysSinceLastPlay =
            todayEpochDay - current.lastPlayedEpochDay


        when (daysSinceLastPlay) {
            0L -> return // Already played today
            1L -> { // Played consecutive day
                val newStreak =
                    current.currentStreak + 1

                val event =
                    when (newStreak) {
                        2 -> StreakEvent.StreakStarted(newStreak)
                        7, 30, 100 -> StreakEvent.Milestone(newStreak)
                        else -> StreakEvent.StreakContinued(newStreak)
                    }

                storageManager.saveStreakData(
                    current.copy(
                        currentStreak = newStreak,
                        longestStreak = maxOf(
                            current.longestStreak,
                            newStreak
                        ),
                        lastPlayedEpochDay = todayEpochDay,
                        pendingEvent = event
                    )
                )
            }

            else -> { // Missed days
                val previousStreak = current.currentStreak
                val event =
                    if (daysSinceLastPlay >= 5) {
                        StreakEvent.WelcomeBack(daysAway = daysSinceLastPlay.toInt())
                    } else {
                        StreakEvent.StreakBroken(previousStreak)
                    }

                storageManager.saveStreakData(
                    current.copy(
                        currentStreak = 1,
                        longestStreak = current.longestStreak,
                        lastPlayedEpochDay = todayEpochDay,
                        pendingEvent = event
                    )
                )
            }
        }
    }

    override suspend fun getCurrentStreak(): Int {
        return storageManager.getStreakData().currentStreak
    }

    override suspend fun getData(): StreakData {
        return storageManager.getStreakData()
    }

    override suspend fun getPendingEvent(): StreakEvent {
        return storageManager.getStreakData().pendingEvent ?: StreakEvent.None
    }

    override suspend fun consumeEvent() {

        val streak = storageManager.getStreakData()

        storageManager.saveStreakData(
            streak.copy(
                pendingEvent = null
            )
        )
    }
}