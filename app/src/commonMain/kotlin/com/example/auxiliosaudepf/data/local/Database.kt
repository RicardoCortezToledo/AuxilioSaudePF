package com.example.auxiliosaudepf.data.local

import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory

interface Database {
    fun getAllCategories(): List<ReceiptCategory>
    fun insertReceipt(receipt: Receipt): Long
    fun getAllReceipts(): List<Receipt>
    fun deleteReceipt(id: Long): Int
    fun deleteAllReceipts(): Int
}
