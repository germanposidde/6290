package com.kmp.pyr.inner.ui.variants

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kmp.pyr.inner.LoadingSdk
import com.kmp.pyr.inner.localization.currentAppLanguage
import kotlinx.coroutines.delay

private const val GAP_BETWEEN_LINES_MS = 100L
private const val LINE_SCROLL_MS = 8000

@Composable
fun VariantTwoContent() {
    val config = remember { LoadingSdk.getConfig() }

    val phrases = remember { variantTwoPhrases(currentAppLanguage()) }

    var phrase by remember { mutableStateOf(phrases.random()) }
    var completedCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(completedCount) {
        if (completedCount == 0) return@LaunchedEffect
        delay(GAP_BETWEEN_LINES_MS)
        phrase = (phrases - phrase).random()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .border(
                    width = 1.dp,
                    color = config.progressStrokeColor,
                    shape = RoundedCornerShape(3.dp),
                ),
            color = config.progressColor,
            trackColor = config.progressTrackColor,
        )

        _root_ide_package_.com.kmp.pyr.inner.ui.components.ScrollingText(
            text = phrase,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textColor = config.textColor,
            textStyle = config.textStyle,
            durationMillis = LINE_SCROLL_MS,
            onScrollComplete = { completedCount++ },
        )
    }
}
