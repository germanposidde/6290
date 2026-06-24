package com.kmp.pyr.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.ui.theme.EgyptColors

data class LineSeries(val label: String, val color: Color, val values: List<Double>)

/**
 * Multi-series line chart with animated draw-in and tap-to-highlight. Tapping
 * selects the nearest x position and shows each series' value at that index.
 */
@Composable
fun LineChart(
    series: List<LineSeries>,
    modifier: Modifier = Modifier,
    xLabels: List<String> = emptyList(),
) {
    val anim by animateFloatAsState(if (series.isEmpty()) 0f else 1f, tween(900), label = "line")
    var selected by remember { mutableStateOf(-1) }
    val count = series.maxOfOrNull { it.values.size } ?: 0

    Column(modifier) {
        Canvas(
            Modifier.fillMaxWidth().weight(1f).pointerInput(count) {
                detectTapGestures { tap ->
                    if (count > 1) {
                        val idx = ((tap.x / size.width) * (count - 1)).toInt().coerceIn(0, count - 1)
                        selected = if (selected == idx) -1 else idx
                    }
                }
            }
        ) {
            if (count < 2) return@Canvas
            val gridColor = EgyptColors.NightStroke.copy(alpha = 0.5f)
            val all = series.flatMap { it.values }
            val min = all.min(); val max = all.max()
            val range = (max - min).takeIf { it != 0.0 } ?: 1.0
            val plotH = size.height * 0.9f
            val top = size.height * 0.05f
            val stepX = size.width / (count - 1)

            // horizontal grid lines
            repeat(4) { i ->
                val y = top + plotH * (i / 3f)
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
            }

            series.forEach { s ->
                if (s.values.size < 2) return@forEach
                val pts = s.values.mapIndexed { i, v ->
                    Offset(i * stepX, top + plotH - ((v - min) / range).toFloat() * plotH)
                }
                val visible = (pts.size * anim).toInt().coerceAtLeast(2)
                val shown = pts.take(visible)
                val line = Path().apply {
                    moveTo(shown.first().x, shown.first().y)
                    shown.drop(1).forEach { lineTo(it.x, it.y) }
                }
                val fill = Path().apply {
                    addPath(line)
                    lineTo(shown.last().x, size.height); lineTo(shown.first().x, size.height); close()
                }
                drawPath(fill, Brush.verticalGradient(listOf(s.color.copy(alpha = 0.18f), Color.Transparent)))
                drawPath(line, s.color, style = Stroke(width = 2.6f))
            }

            if (selected in 0 until count) {
                val x = selected * stepX
                drawLine(EgyptColors.GoldBright, Offset(x, top), Offset(x, top + plotH), 1.5f)
                series.forEach { s ->
                    val v = s.values.getOrNull(selected) ?: return@forEach
                    val y = top + plotH - ((v - min) / range).toFloat() * plotH
                    drawCircle(EgyptColors.Night, 6f, Offset(x, y))
                    drawCircle(s.color, 6f, Offset(x, y), style = Stroke(2.5f))
                }
            }
        }

        // Selected readout or legend
        if (selected in 0 until count) {
            Column(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                Text(
                    xLabels.getOrNull(selected) ?: "Point ${selected + 1}",
                    color = EgyptColors.GoldBright, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                )
                series.forEach { s ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Dot(s.color)
                        Text(
                            "  ${s.label}: ${Fmt.compact(s.values.getOrNull(selected) ?: 0.0)}",
                            color = EgyptColors.TextOnNight, fontSize = 11.sp,
                        )
                    }
                }
            }
        } else {
            ChartLegend(series.map { it.label to it.color }, Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
internal fun Dot(color: Color) {
    androidx.compose.foundation.layout.Box(Modifier.size(8.dp).clip(CircleShape).background(color))
}

@Composable
internal fun ChartLegend(items: List<Pair<String, Color>>, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items.forEach { (label, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Dot(color)
                Text("  $label", color = EgyptColors.TextMuted, fontSize = 11.sp)
            }
        }
    }
}
