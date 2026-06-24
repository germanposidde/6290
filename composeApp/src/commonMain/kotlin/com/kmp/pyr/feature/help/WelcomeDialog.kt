package com.kmp.pyr.feature.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.navigation.Destination
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.theme.EgyptColors

/** First-run tour that introduces the five sections in plain language. */
@Composable
fun WelcomeDialog(kingdomName: String, onEnter: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* must tap Enter — this is the intro */ },
        containerColor = EgyptColors.NightCard,
        titleContentColor = EgyptColors.TextOnNight,
        textContentColor = EgyptColors.TextOnNight,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                GlyphIcon(com.kmp.pyr.ui.motif.EgyptGlyph.SUN_DISC, size = 44.dp, tint = EgyptColors.GoldBright, strokeWidth = 3f)
                Spacer(Modifier.height(8.dp))
                Text("Welcome to Kingdom", fontWeight = FontWeight.Bold)
                Text(kingdomName, color = EgyptColors.TextMuted, fontSize = 12.sp)
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "A productivity suite for managing a virtual kingdom's resources, projects and analytics. Five sections, reachable from the bar below:",
                    color = EgyptColors.TextOnNight, fontSize = 13.sp,
                )
                Spacer(Modifier.height(12.dp))
                Destination.entries.forEach { dest ->
                    val guide = SCREEN_GUIDES[dest]
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        GlyphIcon(dest.glyph, size = 26.dp, tint = EgyptColors.GoldBright)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(dest.label, color = EgyptColors.GoldBright, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(guide?.subtitle ?: "", color = EgyptColors.TextMuted, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tip: every screen has a “?” button for detailed help. Everything works fully offline and your data is saved automatically.",
                    color = EgyptColors.TextMuted, fontSize = 12.sp,
                )
            }
        },
        confirmButton = {
            GoldButton("Enter the Kingdom", onClick = onEnter, glyph = com.kmp.pyr.ui.motif.EgyptGlyph.ANKH)
        },
    )
}
