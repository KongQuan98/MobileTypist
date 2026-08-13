package org.example.project.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.popoverPresentationController

actual fun shareText(text: String) {
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    val activityController = UIActivityViewController(listOf(text), null)

    // For iPad compatibility
    activityController.popoverPresentationController?.sourceView = rootViewController?.view

    rootViewController?.presentViewController(activityController, true, null)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun shareImage(image: ImageBitmap, text: String) {
    val skiaBitmap = image.asSkiaBitmap()
    val data =
        org.jetbrains.skia.Image.makeFromBitmap(skiaBitmap).encodeToData(EncodedImageFormat.PNG)
            ?: return
    val bytes = data.bytes
    val nsData = bytes.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
    }
    val uiImage = UIImage.imageWithData(nsData) ?: return

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    val activityController = UIActivityViewController(listOf(uiImage, text), null)

    // For iPad compatibility
    activityController.popoverPresentationController?.sourceView = rootViewController?.view

    rootViewController?.presentViewController(activityController, true, null)
}
