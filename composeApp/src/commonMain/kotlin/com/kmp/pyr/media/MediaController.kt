package com.kmp.pyr.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Cross-platform entry point for capturing/picking a single image. */
interface MediaController {
    fun pickFromGallery()
    fun captureFromCamera()
}

/**
 * Returns a [MediaController]. Images come back as raw JPEG [ByteArray] so they
 * display via [decodeToImageBitmap] and persist directly (Base64). The platform
 * actual also hosts any UI it needs (e.g. the Android in-app camera dialog).
 *
 * @param onImagePicked raw JPEG bytes of the chosen/captured image
 * @param onCameraDenied invoked when the camera can't be used — the caller shows
 *   a Close-only dialog (no Settings deep-link).
 */
@Composable
expect fun rememberMediaController(
    onImagePicked: (ByteArray) -> Unit,
    onCameraDenied: () -> Unit,
): MediaController

expect fun decodeToImageBitmap(bytes: ByteArray): ImageBitmap?

/** Decode a persisted Base64 seal image to a displayable bitmap. */
@OptIn(ExperimentalEncodingApi::class)
fun decodeSealBitmap(base64: String?): ImageBitmap? =
    base64?.let { runCatching { decodeToImageBitmap(Base64.decode(it)) }.getOrNull() }
