package com.example.auxiliosaudepf.util

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberFilePicker(
    onResult: (pathOrUri: String, isPdf: Boolean) -> Unit
): PlatformImagePicker {
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            val uri = tempPhotoUri
            if (success && uri != null) {
                onResult(uri.toString(), false)
            }
        }
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                val isPdf = context.contentResolver.getType(uri) == "application/pdf" ||
                        uri.path?.endsWith(".pdf", ignoreCase = true) == true
                onResult(uri.toString(), isPdf)
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                try {
                    val tempFile = File.createTempFile("captured_receipt_", ".jpg", context.cacheDir).apply {
                        createNewFile()
                        deleteOnExit()
                    }
                    val uri = FileProvider.getUriForFile(
                        context,
                        "com.example.auxiliosaudepf.fileprovider",
                        tempFile
                    )
                    tempPhotoUri = uri
                    cameraLauncher.launch(uri)
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro ao abrir câmera: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_LONG).show()
            }
        }
    )

    return remember {
        object : PlatformImagePicker {
            override fun launchCamera() {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }

            override fun launchGallery() {
                try {
                    filePickerLauncher.launch(arrayOf("image/*", "application/pdf"))
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro ao abrir seletor: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
