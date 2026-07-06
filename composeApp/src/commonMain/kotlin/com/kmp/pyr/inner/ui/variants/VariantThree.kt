package com.kmp.pyr.inner.ui.variants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.pyr.inner.LoadingSdk

@Composable
fun VariantThreeContent() {
    val config = remember { LoadingSdk.getConfig() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        _root_ide_package_.com.kmp.pyr.inner.ui.components.NonLinearProgressBar(
            modifier = Modifier.fillMaxWidth(),
            progressColor = config.progressColor,
            trackColor = config.progressTrackColor,
            strokeColor = config.progressStrokeColor,
            barHeight = config.progressBarHeight,
            strokeWidth = config.progressStrokeWidth,
            textColor = config.textColor,
            textStyle = config.textStyle,
        )
    }
}
