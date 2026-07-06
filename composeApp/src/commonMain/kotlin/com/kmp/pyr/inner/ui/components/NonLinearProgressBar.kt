package com.kmp.pyr.inner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val MAX_PERCENT = 98

private const val STEP_BASE_MS = 50L
private const val STEP_K_NUM = 36L
private const val STEP_K_DEN = 100L

@Composable
fun NonLinearProgressBar(
    modifier: Modifier = Modifier,
    progressColor: Color = Color.White,
    trackColor: Color = Color.White.copy(alpha = 0.3f),
    strokeColor: Color = Color.Transparent,
    barHeight: Dp = 12.dp,
    strokeWidth: Dp = 1.dp,
    textColor: Color = Color.White,
    textStyle: TextStyle = TextStyle.Default,
    onProgressChange: (Float) -> Unit = {},
) {
    val cornerRadius = barHeight / 2f
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        for (percent in 1..MAX_PERCENT) {
            progress = percent / 100f
            onProgressChange(progress)
            val stepDelay = STEP_BASE_MS + (percent.toLong() * percent.toLong() * STEP_K_NUM) / STEP_K_DEN
            delay(stepDelay)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "${(progress * 100).toInt()}%",
            color = textColor,
            style = textStyle,
        )

        // Custom track + fill (instead of Material3 LinearProgressIndicator, whose drawn thickness
        // is fixed and ignores the height modifier) so barHeight is honored exactly.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .background(trackColor)
                .border(
                    width = strokeWidth,
                    color = strokeColor,
                    shape = RoundedCornerShape(cornerRadius),
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(progressColor),
            )
        }
    }
}
