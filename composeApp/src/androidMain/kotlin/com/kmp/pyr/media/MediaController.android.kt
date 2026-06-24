package com.kmp.pyr.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.pyr.ui.theme.EgyptColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual fun decodeToImageBitmap(bytes: ByteArray): ImageBitmap? =
    runCatching { BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap() }.getOrNull()

@Composable
actual fun rememberMediaController(
    onImagePicked: (ByteArray) -> Unit,
    onCameraDenied: () -> Unit,
): MediaController {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCamera by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) scope.launch(Dispatchers.IO) {
            val bytes = runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()
            if (bytes != null) withContext(Dispatchers.Main) { onImagePicked(bytes) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showCamera = true else onCameraDenied()
    }

    val controller = remember {
        object : MediaController {
            override fun pickFromGallery() {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            override fun captureFromCamera() {
                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                if (granted) showCamera = true else permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    if (showCamera) {
        CameraDialog(
            onCaptured = { bytes -> showCamera = false; onImagePicked(bytes) },
            onDismiss = { showCamera = false },
            onError = { showCamera = false; onCameraDenied() },
        )
    }

    return controller
}

@Composable
private fun CameraDialog(
    onCaptured: (ByteArray) -> Unit,
    onDismiss: () -> Unit,
    onError: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val future = ProcessCameraProvider.getInstance(ctx)
                    future.addListener({
                        runCatching {
                            val provider = future.get()
                            val preview = Preview.Builder().build()
                                .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                            val capture = ImageCapture.Builder().build()
                            imageCapture = capture
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture,
                            )
                        }.onFailure { onError() }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
            )

            // Close
            Box(
                Modifier.padding(20.dp).size(40.dp).clip(CircleShape)
                    .background(EgyptColors.NightCard).clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) { Text("✕", color = EgyptColors.GoldBright) }

            // Shutter
            Column(
                Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Capture the royal portrait", color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
                Box(
                    Modifier.size(74.dp).clip(CircleShape).background(EgyptColors.GoldBright)
                        .clickable {
                            val capture = imageCapture ?: return@clickable
                            capture.takePicture(
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        scope.launch(Dispatchers.IO) {
                                            val bytes = image.toJpegBytes()
                                            image.close()
                                            withContext(Dispatchers.Main) { onCaptured(bytes) }
                                        }
                                    }

                                    override fun onError(exc: ImageCaptureException) { onError() }
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(Modifier.size(60.dp).clip(CircleShape).background(EgyptColors.GoldDeep))
                }
            }
        }
    }
}

/** Convert a captured JPEG ImageProxy to upright JPEG bytes (off the main thread). */
private fun ImageProxy.toJpegBytes(): ByteArray {
    val buffer = planes[0].buffer
    val raw = ByteArray(buffer.remaining()).also { buffer.get(it) }
    val rotation = imageInfo.rotationDegrees
    if (rotation == 0) return raw
    val bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.size) ?: return raw
    val rotated = Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height,
        Matrix().apply { postRotate(rotation.toFloat()) }, true,
    )
    return ByteArrayOutputStream().use { out ->
        rotated.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.toByteArray()
    }
}
