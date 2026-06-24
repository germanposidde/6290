package com.kmp.pyr.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.theme.EgyptColors
import com.kmp.pyr.ui.theme.EngravedEyebrow

/** Gold engraved eyebrow + serif title, optionally with a leading glyph. */
@Composable
fun SectionHeader(
    title: String,
    eyebrow: String? = null,
    glyph: EgyptGlyph? = null,
    caption: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (glyph != null) {
            GlyphIcon(glyph, size = 26.dp, tint = EgyptColors.Gold)
        }
        Column(Modifier.padding(start = if (glyph != null) 10.dp else 0.dp)) {
            if (eyebrow != null) {
                Text(eyebrow.uppercase(), style = EngravedEyebrow, color = EgyptColors.Gold)
            }
            Text(title, style = MaterialTheme.typography.titleLarge, color = EgyptColors.TextOnNight)
            if (caption != null) {
                Text(
                    caption,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = EgyptColors.TextMuted,
                )
            }
        }
    }
}
