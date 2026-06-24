package com.kmp.pyr.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.ui.components.charts.Sparkline
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.theme.EgyptColors

/** Overview card: glyph + label, big value, delta pill, and a mini sparkline. */
@Composable
fun StatCard(
    label: String,
    value: String,
    delta: Double,
    glyph: EgyptGlyph,
    trend: List<Double>,
    modifier: Modifier = Modifier,
    accent: Color = EgyptColors.GoldBright,
) {
    EgyptCard(modifier = modifier, contentPadding = 14) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlyphIcon(glyph, size = 22.dp, tint = accent)
            Spacer(Modifier.size(8.dp))
            Text(label, color = EgyptColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.headlineSmall, color = EgyptColors.TextOnNight)
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            DeltaPill(delta)
            Box(Modifier.height(28.dp).size(width = 64.dp, height = 28.dp)) {
                Sparkline(trend, Modifier.fillMaxWidth().height(28.dp), color = accent)
            }
        }
    }
}

@Composable
fun DeltaPill(delta: Double) {
    val up = delta >= 0
    val color = if (up) EgyptColors.Profit else EgyptColors.Loss
    Text(
        text = (if (up) "▲ " else "▼ ") + com.kmp.pyr.data.Fmt.compact(kotlin.math.abs(delta)),
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}
