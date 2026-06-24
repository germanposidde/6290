package com.kmp.pyr.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.kmp.pyr.ui.motif.EgyptGlyph
import com.kmp.pyr.ui.motif.GlyphIcon
import com.kmp.pyr.ui.theme.EgyptColors
import kotlinx.coroutines.launch

/**
 * Primary gold button with built-in double-tap / spam protection: while an
 * invocation is in flight the button ignores further taps and disables itself.
 */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glyph: EgyptGlyph? = null,
) {
    var processing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scale by animateFloatAsState(if (processing) 0.97f else 1f, label = "press")

    Button(
        onClick = {
            if (processing) return@Button
            processing = true
            scope.launch { try { onClick() } finally { processing = false } }
        },
        modifier = modifier.scale(scale),
        enabled = enabled && !processing,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = EgyptColors.Gold,
            contentColor = EgyptColors.NightDeep,
            disabledContainerColor = EgyptColors.GoldDeep.copy(alpha = 0.4f),
            disabledContentColor = EgyptColors.TextMuted,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (glyph != null) {
                GlyphIcon(glyph, size = 18.dp, tint = EgyptColors.NightDeep, strokeWidth = 2.2f)
                androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
            }
            Text(text)
        }
    }
}

/** Destructive outlined button (carnelian) for irreversible actions; same spam protection. */
@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var processing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            if (processing) return@OutlinedButton
            processing = true
            scope.launch { try { onClick() } finally { processing = false } }
        },
        modifier = modifier,
        enabled = enabled && !processing,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, EgyptColors.Carnelian),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EgyptColors.CarnelianBright),
    ) {
        GlyphIcon(EgyptGlyph.SCARAB, size = 18.dp, tint = EgyptColors.CarnelianBright, strokeWidth = 2.2f)
        androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
        Text(text)
    }
}

/** Secondary outlined gold button, same spam protection. */
@Composable
fun GoldOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glyph: EgyptGlyph? = null,
) {
    var processing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            if (processing) return@OutlinedButton
            processing = true
            scope.launch { try { onClick() } finally { processing = false } }
        },
        modifier = modifier,
        enabled = enabled && !processing,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, EgyptColors.Gold),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EgyptColors.GoldBright),
    ) {
        if (glyph != null) {
            GlyphIcon(glyph, size = 18.dp, tint = EgyptColors.GoldBright, strokeWidth = 2.2f)
            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
        }
        Text(text)
    }
}
