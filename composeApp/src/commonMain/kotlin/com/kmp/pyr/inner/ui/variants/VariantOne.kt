package com.kmp.pyr.inner.ui.variants

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kmp.pyr.inner.LoadingSdk

@Composable
fun VariantOneContent() {
    val config = remember { LoadingSdk.getConfig() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(
                    width = 1.dp,
                    color = config.progressStrokeColor,
                    shape = RoundedCornerShape(4.dp),
                ),
            color = config.progressColor,
            trackColor = config.progressTrackColor,
        )
    }
}
