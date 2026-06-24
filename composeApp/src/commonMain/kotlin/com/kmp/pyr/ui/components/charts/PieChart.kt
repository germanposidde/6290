package com.kmp.pyr.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.ui.theme.EgyptColors
import kotlin.math.roundToInt

data class PieSlice(val label: String, val value: Double, val color: Color)

/** Donut chart with an animated sweep, centre total, and a legend column. */
@Composable
fun PieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    centerLabel: String = "Total",
    ringWidth: Float = 34f,
) {
    val anim by animateFloatAsState(if (slices.isEmpty()) 0f else 1f, tween(900), label = "pie")
    val total = slices.sumOf { it.value }.takeIf { it > 0 } ?: 1.0

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(150.dp)) {
                val pad = ringWidth / 2f + 4f
                val arcSize = Size(size.width - pad * 2, size.height - pad * 2)
                val topLeft = Offset(pad, pad)
                drawArc(EgyptColors.NightStroke.copy(alpha = 0.4f), 0f, 360f, false, topLeft, arcSize, style = Stroke(ringWidth))
                var start = -90f
                slices.forEach { s ->
                    val sweep = (s.value / total).toFloat() * 360f * anim
                    drawArc(s.color, start, sweep, false, topLeft, arcSize, style = Stroke(ringWidth))
                    start += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(Fmt.compact(total), color = EgyptColors.GoldBright, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(centerLabel, color = EgyptColors.TextMuted, fontSize = 10.sp)
            }
        }
        Column(
            Modifier.width(8.dp)
        ) {}
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            slices.forEach { s ->
                val pct = ((s.value / total) * 100).roundToInt()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Dot(s.color)
                    Text(
                        "  ${s.label}",
                        color = EgyptColors.TextOnNight, fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text("$pct%", color = EgyptColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
