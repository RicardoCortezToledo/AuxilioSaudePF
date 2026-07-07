package com.example.auxiliosaudepf.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptCategory(
    val id: Long = 0,
    val name: String,
    val description: String,
    val keywords: List<String>
)
