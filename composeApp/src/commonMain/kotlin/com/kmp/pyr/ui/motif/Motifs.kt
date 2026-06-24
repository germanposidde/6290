package com.kmp.pyr.ui.motif

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kmp.pyr.ui.theme.EgyptColors
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** The set of hand-drawn Egyptian symbols available across the app. */
enum class EgyptGlyph { ANKH, EYE_OF_HORUS, SCARAB, PYRAMID, OBELISK, SUN_DISC, FEATHER, LOTUS }

/**
 * A crisp, fully procedural Egyptian glyph. No bitmap assets — scales to any size.
 */
@Composable
fun GlyphIcon(
    glyph: EgyptGlyph,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = EgyptColors.Gold,
    strokeWidth: Float = 2f,
) {
    Canvas(modifier = modifier.size(size)) {
        drawGlyph(glyph, tint, strokeWidth)
    }
}

fun DrawScope.drawGlyph(glyph: EgyptGlyph, tint: Color, strokeWidth: Float = 2f) {
    val w = this.size.width
    val h = this.size.height
    val s = min(w, h)
    val stroke = Stroke(width = strokeWidth)
    when (glyph) {
        EgyptGlyph.ANKH -> {
            val cx = w / 2f
            val loopR = s * 0.18f
            val loopCy = h * 0.27f
            drawCircle(tint, loopR, Offset(cx, loopCy), style = Stroke(strokeWidth * 1.4f))
            // vertical bar
            drawLine(tint, Offset(cx, loopCy + loopR), Offset(cx, h * 0.92f), strokeWidth * 1.4f)
            // crossbar
            drawLine(tint, Offset(w * 0.22f, h * 0.56f), Offset(w * 0.78f, h * 0.56f), strokeWidth * 1.4f)
        }
        EgyptGlyph.EYE_OF_HORUS -> {
            val cx = w / 2f; val cy = h * 0.42f
            val path = Path().apply {
                moveTo(w * 0.12f, cy)
                quadraticTo(cx, h * 0.18f, w * 0.82f, cy)
                quadraticTo(cx, h * 0.66f, w * 0.12f, cy)
                close()
            }
            drawPath(path, tint, style = stroke)
            drawCircle(tint, s * 0.10f, Offset(cx, cy))
            // teardrop tail
            drawLine(tint, Offset(w * 0.42f, h * 0.6f), Offset(w * 0.34f, h * 0.86f), strokeWidth)
            // brow curl
            val brow = Path().apply {
                moveTo(w * 0.62f, h * 0.6f)
                quadraticTo(w * 0.86f, h * 0.74f, w * 0.7f, h * 0.92f)
            }
            drawPath(brow, tint, style = stroke)
        }
        EgyptGlyph.SCARAB -> {
            val cx = w / 2f
            // body
            drawOval(tint, topLeft = Offset(w * 0.3f, h * 0.32f), size = Size(w * 0.4f, h * 0.5f), style = stroke)
            // head
            drawCircle(tint, s * 0.08f, Offset(cx, h * 0.28f), style = stroke)
            // central spine
            drawLine(tint, Offset(cx, h * 0.36f), Offset(cx, h * 0.78f), strokeWidth)
            // legs
            for (sgn in listOf(-1f, 1f)) {
                drawLine(tint, Offset(cx + sgn * w * 0.18f, h * 0.4f), Offset(cx + sgn * w * 0.4f, h * 0.3f), strokeWidth)
                drawLine(tint, Offset(cx + sgn * w * 0.2f, h * 0.55f), Offset(cx + sgn * w * 0.44f, h * 0.55f), strokeWidth)
                drawLine(tint, Offset(cx + sgn * w * 0.18f, h * 0.7f), Offset(cx + sgn * w * 0.4f, h * 0.82f), strokeWidth)
            }
        }
        EgyptGlyph.PYRAMID -> {
            val base = Path().apply {
                moveTo(w * 0.5f, h * 0.16f)
                lineTo(w * 0.9f, h * 0.84f)
                lineTo(w * 0.1f, h * 0.84f)
                close()
            }
            drawPath(base, tint, style = stroke)
            drawLine(tint, Offset(w * 0.5f, h * 0.16f), Offset(w * 0.5f, h * 0.84f), strokeWidth * 0.7f)
        }
        EgyptGlyph.OBELISK -> {
            val cx = w / 2f
            val body = Path().apply {
                moveTo(cx - w * 0.1f, h * 0.85f)
                lineTo(cx - w * 0.07f, h * 0.25f)
                lineTo(cx, h * 0.12f)
                lineTo(cx + w * 0.07f, h * 0.25f)
                lineTo(cx + w * 0.1f, h * 0.85f)
                close()
            }
            drawPath(body, tint, style = stroke)
        }
        EgyptGlyph.SUN_DISC -> {
            val cx = w / 2f; val cy = h / 2f; val r = s * 0.22f
            drawCircle(tint, r, Offset(cx, cy), style = Stroke(strokeWidth * 1.3f))
            repeat(12) { i ->
                val a = (i / 12f) * 6.2832f
                val r1 = r * 1.35f; val r2 = r * 1.7f
                drawLine(tint, Offset(cx + cos(a) * r1, cy + sin(a) * r1), Offset(cx + cos(a) * r2, cy + sin(a) * r2), strokeWidth)
            }
        }
        EgyptGlyph.FEATHER -> {
            val cx = w / 2f
            drawLine(tint, Offset(cx, h * 0.12f), Offset(cx, h * 0.9f), strokeWidth * 1.2f)
            var y = h * 0.2f
            while (y < h * 0.88f) {
                val len = (1f - (y - h * 0.2f) / (h * 0.7f)) * w * 0.22f + w * 0.06f
                drawLine(tint, Offset(cx, y), Offset(cx - len, y + h * 0.04f), strokeWidth)
                drawLine(tint, Offset(cx, y), Offset(cx + len, y + h * 0.04f), strokeWidth)
                y += h * 0.08f
            }
        }
        EgyptGlyph.LOTUS -> {
            val cx = w / 2f; val baseY = h * 0.82f
            for (sgn in listOf(0f, -1f, 1f)) {
                val petal = Path().apply {
                    moveTo(cx, baseY)
                    quadraticTo(cx + sgn * w * 0.34f, h * 0.3f, cx + sgn * w * 0.18f, h * 0.16f)
                    quadraticTo(cx + sgn * w * 0.02f, h * 0.4f, cx, baseY)
                }
                drawPath(petal, tint, style = stroke)
            }
            drawLine(tint, Offset(cx, baseY), Offset(cx, h * 0.94f), strokeWidth)
        }
    }
}

