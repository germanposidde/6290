package com.kmp.pyr.ui.motif

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.pyr.ui.theme.EgyptColors

/**
 * A horizontal gold divider with a central glyph — used to separate sections
 * the way a cartouche border separates registers on a temple wall.
 */
@Composable
fun OrnamentDivider(
    modifier: Modifier = Modifier,
    glyph: EgyptGlyph = EgyptGlyph.SUN_DISC,
    color: Color = EgyptColors.Gold,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        GoldLine(Modifier.weight(1f), color, toRight = true)
        GlyphIcon(glyph, modifier = Modifier.padding(horizontal = 10.dp), size = 22.dp, tint = color)
        GoldLine(Modifier.weight(1f), color, toRight = false)
    }
}

@Composable
private fun GoldLine(modifier: Modifier, color: Color, toRight: Boolean) {
    Box(
        modifier
            .height(2.dp)
            .drawWithCache {
                val brush = Brush.horizontalGradient(
                    if (toRight) listOf(Color.Transparent, color) else listOf(color, Color.Transparent)
                )
                onDrawBehind { drawRect(brush) }
            }
    )
}

/**
 * Draws a thin gold "engraved" frame just inside the edges of the content.
 * Apply to a Card/Box modifier to give it a temple-relief border.
 */
fun Modifier.goldFrame(
    color: Color = EgyptColors.GoldSoft,
    inset: Float = 10f,
    cornerTick: Float = 14f,
): Modifier = drawWithCache {
    val solid = EgyptColors.Gold.copy(alpha = 0.55f)
    onDrawWithContent {
        drawContent()
        val l = inset; val t = inset; val r = size.width - inset; val b = size.height - inset
        drawRect(color, topLeft = Offset(l, t), size = androidx.compose.ui.geometry.Size(r - l, b - t),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
        // corner accents
        for (cx in listOf(l, r)) for (cy in listOf(t, b)) {
            val sx = if (cx == l) 1f else -1f
            val sy = if (cy == t) 1f else -1f
            drawLine(solid, Offset(cx, cy), Offset(cx + sx * cornerTick, cy), 2f)
            drawLine(solid, Offset(cx, cy), Offset(cx, cy + sy * cornerTick), 2f)
        }
    }
}

/** A small lit/dim achievement medallion bearing a glyph. */
@Composable
fun GlyphMedallion(
    glyph: EgyptGlyph,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(56.dp)
            .drawWithCache {
                val ring = if (unlocked) EgyptColors.GoldBright else EgyptColors.NightStroke
                val disc = if (unlocked)
                    Brush.radialGradient(listOf(EgyptColors.GoldDeep, EgyptColors.NightRaised))
                else Brush.radialGradient(listOf(EgyptColors.NightRaised, EgyptColors.Night))
                onDrawBehind {
                    drawCircle(disc)
                    drawCircle(ring, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        GlyphIcon(
            glyph,
            size = 30.dp,
            tint = if (unlocked) EgyptColors.GoldBright else EgyptColors.TextMuted.copy(alpha = 0.5f),
            strokeWidth = 2.4f,
        )
    }
}
