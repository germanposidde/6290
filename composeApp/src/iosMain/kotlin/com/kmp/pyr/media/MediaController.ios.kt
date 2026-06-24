@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.kmp.pyr.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSData
import platform.Foundation.NSItemProvider
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

actual fun decodeToImageBitmap(bytes: ByteArray): ImageBitmap? =
    runCatching { Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()

@Composable
actual fun rememberMediaController(
    onImagePicked: (ByteArray) -> Unit,
    onCameraDenied: () -> Unit,
): MediaController {
    // UIKit holds delegates weakly, so retain them across recompositions.
    val galleryDelegate = remember { GalleryDelegate(onImagePicked) }
    val cameraDelegate = remember { CameraDelegate(onImagePicked) }

    return remember {
        object : MediaController {
            override fun pickFromGallery() {
                val config = PHPickerConfiguration().apply {
                    setSelectionLimit(1)
                    setFilter(PHPickerFilter.imagesFilter())
                }
                val picker = PHPickerViewController(configuration = config)
                picker.delegate = galleryDelegate
                present(picker)
            }

            override fun captureFromCamera() {
                when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
                    AVAuthorizationStatusAuthorized -> presentCamera(cameraDelegate, onCameraDenied)
                    AVAuthorizationStatusNotDetermined ->
                        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                            dispatch_async(dispatch_get_main_queue()) {
                                if (granted) presentCamera(cameraDelegate, onCameraDenied) else onCameraDenied()
                            }
                        }
                    else -> onCameraDenied()
                }
            }
        }
    }
}

private fun presentCamera(delegate: CameraDelegate, onDenied: () -> Unit) {
    if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
        onDenied(); return
    }
    val picker = UIImagePickerController()
    picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    picker.delegate = delegate
    present(picker)
}

/** Defer to the next runloop so the chooser's transient window is gone first. */
private fun present(vc: UIViewController) = dispatch_async(dispatch_get_main_queue()) {
    topViewController()?.presentViewController(vc, animated = true, completion = null)
}

private fun topViewController(): UIViewController? {
    val app = UIApplication.sharedApplication
    val window = app.keyWindow ?: (app.windows.firstOrNull() as? UIWindow)
    var top = window?.rootViewController
    while (top?.presentedViewController != null) top = top.presentedViewController
    return top
}

private class GalleryDelegate(
    private val onImagePicked: (ByteArray) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, null)
        val result = didFinishPicking.firstOrNull() as? PHPickerResult ?: return
        val provider: NSItemProvider = result.itemProvider
        val typeId = (provider.registeredTypeIdentifiers.firstOrNull() as? String) ?: "public.image"
        provider.loadDataRepresentationForTypeIdentifier(typeId) { data, _ ->
            val bytes = data?.let { normalizeToJpeg(it) }
            if (bytes != null) dispatch_async(dispatch_get_main_queue()) { onImagePicked(bytes) }
        }
    }
}

private class CameraDelegate(
    private val onImagePicked: (ByteArray) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        picker.dismissViewControllerAnimated(true, null)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage ?: return
        val jpeg = UIImageJPEGRepresentation(image, 0.9) ?: return
        val bytes = jpeg.toByteArray()
        dispatch_async(dispatch_get_main_queue()) { onImagePicked(bytes) }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }
}

private fun normalizeToJpeg(data: NSData): ByteArray? {
    val image = UIImage(data = data)
    val jpeg = UIImageJPEGRepresentation(image, 0.9) ?: return null
    return jpeg.toByteArray()
}

private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).apply {
        usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    }
}
