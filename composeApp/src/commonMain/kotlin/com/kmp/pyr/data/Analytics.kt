package com.kmp.pyr.data

import kotlin.math.roundToInt

enum class ReportPeriod(val label: String, val days: Int) {
    WEEKLY("Weekly", 7),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 360),
}

data class PeriodReport(
    val period: ReportPeriod,
    val totals: Map<ResourceType, Double>,
    val previousTotals: Map<ResourceType, Double>,
    val avgProductivity: Float,
    val prevAvgProductivity: Float,
    val series: List<DailyStat>,
    val bestResource: ResourceType,
    val worstResource: ResourceType,
) {
    val grandTotal: Double get() = totals.values.sum()
    val prevGrandTotal: Double get() = previousTotals.values.sum()
    val growth: Float
        get() = if (prevGrandTotal == 0.0) 0f else ((grandTotal - prevGrandTotal) / prevGrandTotal).toFloat()
    fun growthOf(type: ResourceType): Float {
        val prev = previousTotals[type] ?: 0.0
        val cur = totals[type] ?: 0.0
        return if (prev == 0.0) 0f else ((cur - prev) / prev).toFloat()
    }
}

object Analytics {

    fun report(snapshot: KingdomSnapshot, period: ReportPeriod): PeriodReport {
        val today = snapshot.today
        val stats = snapshot.dailyStats
        val current = stats.filter { it.day > today - period.days && it.day <= today }
        val previous = stats.filter { it.day > today - 2 * period.days && it.day <= today - period.days }

        fun totals(list: List<DailyStat>): Map<ResourceType, Double> =
            ResourceType.entries.associateWith { t -> list.sumOf { it.amount(t) } }

        val curTotals = totals(current)
        val growthByType = ResourceType.entries.associateWith { t ->
            val prev = previous.sumOf { it.amount(t) }
            val cur = curTotals[t] ?: 0.0
            if (prev == 0.0) 0.0 else (cur - prev) / prev
        }
        val best = growthByType.maxByOrNull { it.value }?.key ?: ResourceType.GOLD
        val worst = growthByType.minByOrNull { it.value }?.key ?: ResourceType.LABOR

        return PeriodReport(
            period = period,
            totals = curTotals,
            previousTotals = totals(previous),
            avgProductivity = current.map { it.productivity }.avgOr(0f),
            prevAvgProductivity = previous.map { it.productivity }.avgOr(0f),
            series = current,
            bestResource = best,
            worstResource = worst,
        )
    }

    /** Forecast finish day for a project from current milestone completion pace. */
    fun forecastFinishDay(project: Project, today: Int): Int {
        val done = project.milestones.count { it.done }
        val total = project.milestones.size
        if (total == 0 || done == 0) return project.endDay
        if (done == total) return today
        val elapsed = (today - project.startDay).coerceAtLeast(1)
        val pacePerDay = done.toFloat() / elapsed
        val remaining = total - done
        val daysLeft = (remaining / pacePerDay).roundToInt()
        return today + daysLeft
    }

    private fun List<Float>.avgOr(default: Float): Float =
        if (isEmpty()) default else sum() / size
}