/**
 * A pyramid that fills from the base toward the capstone as [progress] (0..1) grows —
 * the core visual for the Pyramid Planner.
 */
@Composable
fun ProgressPyramid(
    progress: Float,
    modifier: Modifier = Modifier,
    fill: Color = EgyptColors.GoldBright,
    track: Color = EgyptColors.NightStroke,
    capstoneLit: Color = EgyptColors.GoldBright,
) {
    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val apex = Offset(w / 2f, h * 0.06f)
        val bl = Offset(w * 0.06f, h * 0.94f)
        val br = Offset(w * 0.94f, h * 0.94f)
        val outline = Path().apply {
            moveTo(apex.x, apex.y); lineTo(br.x, br.y); lineTo(bl.x, bl.y); close()
        }
        drawPath(outline, track, style = Stroke(width = 3f))

        val p = progress.coerceIn(0f, 1f)
        // Fill rises from base: the filled region is everything below cut height.
        val cutY = br.y - (br.y - apex.y) * p
        val tFromTop = (cutY - apex.y) / (br.y - apex.y)
        val leftX = apex.x + (bl.x - apex.x) * tFromTop
        val rightX = apex.x + (br.x - apex.x) * tFromTop
        val filled = Path().apply {
            moveTo(leftX, cutY); lineTo(rightX, cutY); lineTo(br.x, br.y); lineTo(bl.x, bl.y); close()
        }
        drawPath(filled, fill)

        // Course lines (stone rows)
        val rows = 6
        for (i in 1 until rows) {
            val t = i / rows.toFloat()
            val y = apex.y + (br.y - apex.y) * t
            val lx = apex.x + (bl.x - apex.x) * t
            val rx = apex.x + (br.x - apex.x) * t
            drawLine(track.copy(alpha = 0.5f), Offset(lx, y), Offset(rx, y), 1.5f)
        }
        // Capstone glows once near-complete
        if (p > 0.85f) {
            val capH = (br.y - apex.y) * 0.14f
            val cy = apex.y + capH
            val clx = apex.x + (bl.x - apex.x) * (capH / (br.y - apex.y))
            val crx = apex.x + (br.x - apex.x) * (capH / (br.y - apex.y))
            val cap = Path().apply { moveTo(apex.x, apex.y); lineTo(crx, cy); lineTo(clx, cy); close() }
            drawPath(cap, capstoneLit)
        }
    }
}

internal val DashEffect: PathEffect get() = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
internal val Rect.center2: Offset get() = Offset((left + right) / 2f, (top + bottom) / 2f)
