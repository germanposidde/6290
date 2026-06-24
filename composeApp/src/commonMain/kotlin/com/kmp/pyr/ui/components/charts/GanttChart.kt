package com.kmp.pyr.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.ui.motif.DashEffect
import com.kmp.pyr.ui.theme.EgyptColors

data class GanttRow(
    val label: String,
    val startDay: Int,
    val endDay: Int,
    val progress: Float,
    val color: Color,
    val milestoneDays: List<Int> = emptyList(),
)

/** A simple Gantt timeline: one bar per project across a shared day axis, with a "today" marker. */
@Composable
fun GanttChart(
    rows: List<GanttRow>,
    today: Int,
    modifier: Modifier = Modifier,
    rowHeight: Dp = 34.dp,
) {
    val anim by animateFloatAsState(if (rows.isEmpty()) 0f else 1f, tween(800), label = "gantt")
    val measurer = rememberTextMeasurer()
    val labelW = 120f

    Column(modifier) {
        Canvas(
            Modifier.fillMaxWidth().height(rowHeight * rows.size.coerceAtLeast(1))
        ) {
            if (rows.isEmpty()) return@Canvas
            val minDay = (rows.minOf { it.startDay }).coerceAtMost(today)
            val maxDay = (rows.maxOf { it.endDay }).coerceAtLeast(today)
            val span = (maxDay - minDay).coerceAtLeast(1)
            val plotX = labelW
            val plotW = size.width - labelW
            fun x(day: Int) = plotX + (day - minDay).toFloat() / span * plotW
            val rh = size.height / rows.size

            // vertical quarter gridlines
            repeat(5) { i ->
                val gx = plotX + plotW * (i / 4f)
                drawLine(EgyptColors.NightStroke.copy(alpha = 0.4f), Offset(gx, 0f), Offset(gx, size.height), 1f)
            }
            // today marker
            val tx = x(today)
            drawLine(EgyptColors.GoldBright, Offset(tx, 0f), Offset(tx, size.height), 1.5f, pathEffect = DashEffect)

            rows.forEachIndexed { i, row ->
                val cy = i * rh + rh / 2f
                val barH = rh * 0.46f
                val x0 = x(row.startDay)
                val x1 = x(row.endDay)
                val fullW = (x1 - x0).coerceAtLeast(2f)
                // track
                drawRoundRect(
                    EgyptColors.NightStroke,
                    topLeft = Offset(x0, cy - barH / 2),
                    size = Size(fullW, barH),
                    cornerRadius = CornerRadius(barH / 2, barH / 2),
                )
                // progress fill
                drawRoundRect(
                    row.color,
                    topLeft = Offset(x0, cy - barH / 2),
                    size = Size(fullW * row.progress * anim, barH),
                    cornerRadius = CornerRadius(barH / 2, barH / 2),
                )
                // milestones
                row.milestoneDays.forEach { md ->
                    drawCircle(EgyptColors.GoldBright, barH * 0.28f, Offset(x(md), cy))
                }
                // label
                val tl = measurer.measure(row.label, TextStyle(color = EgyptColors.TextOnNight, fontSize = 10.sp))
                drawText(tl, topLeft = Offset(0f, cy - tl.size.height / 2f))
            }
        }
    }
}
