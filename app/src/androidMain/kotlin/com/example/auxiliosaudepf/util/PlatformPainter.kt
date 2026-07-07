package com.example.auxiliosaudepf.util

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberPlatformPainter(pathOrUri: String?): Painter? {
    if (pathOrUri.isNullOrEmpty()) return null
    val context = LocalContext.current
    return remember(pathOrUri) {
        try {
            val bitmap = if (pathOrUri.startsWith("content://")) {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(pathOrUri))
                val b = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                b
            } else {
                BitmapFactory.decodeFile(pathOrUri)
            }
            bitmap?.asImageBitmap()?.let { BitmapPainter(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
