package org.example.project.ui

import androidx.compose.runtime.Composable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.heatmap_day_label
import mobiletypist.composeapp.generated.resources.heatmap_day_name_friday
import mobiletypist.composeapp.generated.resources.heatmap_day_name_monday
import mobiletypist.composeapp.generated.resources.heatmap_day_name_saturday
import mobiletypist.composeapp.generated.resources.heatmap_day_name_sunday
import mobiletypist.composeapp.generated.resources.heatmap_day_name_thursday
import mobiletypist.composeapp.generated.resources.heatmap_day_name_tuesday
import mobiletypist.composeapp.generated.resources.heatmap_day_name_wednesday
import mobiletypist.composeapp.generated.resources.heatmap_duration_hours
import mobiletypist.composeapp.generated.resources.heatmap_duration_minutes
import mobiletypist.composeapp.generated.resources.heatmap_duration_seconds
import mobiletypist.composeapp.generated.resources.heatmap_duration_zero
import mobiletypist.composeapp.generated.resources.heatmap_month_april
import mobiletypist.composeapp.generated.resources.heatmap_month_august
import mobiletypist.composeapp.generated.resources.heatmap_month_december
import mobiletypist.composeapp.generated.resources.heatmap_month_february
import mobiletypist.composeapp.generated.resources.heatmap_month_january
import mobiletypist.composeapp.generated.resources.heatmap_month_july
import mobiletypist.composeapp.generated.resources.heatmap_month_june
import mobiletypist.composeapp.generated.resources.heatmap_month_march
import mobiletypist.composeapp.generated.resources.heatmap_month_may
import mobiletypist.composeapp.generated.resources.heatmap_month_november
import mobiletypist.composeapp.generated.resources.heatmap_month_october
import mobiletypist.composeapp.generated.resources.heatmap_month_september
import mobiletypist.composeapp.generated.resources.heatmap_month_year
import org.example.project.data.repo.YearMonth
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun heatmapMonthName(month: Int): String {
    return stringResource(heatmapMonthResource(month))
}

@Composable
fun heatmapMonthYearLabel(yearMonth: YearMonth): String {
    return stringResource(
        Res.string.heatmap_month_year,
        heatmapMonthName(yearMonth.month),
        yearMonth.year,
    )
}

@Composable
fun heatmapDayLabel(year: Int, month: Int, day: Int): String {
    val date = LocalDate(year, month, day)
    return stringResource(
        Res.string.heatmap_day_label,
        heatmapDayOfWeekName(date.dayOfWeek),
        heatmapMonthName(month),
        day,
    )
}

@Composable
fun heatmapFormatDuration(seconds: Int): String {
    if (seconds <= 0) return stringResource(Res.string.heatmap_duration_zero)
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return when {
        hours > 0 -> stringResource(Res.string.heatmap_duration_hours, hours, minutes)
        minutes > 0 -> stringResource(Res.string.heatmap_duration_minutes, minutes, secs)
        else -> stringResource(Res.string.heatmap_duration_seconds, secs)
    }
}

@Composable
private fun heatmapDayOfWeekName(dayOfWeek: DayOfWeek): String {
    val resource = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> Res.string.heatmap_day_name_sunday
        DayOfWeek.MONDAY -> Res.string.heatmap_day_name_monday
        DayOfWeek.TUESDAY -> Res.string.heatmap_day_name_tuesday
        DayOfWeek.WEDNESDAY -> Res.string.heatmap_day_name_wednesday
        DayOfWeek.THURSDAY -> Res.string.heatmap_day_name_thursday
        DayOfWeek.FRIDAY -> Res.string.heatmap_day_name_friday
        DayOfWeek.SATURDAY -> Res.string.heatmap_day_name_saturday
    }
    return stringResource(resource)
}

private fun heatmapMonthResource(month: Int): StringResource {
    return when (month) {
        1 -> Res.string.heatmap_month_january
        2 -> Res.string.heatmap_month_february
        3 -> Res.string.heatmap_month_march
        4 -> Res.string.heatmap_month_april
        5 -> Res.string.heatmap_month_may
        6 -> Res.string.heatmap_month_june
        7 -> Res.string.heatmap_month_july
        8 -> Res.string.heatmap_month_august
        9 -> Res.string.heatmap_month_september
        10 -> Res.string.heatmap_month_october
        11 -> Res.string.heatmap_month_november
        12 -> Res.string.heatmap_month_december
        else -> Res.string.heatmap_month_january
    }
}
