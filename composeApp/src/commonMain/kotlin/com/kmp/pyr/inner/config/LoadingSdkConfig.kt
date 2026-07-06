package com.kmp.pyr.inner.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource

data class LoadingSdkConfig(
    /** Background image for the loading screen. If null, [backgroundColor] is used. */
    val backgroundDrawable: DrawableResource?,

    /** Fallback background color when [backgroundDrawable] is null. */
    val backgroundColor: Color,

    /** Color of the filled portion of the progress bar. */
    val progressColor: Color,

    /** Color of the track (unfilled portion) of the progress bar. */
    val progressTrackColor: Color,

    /** Color of the stroke/border around the progress bar (Color.Transparent disables it). */
    val progressStrokeColor: Color,

    /** Height (thickness) of the progress bar. Default: [DEFAULT_PROGRESS_BAR_HEIGHT]. */
    val progressBarHeight: Dp,

    /** Color of text in Variant 2 (scrolling text) and Variant 3 (percentage). */
    val textColor: Color,

    /** Text style for Variant 2 (scrolling text) and Variant 3 (percentage). */
    val textStyle: TextStyle,

    /** Key for the loading flag in the query string. Default: "extra_3". */
    val extra3Key: String,

    /** Key for the variant id in the query string. Default: "extra_4". */
    val extra4Key: String,
) {
    /** Stroke/border width, kept proportional to [progressBarHeight] (see [PROGRESS_STROKE_RATIO]). */
    val progressStrokeWidth: Dp get() = progressBarHeight * PROGRESS_STROKE_RATIO

    /** Corner radius that keeps the bar a pill shape at any [progressBarHeight]. */
    val progressBarCornerRadius: Dp get() = progressBarHeight / 2f

    class Builder {
        private var backgroundDrawable: DrawableResource? = null
        private var backgroundColor: Color = Color.Black
        private var progressColor: Color = Color.White
        private var progressTrackColor: Color = Color(0x4DFFFFFF)
        private var progressStrokeColor: Color = Color.Transparent
        private var progressBarHeight: Dp = DEFAULT_PROGRESS_BAR_HEIGHT
        private var textColor: Color = Color.White
        private var textStyle: TextStyle = DEFAULT_TEXT_STYLE
        private var extra3Key: String = "extra_3"
        private var extra4Key: String = "extra_4"

        /** Sets the background drawable. Takes priority over [setBackgroundColor]. */
        fun setBackgroundDrawable(drawable: DrawableResource) = apply {
            this.backgroundDrawable = drawable
        }

        /** Sets the background color (used only if no drawable is set). */
        fun setBackgroundColor(color: Color) = apply {
            this.backgroundColor = color
        }

        /**
         * Sets the progress bar colors at once.
         *
         * @param progress Color of the filled portion
         * @param track Color of the unfilled portion (background of the bar)
         * @param stroke Color of the stroke/border (use Color.Transparent to disable)
         */
        fun setProgressColors(
            progress: Color,
            track: Color,
            stroke: Color = Color.Transparent,
        ) = apply {
            this.progressColor = progress
            this.progressTrackColor = track
            this.progressStrokeColor = stroke
        }

        /**
         * Sets the progress bar height (thickness). The stroke/border width and corner radius are
         * derived proportionally from it ([progressStrokeWidth], [progressBarCornerRadius]), so the
         * bar stays visually balanced at any height.
         *
         * @param height bar thickness; defaults to [DEFAULT_PROGRESS_BAR_HEIGHT]
         */
        fun setProgressBarHeight(height: Dp = DEFAULT_PROGRESS_BAR_HEIGHT) = apply {
            this.progressBarHeight = height
        }

        /** Sets the text color for Variant 2 (scrolling text) and Variant 3 (percentage). */
        fun setTextColor(color: Color) = apply {
            this.textColor = color
        }

        /**
         * Sets the text style (font size, weight, family) for Variant 2 and Variant 3.
         * If not called, [DEFAULT_TEXT_STYLE] is used (14sp, Medium weight).
         */
        fun setTextStyle(style: TextStyle) = apply {
            this.textStyle = style
        }

        /**
         * Sets the query string keys.
         *
         * Resulting format: "{extra3Key}=loading&{extra4Key}={variantId}"
         *
         * @param extra3Key Key for the loading flag (default: "extra_3")
         * @param extra4Key Key for the variant id (default: "extra_4")
         */
        fun setExtraKeys(extra3Key: String, extra4Key: String) = apply {
            this.extra3Key = extra3Key
            this.extra4Key = extra4Key
        }

        fun build(): LoadingSdkConfig = LoadingSdkConfig(
            backgroundDrawable = backgroundDrawable,
            backgroundColor = backgroundColor,
            progressColor = progressColor,
            progressTrackColor = progressTrackColor,
            progressStrokeColor = progressStrokeColor,
            progressBarHeight = progressBarHeight,
            textColor = textColor,
            textStyle = textStyle,
            extra3Key = extra3Key,
            extra4Key = extra4Key,
        )
    }

    companion object {
        /** Default progress bar height (thickness). */
        val DEFAULT_PROGRESS_BAR_HEIGHT: Dp = 12.dp

        /** Stroke width as a fraction of the bar height (12.dp height → ~1.dp stroke). */
        const val PROGRESS_STROKE_RATIO: Float = 1f / 12f

        /** Default text style — 14sp, Medium weight. */
        val DEFAULT_TEXT_STYLE: TextStyle = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )

        /** Returns a default configuration with sensible defaults. */
        fun default(): LoadingSdkConfig = Builder().build()
    }
}
