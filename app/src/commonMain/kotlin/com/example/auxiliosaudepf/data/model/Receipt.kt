package com.example.auxiliosaudepf.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    val id: Long = 0,
    val imagePath: String,
    val ocrText: String,
    val categoryId: Long,
    val timestamp: Long,
    val amount: Double = 0.0
)
