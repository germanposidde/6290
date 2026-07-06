package com.kmp.pyr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.roam.localnav.LocalNavObj.point
import com.kmp.pyr.roam.localnav.ScreenManager
import com.kmp.pyr.ui.components.GoldButton
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.NightBackground
import com.kmp.pyr.ui.theme.EgyptColors
import com.kmp.pyr.ui.theme.EgyptTheme
import org.jetbrains.compose.resources.painterResource
import pyranaroyale6289.composeapp.generated.resources.Res
import pyranaroyale6289.composeapp.generated.resources.bg

@Composable
fun NoInternetScreenA() {
    EgyptTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painterResource(Res.drawable.bg),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xCC0A1428), RoundedCornerShape(24.dp))
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GlyphIcon(EgyptGlyph.EYE_OF_HORUS, size = 96.dp, tint = EgyptColors.GoldBright, strokeWidth = 3f)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "The Nile Has Run Dry",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            shadow = Shadow(Color(0xE6000000), Offset(0f, 2f), 8f),
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
                            shadow = Shadow(Color(0xCC000000), Offset(0f, 1f), 6f),
                        ),
                    )
                }
                Spacer(Modifier.height(28.dp))
                GoldButton(
                    "Retry",
                    onClick = { point(ScreenManager.Welcome) },
                    glyph = EgyptGlyph.SUN_DISC,
                    modifier = Modifier.fillMaxWidth(0.6f),
                )
            }
        }
    }
}
