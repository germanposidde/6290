package com.kmp.pyr.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kmp.pyr.ui.theme.EgyptColors

/** A tiny filled line used inside stat cards. */
@Composable
fun Sparkline(
    values: List<Double>,
    modifier: Modifier = Modifier,
    color: Color = EgyptColors.GoldBright,
) {
    val anim by animateFloatAsState(if (values.isEmpty()) 0f else 1f, tween(700), label = "spark")
    Canvas(modifier) {
        if (values.size < 2) return@Canvas
        val min = values.min(); val max = values.max()
        val range = (max - min).takeIf { it != 0.0 } ?: 1.0
        val stepX = size.width / (values.size - 1)
        val pts = values.mapIndexed { i, v ->
            Offset(i * stepX, size.height - ((v - min) / range).toFloat() * size.height * 0.9f - size.height * 0.05f)
        }
        val visible = (pts.size * anim).toInt().coerceAtLeast(2)
        val shown = pts.take(visible)
        val line = Path().apply {
            moveTo(shown.first().x, shown.first().y)
            shown.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val fill = Path().apply {
            addPath(line)
            lineTo(shown.last().x, size.height)
            lineTo(shown.first().x, size.height)
            close()
        }
        drawPath(fill, Brush.verticalGradient(listOf(color.copy(alpha = 0.30f), Color.Transparent)))
        drawPath(line, color, style = Stroke(width = 2.4f))
    }
}
