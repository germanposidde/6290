package com.kmp.pyr

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.inner.config.LoadingSdkConfig
import com.kmp.pyr.inner.ui.BaseLoadingScreen
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.NightBackground
import com.kmp.pyr.ui.theme.EgyptColors
import com.kmp.pyr.ui.theme.EgyptTheme
import org.jetbrains.compose.resources.painterResource
import pyranaroyale6289.composeapp.generated.resources.Res
import pyranaroyale6289.composeapp.generated.resources.bg

expect fun deviceLanguageCode(): String

fun loadingConfig(): LoadingSdkConfig = LoadingSdkConfig.Builder()
    .setBackgroundDrawable(Res.drawable.bg)
    .setProgressColors(
        progress = Color(0xFFFFC107),
        track = Color(0x4DFFFFFF),
        stroke = Color.White,
    )
    .setProgressBarHeight(18.dp)
    .setTextColor(Color.White)
    .setTextStyle(TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
    .build()

@Composable
fun LoadingScreen() {
    BaseLoadingScreen(
        topContent = {
            LoadingScreenContent()
        }
    )
}

@Composable
fun LoadingScreenContent(
) {
    EgyptTheme {
        Box(Modifier.fillMaxSize()) {
            Image(painterResource(Res.drawable.bg), contentScale = ContentScale.FillBounds, contentDescription = null)
            val transition = rememberInfiniteTransition(label = "load")
            val pulse by transition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
                label = "pulse",
            )
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                GlyphIcon(
                    EgyptGlyph.SUN_DISC,
                    modifier = Modifier.scale(pulse),
                    size = 92.dp,
                    tint = EgyptColors.GoldBright,
                    strokeWidth = 3f,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Summoning the royal archive…",
                    color = EgyptColors.TextMuted,
                    fontSize = 13.sp,
                )
            }
        }
    }
}