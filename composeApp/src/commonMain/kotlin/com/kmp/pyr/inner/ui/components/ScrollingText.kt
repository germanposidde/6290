package com.kmp.pyr.inner.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ScrollingText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    textStyle: TextStyle = TextStyle.Default,
    durationMillis: Int = 8000,
    onScrollComplete: () -> Unit = {},
) {
    val xOffset = remember(text) { Animatable(1f) }

    LaunchedEffect(text) {
        xOffset.animateTo(
            targetValue = -2f,
            animationSpec = tween(durationMillis, easing = LinearEasing),
        )
        onScrollComplete()
    }

    Box(modifier = modifier.height(24.dp)) {
        Text(
            text = text,
            color = textColor,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
            modifier = Modifier.offset(x = (xOffset.value * 400).dp),
        )
    }
}
