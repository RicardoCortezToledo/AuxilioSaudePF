package com.example.auxiliosaudepf.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
class IosPlatformFiles : PlatformFiles {
    private val fileManager = NSFileManager.defaultManager

    private fun getDocumentsDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        return (paths.firstOrNull() as? String) ?: ""
    }

    override fun deleteFile(path: String) {
        try {
            fileManager.removeItemAtPath(path, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun copyFileToInternalStorage(uriOrPath: String, prefix: String, extension: String): String {
        return try {
            val destDir = getDocumentsDirectory()
            val fileName = "${prefix}_${NSDate().timeIntervalSince1970.toLong()}.$extension"
            val destPath = "$destDir/$fileName"
            fileManager.copyItemAtPath(uriOrPath, destPath, null)
            destPath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
