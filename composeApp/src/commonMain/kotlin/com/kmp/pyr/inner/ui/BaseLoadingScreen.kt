package com.kmp.pyr.inner.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.kmp.pyr.inner.LoadingSdk
import com.kmp.pyr.inner.model.LoadingVariant
import com.kmp.pyr.inner.ui.variants.VariantOneContent
import com.kmp.pyr.inner.ui.variants.VariantThreeContent
import com.kmp.pyr.inner.ui.variants.VariantTwoContent
import org.jetbrains.compose.resources.painterResource

@Composable
fun BaseLoadingScreen(
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit,
) {
    val config = remember { LoadingSdk.getConfig() }
    val variant = remember { LoadingSdk.getController().getVariant() }

    Box(modifier = modifier.fillMaxSize()) {
        val bg = config.backgroundDrawable
        if (bg != null) {
            Image(
                painter = painterResource(bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(config.backgroundColor))
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                contentAlignment = Alignment.Center,
            ) {
                topContent()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                when (variant) {
                    LoadingVariant.VARIANT_ONE -> VariantOneContent()
                    LoadingVariant.VARIANT_TWO -> VariantTwoContent()
                    LoadingVariant.VARIANT_THREE -> VariantThreeContent()
                }
            }
        }
    }
}
