package com.kmp.pyr.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.theme.EgyptColors

/**
 * Standard top-of-screen heading: gold eyebrow, serif title, a plain-language
 * subtitle that states what the screen is for, and a tappable "?" help button.
 */
@Composable
fun ScreenIntro(
    eyebrow: String,
    title: String,
    subtitle: String,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(eyebrow.uppercase(), color = EgyptColors.Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
            Text(title, style = MaterialTheme.typography.headlineMedium, color = EgyptColors.TextOnNight)
            Text(subtitle, color = EgyptColors.TextMuted, fontSize = 13.sp)
        }
        HelpButton(onHelp)
    }
}

@Composable
fun HelpButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(EgyptColors.NightRaised)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("?", color = EgyptColors.GoldBright, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

/** A subtle inline hint, e.g. "Tap a point to inspect values". */
@Composable
fun Hint(text: String, modifier: Modifier = Modifier, glyph: EgyptGlyph = EgyptGlyph.EYE_OF_HORUS) {
    Row(modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        GlyphIcon(glyph, size = 14.dp, tint = EgyptColors.GoldDeep)
        Text("  $text", color = EgyptColors.TextMuted, fontSize = 11.sp)
    }
}

/** A help sheet listing what the current screen does and how to use it. */
@Composable
fun HelpDialog(title: String, points: List<String>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EgyptColors.NightCard,
        titleContentColor = EgyptColors.TextOnNight,
        textContentColor = EgyptColors.TextOnNight,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlyphIcon(EgyptGlyph.ANKH, size = 22.dp, tint = EgyptColors.Gold)
                Text("  $title")
            }
        },
        text = {
            Column {
                points.forEach { p ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Text("•  ", color = EgyptColors.Gold, fontSize = 14.sp)
                        Text(p, color = EgyptColors.TextOnNight, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            GoldButton("Understood", onClick = onDismiss)
        },
    )
}
