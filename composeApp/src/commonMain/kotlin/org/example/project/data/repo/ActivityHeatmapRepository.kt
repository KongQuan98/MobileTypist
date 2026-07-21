package org.example.project.data.repo

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.example.project.data.model.TypingTestResult
import org.example.project.utils.currentDateKey
import org.example.project.utils.timestampToDateKey

data class HeatmapCell(
    val day: Int?,
    val testCount: Int = 0,
    val dateKey: String? = null,
)

data class MonthHeatmapData(
    val year: Int,
    val month: Int,
    val weeks: List<List<HeatmapCell>>,
    val maxTestCount: Int,
)

data class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {
    override fun compareTo(other: YearMonth): Int {
        val yearCompare = year.compareTo(other.year)
        return if (yearCompare != 0) yearCompare else month.compareTo(other.month)
    }
}

object ActivityHeatmapRepository {

    fun dateKeyFromTimestamp(timestamp: Long): String {
        return timestampToDateKey(timestamp)
    }

    fun aggregateFromResults(results: List<TypingTestResult>): Map<String, Int> {
        return results
            .groupBy { dateKeyFromTimestamp(it.timestamp) }
            .mapValues { (_, dayResults) -> dayResults.size }
    }

    fun availableMonths(dailyActivity: Map<String, Int>): List<YearMonth> {
        val today = currentLocalDate()
        val end = YearMonth(today.year, today.monthNumber)

        if (dailyActivity.isEmpty()) {
            return listOf(end)
        }

        val earliestKey = dailyActivity.keys.min()
        val earliestDate = LocalDate.parse(earliestKey)
        val start = YearMonth(earliestDate.year, earliestDate.monthNumber)

        val months = mutableListOf<YearMonth>()
        var cursor = start
        while (cursor <= end) {
            months.add(cursor)
            cursor = nextMonth(cursor)
        }
        return months
    }

    fun buildMonthGrid(
        year: Int,
        month: Int,
        dailyActivity: Map<String, Int>,
    ): MonthHeatmapData {
        val firstDay = LocalDate(year, month, 1)
        val daysInMonth = daysInMonth(year, month)
        val leadingPadding = sundayFirstIndex(firstDay.dayOfWeek)
        val totalCells = leadingPadding + daysInMonth
        val weekCount = (totalCells + 6) / 7

        val weeks = List(weekCount) { weekIndex ->
            List(7) { rowIndex ->
                val position = weekIndex * 7 + rowIndex
                if (position < leadingPadding || position >= leadingPadding + daysInMonth) {
                    HeatmapCell(day = null)
                } else {
                    val day = position - leadingPadding + 1
                    val date = LocalDate(year, month, day)
                    val dateKey = date.toString()
                    val count = dailyActivity[dateKey] ?: 0
                    HeatmapCell(
                        day = day,
                        testCount = count,
                        dateKey = dateKey,
                    )
                }
            }
        }

        val maxCount = weeks
            .flatten()
            .maxOfOrNull { it.testCount }
            ?.coerceAtLeast(1)
            ?: 1

        return MonthHeatmapData(
            year = year,
            month = month,
            weeks = weeks,
            maxTestCount = maxCount,
        )
    }

    private fun currentLocalDate(): LocalDate {
        return LocalDate.parse(currentDateKey())
    }

    private fun sundayFirstIndex(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
        }
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    private fun nextMonth(yearMonth: YearMonth): YearMonth {
        return if (yearMonth.month == 12) {
            YearMonth(yearMonth.year + 1, 1)
        } else {
            YearMonth(yearMonth.year, yearMonth.month + 1)
        }
    }
}
