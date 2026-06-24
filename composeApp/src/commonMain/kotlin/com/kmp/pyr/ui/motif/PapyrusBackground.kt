package com.kmp.pyr.ui.motif

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kmp.pyr.ui.theme.EgyptColors
import kotlin.math.sin

/**
 * The app-wide night-sky backdrop: a deep blue vertical gradient with a faint
 * gold sun-glow near the top and a scattering of subtle stars.
 */
@Composable
fun NightBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    Brush.verticalGradient(
                        0f to EgyptColors.NightDeep,
                        0.5f to EgyptColors.Night,
                        1f to EgyptColors.NightDeep,
                    )
                )
                // sun glow upper-right
                drawCircle(
                    Brush.radialGradient(
                        listOf(EgyptColors.GoldSoft, Color.Transparent),
                        center = Offset(size.width * 0.82f, size.height * 0.08f),
                        radius = size.width * 0.55f,
                    ),
                    radius = size.width * 0.55f,
                    center = Offset(size.width * 0.82f, size.height * 0.08f),
                )
                // deterministic "stars"
                var seed = 12345
                repeat(40) {
                    seed = (seed * 1103515245 + 12345) and 0x7fffffff
                    val x = (seed % 1000) / 1000f * size.width
                    seed = (seed * 1103515245 + 12345) and 0x7fffffff
                    val y = (seed % 1000) / 1000f * size.height * 0.7f
                    val r = (sin(x + y) * 0.5f + 0.5f) * 1.3f + 0.4f
                    drawCircle(EgyptColors.SandstoneLight.copy(alpha = 0.10f), r, Offset(x, y))
                }
            }
    ) { content() }
}

/**
 * A sandstone "papyrus" surface fill for scroll-style panels — warm gradient with
 * faint horizontal fibres.
 */
fun Modifier.papyrusSurface(): Modifier = drawBehind {
    drawRect(
        Brush.verticalGradient(listOf(EgyptColors.Papyrus, EgyptColors.PapyrusShade))
    )
    var y = 6f
    while (y < size.height) {
        drawLine(
            EgyptColors.PapyrusShade.copy(alpha = 0.35f),
            Offset(0f, y), Offset(size.width, y), 1f,
        )
        y += 9f
    }
}
