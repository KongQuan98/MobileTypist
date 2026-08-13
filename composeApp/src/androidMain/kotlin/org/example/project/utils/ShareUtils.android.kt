package org.example.project.utils

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import org.example.project.data.storage.appContext
import java.io.File
import java.io.FileOutputStream

actual fun shareText(text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    appContext.startActivity(shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

actual fun shareImage(image: ImageBitmap, text: String) {
    val bitmap = image.asAndroidBitmap()
    val cachePath = File(appContext.cacheDir, "shared_images")
    cachePath.mkdirs()
    val file = File(cachePath, "share_result.png")
    val fileOutputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    fileOutputStream.flush()
    fileOutputStream.close()

    val contentUri = FileProvider.getUriForFile(
        appContext,
        "${appContext.packageName}.fileprovider",
        file
    )

    if (contentUri != null) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, "image/png")
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Result")
        appContext.startActivity(chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
