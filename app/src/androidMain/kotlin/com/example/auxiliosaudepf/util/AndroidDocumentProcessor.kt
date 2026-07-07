package com.example.auxiliosaudepf.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File
import java.io.FileOutputStream

class AndroidDocumentProcessor(private val context: Context) : DocumentProcessor {
    override fun extractTextFromPdf(uriOrPath: String): String {
        var reader: PdfReader? = null
        return try {
            val uri = Uri.parse(uriOrPath)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
            reader = PdfReader(inputStream)
            val sb = java.lang.StringBuilder()
            val numPages = reader.numberOfPages
            for (i in 1..numPages) {
                val pageText = PdfTextExtractor.getTextFromPage(reader, i)
                sb.append(pageText).append("\n")
            }
            sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun runOcr(imagePath: String, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        try {
            val uri = Uri.parse(imagePath)
            val bitmap = if (imagePath.startsWith("content://")) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val b = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                b
            } else {
                BitmapFactory.decodeFile(imagePath)
            }
            if (bitmap == null) {
                onFailure(Exception("Não foi possível carregar o bitmap para OCR"))
                return
            }
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onSuccess(visionText.text)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun renderPdfFirstPage(uriOrPath: String): String? {
        return try {
            val uri = Uri.parse(uriOrPath)
            val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
            val renderer = PdfRenderer(pfd)
            if (renderer.pageCount > 0) {
                val page = renderer.openPage(0)
                val bitmap = Bitmap.createBitmap(720, 1080, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                renderer.close()
                pfd.close()
                
                // Save rendered bitmap to a temp file and return its path
                val tempFile = File.createTempFile("pdf_preview_", ".jpg", context.cacheDir).apply {
                    deleteOnExit()
                }
                val outStream = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
                outStream.close()
                tempFile.absolutePath
            } else {
                renderer.close()
                pfd.close()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
