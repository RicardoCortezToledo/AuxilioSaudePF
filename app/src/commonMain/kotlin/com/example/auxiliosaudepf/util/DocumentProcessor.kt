package com.example.auxiliosaudepf.util

interface DocumentProcessor {
    fun extractTextFromPdf(uriOrPath: String): String
    fun runOcr(imagePath: String, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit)
    fun renderPdfFirstPage(uriOrPath: String): String? // returns path to rendered page thumbnail
}
