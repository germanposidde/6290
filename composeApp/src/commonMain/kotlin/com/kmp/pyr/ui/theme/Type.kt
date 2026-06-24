package com.kmp.pyr.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Display headers use a serif family for a carved-stone feel; body uses the
 * default sans. Letter-spacing on titles evokes engraved temple inscriptions.
 */
val EgyptTypography: Typography = Typography().run {
    val serif = FontFamily.Serif
    copy(
        displaySmall = displaySmall.copy(
            fontFamily = serif, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = serif, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp,
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = serif, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp,
        ),
        titleLarge = titleLarge.copy(
            fontFamily = serif, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp,
        ),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp),
        labelMedium = labelMedium.copy(letterSpacing = 1.2.sp),
    )
}

/** Extra style for the gold engraved section eyebrows. */
val EngravedEyebrow = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    letterSpacing = 3.sp,
)
