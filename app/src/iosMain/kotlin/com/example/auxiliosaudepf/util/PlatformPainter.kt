package com.example.auxiliosaudepf.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import org.jetbrains.compose.resources.decodeToImageBitmap
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberPlatformPainter(pathOrUri: String?): Painter? {
    if (pathOrUri.isNullOrEmpty()) return null
    return remember(pathOrUri) {
        try {
            val nsData = NSData.dataWithContentsOfFile(pathOrUri)
            if (nsData != null) {
                val byteArray = ByteArray(nsData.length.toInt())
                nsData.bytes?.let { bytes ->
                    platform.posix.memcpy(byteArray.refTo(0), bytes, nsData.length)
                }
                val imageBitmap = byteArray.decodeToImageBitmap()
                BitmapPainter(imageBitmap)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
