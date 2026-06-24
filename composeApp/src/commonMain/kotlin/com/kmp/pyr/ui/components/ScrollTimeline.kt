package com.kmp.pyr.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.data.ActivityEntry
import com.kmp.pyr.data.ActivityKind
import com.kmp.pyr.data.Fmt
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.papyrusSurface
import com.kmp.pyr.ui.theme.EgyptColors

/**
 * Recent-activity feed rendered as an unrolled papyrus scroll: warm sandstone
 * surface with rolled gold edges top and bottom, dated entries down a spine.
 */
@Composable
fun ScrollTimeline(
    entries: List<ActivityEntry>,
    today: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        ScrollRoller()
        Column(
            Modifier
                .fillMaxWidth()
                .papyrusSurface()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            entries.forEachIndexed { i, e ->
                TimelineRow(e, today, isLast = i == entries.lastIndex)
            }
        }
        ScrollRoller()
    }
}

@Composable
private fun ScrollRoller() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(14.dp)
            .drawBehind {
                drawRoundRect(
                    Brush.verticalGradient(listOf(EgyptColors.GoldBright, EgyptColors.GoldDeep)),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f, 7f),
                )
                drawLine(EgyptColors.GoldDeep, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1f)
            }
    )
}

@Composable
private fun TimelineRow(e: ActivityEntry, today: Int, isLast: Boolean) {
    Row(Modifier.fillMaxWidth()) {
        // spine + node
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(26.dp).clip(RoundedCornerShape(13.dp)).background(EgyptColors.Night),
                contentAlignment = Alignment.Center,
            ) { GlyphIcon(glyphFor(e.kind), size = 16.dp, tint = EgyptColors.GoldBright) }
            if (!isLast) Box(Modifier.width(2.dp).height(34.dp).background(EgyptColors.GoldDeep.copy(alpha = 0.5f)))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(e.title, color = EgyptColors.TextOnSand, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                if (e.delta != 0.0) {
                    val up = e.delta >= 0
                    Text(
                        Fmt.signed(e.delta),
                        color = if (up) EgyptColors.GoldDeep else EgyptColors.Carnelian,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp,
                    )
                }
            }
            Text(e.detail, color = EgyptColors.TextOnSand.copy(alpha = 0.75f), fontSize = 12.sp)
            Text(Fmt.relativeDay(e.day, today), color = EgyptColors.GoldDeep, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

fun glyphFor(kind: ActivityKind): EgyptGlyph = when (kind) {
    ActivityKind.RECORD -> EgyptGlyph.SUN_DISC
    ActivityKind.CALCULATION -> EgyptGlyph.EYE_OF_HORUS
    ActivityKind.MILESTONE -> EgyptGlyph.PYRAMID
    ActivityKind.PROJECT -> EgyptGlyph.OBELISK
    ActivityKind.BACKUP -> EgyptGlyph.SCARAB
}
