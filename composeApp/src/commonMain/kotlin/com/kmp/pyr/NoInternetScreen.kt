package com.kmp.pyr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.NightBackground
import com.kmp.pyr.ui.theme.EgyptColors
import com.kmp.pyr.ui.theme.EgyptTheme

@Composable
fun NoInternetScreen(onRetry: () -> Unit) {
    EgyptTheme {
        NightBackground {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                GlyphIcon(EgyptGlyph.EYE_OF_HORUS, size = 96.dp, tint = EgyptColors.Gold, strokeWidth = 3f)
                Spacer(Modifier.height(28.dp))
                Text(
                    "The Nile Has Run Dry",
                    color = EgyptColors.TextOnNight,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "No connection could be found. Restore your link to the heavens and try again.",
                    color = EgyptColors.TextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(28.dp))
                GoldButton(
                    "Retry",
                    onClick = onRetry,
                    glyph = EgyptGlyph.SUN_DISC,
                    modifier = Modifier.fillMaxWidth(0.6f),
                )
            }
        }
    }
}
