package com.example.auxiliosaudepf.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class AndroidPlatformFiles(private val context: Context) : PlatformFiles {
    override fun deleteFile(path: String) {
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun copyFileToInternalStorage(uriOrPath: String, prefix: String, extension: String): String {
        return try {
            val uri = Uri.parse(uriOrPath)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
            val fileName = "${prefix}_${System.currentTimeMillis()}.$extension"
            val outFile = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(outFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            outFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
