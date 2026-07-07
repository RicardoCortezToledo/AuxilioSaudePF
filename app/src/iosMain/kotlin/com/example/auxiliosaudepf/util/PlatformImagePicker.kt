package com.example.auxiliosaudepf.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject

class DocumentPickerDelegate(
    private val onResult: (String, Boolean) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (url != null) {
            val path = url.path
            if (path != null) {
                val isPdf = path.endsWith(".pdf", ignoreCase = true)
                onResult(path, isPdf)
            }
        }
    }
}

class ImagePickerDelegate(
    private val onResult: (String, Boolean) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<*, *>) {
        val imageURL = didFinishPickingMediaWithInfo[UIImagePickerControllerImageURL] as? NSURL
        val path = imageURL?.path
        if (path != null) {
            onResult(path, false)
        }
        picker.dismissViewControllerAnimated(true, null)
    }
    
    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }
}

@Composable
actual fun rememberFilePicker(
    onResult: (pathOrUri: String, isPdf: Boolean) -> Unit
): PlatformImagePicker {
    val docDelegate = remember { DocumentPickerDelegate(onResult) }
    val imgDelegate = remember { ImagePickerDelegate(onResult) }

    return remember {
        object : PlatformImagePicker {
            override fun launchCamera() {
                val rootVc = UIApplication.sharedApplication.keyWindow?.rootViewController
                if (rootVc != null) {
                    if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                        val imagePicker = UIImagePickerController().apply {
                            this.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                            this.delegate = imgDelegate
                        }
                        rootVc.presentViewController(imagePicker, animated = true, completion = null)
                    } else {
                        // Camera not available (e.g. simulator), fallback to document picker/gallery
                        launchGallery()
                    }
                }
            }

            override fun launchGallery() {
                val rootVc = UIApplication.sharedApplication.keyWindow?.rootViewController
                if (rootVc != null) {
                    val documentPicker = UIDocumentPickerViewController(
                        documentTypes = listOf("public.image", "com.adobe.pdf"),
                        inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
                    ).apply {
                        this.delegate = docDelegate
                    }
                    rootVc.presentViewController(documentPicker, animated = true, completion = null)
                }
            }
        }
    }
}
