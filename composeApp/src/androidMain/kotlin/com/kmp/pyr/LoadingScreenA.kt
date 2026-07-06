package com.kmp.pyr

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.pyr.inner.LoadingSdk
import com.kmp.pyr.roam.StartCache
import com.kmp.pyr.roam.localnav.InternetState
import com.kmp.pyr.roam.localnav.LocalNavObj.point
import com.kmp.pyr.roam.localnav.ScreenManager
import com.kmp.pyr.roam.localnav.getInternetState
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.motif.NightBackground
import com.kmp.pyr.ui.theme.EgyptColors
import com.kmp.pyr.ui.theme.EgyptTheme
import kotlinx.coroutines.delay

@Composable
fun LoadingScreenA(
    activity: ComponentActivity,
    startCache: StartCache,
) {
    remember { LoadingSdk.startMinDuration() }
    LaunchedEffect(Unit) {
        when (getInternetState(activity)) {
            InternetState.NoConnection -> {
                delay(1500)
                point(ScreenManager.InternetProblem)
            }

            InternetState.Connected -> {
                val result = runCatching {
                    startCache.ifConnected(activity, startCache)
                }

                if (result.isFailure) {
                    point(ScreenManager.MenuPoint)
                }
            }
        }
    }

   LoadingScreen()
}
