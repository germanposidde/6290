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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
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
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xE6000000),
                            offset = Offset(0f, 2f),
                            blurRadius = 8f,
                        ),
                    ),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "No connection could be found. Restore your link to the heavens and try again.",
                    color = EgyptColors.TextOnNight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xCC000000),
                            offset = Offset(0f, 1f),
                            blurRadius = 6f,
                        ),
                    ),
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
