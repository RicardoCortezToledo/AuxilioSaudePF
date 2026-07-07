package com.example.auxiliosaudepf.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.*
import platform.PDFKit.*
import platform.UIKit.UIImageJPEGRepresentation
import platform.Vision.*

class IosDocumentProcessor : DocumentProcessor {
    override fun extractTextFromPdf(uriOrPath: String): String {
        return try {
            val url = NSURL.fileURLWithPath(uriOrPath)
            val doc = PDFDocument(url) ?: return ""
            val sb = StringBuilder()
            val pageCount = doc.pageCount()
            for (i in 0 until pageCount) {
                val page = doc.pageAtIndex(i.toLong())
                page?.string()?.let { sb.append(it).append("\n") }
            }
            sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun runOcr(imagePath: String, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        try {
            val url = NSURL.fileURLWithPath(imagePath)
            val handler = VNImageRequestHandler(url = url, options = null)
            var textFound = ""
            val request = VNRecognizeTextRequest { request, error ->
                if (error == null && request != null) {
                    val results = request.results() as? List<*>
                    if (results != null) {
                        textFound = results.joinToString("\n") { obs ->
                            val observation = obs as? VNRecognizedTextObservation
                            val candidate = observation?.topCandidates(1u)?.firstOrNull() as? VNRecognizedText
                            candidate?.string() ?: ""
                        }
                    }
                }
            }
            request.setRecognitionLevel(VNRequestTextRecognitionLevel.VNRequestTextRecognitionLevelAccurate)
            handler.performRequests(listOf(request), null)
            onSuccess(textFound)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun renderPdfFirstPage(uriOrPath: String): String? {
        return try {
            val url = NSURL.fileURLWithPath(uriOrPath)
            val doc = PDFDocument(url) ?: return null
            val page = doc.pageAtIndex(0) ?: return null
            val uiImage = page.thumbnailOfSize(CGSizeMake(720.0, 1080.0), PDFDisplayBox.PDFDisplayBoxMediaBox) ?: return null
            
            val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            val destDir = (paths.firstOrNull() as? String) ?: ""
            val fileName = "pdf_preview_${NSDate().timeIntervalSince1970.toLong()}.jpg"
            val destPath = "$destDir/$fileName"
            
            val data = UIImageJPEGRepresentation(uiImage, 0.8)
            if (data != null && data.writeToFile(destPath, true)) {
                destPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
