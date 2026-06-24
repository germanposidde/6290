package com.kmp.pyr.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EgyptColorScheme = darkColorScheme(
    primary = EgyptColors.Gold,
    onPrimary = EgyptColors.NightDeep,
    primaryContainer = EgyptColors.GoldDeep,
    onPrimaryContainer = EgyptColors.Papyrus,

    secondary = EgyptColors.Sandstone,
    onSecondary = EgyptColors.TextOnSand,
    secondaryContainer = EgyptColors.NightRaised,
    onSecondaryContainer = EgyptColors.TextOnNight,

    tertiary = EgyptColors.Turquoise,
    onTertiary = EgyptColors.NightDeep,
    tertiaryContainer = EgyptColors.Lapis,
    onTertiaryContainer = EgyptColors.Papyrus,

    background = EgyptColors.Night,
    onBackground = EgyptColors.TextOnNight,
    surface = EgyptColors.NightCard,
    onSurface = EgyptColors.TextOnNight,
    surfaceVariant = EgyptColors.NightRaised,
    onSurfaceVariant = EgyptColors.TextMuted,
    surfaceContainerHighest = EgyptColors.NightRaised,

    outline = EgyptColors.NightStroke,
    outlineVariant = EgyptColors.GoldSoft,

    error = EgyptColors.Carnelian,
    onError = EgyptColors.Papyrus,
)

@Composable
fun EgyptTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EgyptColorScheme,
        typography = EgyptTypography,
        shapes = EgyptShapes,
        content = content,
    )
}
