package com.example.auxiliosaudepf.data

import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun getCategories(): List<ReceiptCategory>
    fun getReceipts(): Flow<List<Receipt>>
    fun addReceipt(imagePath: String, ocrText: String, amount: Double = 0.0): Receipt
    fun classifyOcrText(ocrText: String): ReceiptCategory
    fun deleteReceipt(id: Long)
    fun deleteAllReceipts()
}
