package com.kmp.pyr.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.ui.theme.EgyptColors

data class BarEntry(val label: String, val segments: List<Pair<Color, Double>>)

/**
 * Grouped bar chart (1+ bars per category). Bars grow from the baseline on first
 * composition; tapping a group reveals its values.
 */
@Composable
fun BarChart(
    entries: List<BarEntry>,
    modifier: Modifier = Modifier,
    legend: List<Pair<String, Color>> = emptyList(),
) {
    val anim by animateFloatAsState(if (entries.isEmpty()) 0f else 1f, tween(800), label = "bar")
    var selected by remember { mutableStateOf(-1) }
    val measurer = rememberTextMeasurer()

    Column(modifier) {
        Canvas(
            Modifier.fillMaxWidth().weight(1f).pointerInput(entries.size) {
                detectTapGestures { tap ->
                    if (entries.isNotEmpty()) {
                        val idx = ((tap.x / size.width) * entries.size).toInt().coerceIn(0, entries.size - 1)
                        selected = if (selected == idx) -1 else idx
                    }
                }
            }
        ) {
            if (entries.isEmpty()) return@Canvas
            val maxVal = entries.flatMap { it.segments }.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1.0
            val labelH = 18f
            val plotH = size.height - labelH
            val groupW = size.width / entries.size
            val barCount = entries.first().segments.size.coerceAtLeast(1)
            val groupPad = groupW * 0.18f
            val innerW = groupW - groupPad * 2
            val barW = innerW / barCount

            // baseline
            drawLine(EgyptColors.NightStroke, Offset(0f, plotH), Offset(size.width, plotH), 1.5f)

            entries.forEachIndexed { gi, entry ->
                val gx = gi * groupW + groupPad
                entry.segments.forEachIndexed { bi, (color, value) ->
                    val h = (value / maxVal).toFloat() * plotH * 0.92f * anim
                    val x = gx + bi * barW
                    val isSel = selected == gi
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(color, color.copy(alpha = 0.6f))
                        ),
                        topLeft = Offset(x + barW * 0.12f, plotH - h),
                        size = Size(barW * 0.76f, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                        alpha = if (selected == -1 || isSel) 1f else 0.4f,
                    )
                }
                // category label
                val style = TextStyle(color = EgyptColors.TextMuted, fontSize = 9.sp)
                val tl = measurer.measure(entry.label, style)
                drawText(tl, topLeft = Offset(gi * groupW + (groupW - tl.size.width) / 2f, plotH + 3f))

                if (selected == gi) {
                    val topVal = entry.segments.maxOf { it.second }
                    val vstyle = TextStyle(color = EgyptColors.GoldBright, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val vl = measurer.measure(Fmt.compact(topVal), vstyle)
                    drawText(vl, topLeft = Offset(gi * groupW + (groupW - vl.size.width) / 2f, (plotH * 0.02f)))
                }
            }
        }
        if (legend.isNotEmpty()) ChartLegend(legend, Modifier.padding(top = 6.dp))
    }
}
