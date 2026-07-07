package com.example.auxiliosaudepf.util

import androidx.compose.runtime.Composable

interface PlatformImagePicker {
    fun launchCamera()
    fun launchGallery()
}

@Composable
expect fun rememberFilePicker(
    onResult: (pathOrUri: String, isPdf: Boolean) -> Unit
): PlatformImagePicker
