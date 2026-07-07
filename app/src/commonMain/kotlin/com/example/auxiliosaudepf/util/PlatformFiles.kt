package com.example.auxiliosaudepf.util

interface PlatformFiles {
    fun deleteFile(path: String)
    fun copyFileToInternalStorage(uriOrPath: String, prefix: String, extension: String): String
}
