package com.kmp.pyr.data

import kotlin.math.abs
import kotlin.math.roundToLong

/** Egyptian-flavoured month names for the synthetic 12×30 calendar. */
private val MONTHS = listOf(
    "Thoth", "Phaophi", "Athyr", "Choiak", "Tybi", "Mechir",
    "Phamenoth", "Pharmuthi", "Pachons", "Payni", "Epiphi", "Mesore",
)

object Fmt {
    /** day index -> "Pachons 14, Yr 1" */
    fun date(day: Int): String {
        val year = day / 360 + 1
        val within = day % 360
        val month = MONTHS[(within / 30).coerceIn(0, 11)]
        val d = within % 30 + 1
        return "$month $d, Yr $year"
    }

    fun relativeDay(day: Int, today: Int): String = when (val diff = today - day) {
        0 -> "Today"
        1 -> "Yesterday"
        in 2..6 -> "$diff days ago"
        in 7..13 -> "Last week"
        else -> date(day)
    }

    /** Compact number: 12.4K, 3.1M */
    fun compact(value: Double): String {
        val v = abs(value)
        val sign = if (value < 0) "-" else ""
        return when {
            v >= 1_000_000 -> sign + oneDecimal(v / 1_000_000) + "M"
            v >= 1_000 -> sign + oneDecimal(v / 1_000) + "K"
            else -> sign + value.roundToLong().toString()
        }
    }

    /** One decimal place, dropping a trailing ".0". */
    private fun oneDecimal(value: Double): String {
        val scaled = (value * 10).roundToLong()
        val whole = scaled / 10
        val frac = scaled % 10
        return if (frac == 0L) whole.toString() else "$whole.$frac"
    }

    fun number(value: Double): String {
        val rounded = value.roundToLong()
        val s = abs(rounded).toString()
        val sb = StringBuilder()
        for ((i, c) in s.withIndex()) {
            if (i > 0 && (s.length - i) % 3 == 0) sb.append(',')
            sb.append(c)
        }
        return (if (rounded < 0) "-" else "") + sb
    }

    fun signed(value: Double): String = (if (value >= 0) "+" else "") + compact(value)

    fun percent(p: Float): String = "${(p * 100).roundToLong()}%"
}
